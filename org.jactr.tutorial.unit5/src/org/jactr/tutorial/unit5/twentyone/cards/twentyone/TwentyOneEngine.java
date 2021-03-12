package org.jactr.tutorial.unit5.twentyone.cards.twentyone;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.ICardGameListener;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.IShoe;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.DefaultAction;
import org.jactr.tutorial.unit5.twentyone.cards.impl.Shoe;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneListeners;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities.Sum;

public class TwentyOneEngine implements ICardEngine {

	public enum Result {
		BLACKJACK, WIN, PUSH, LOSE, BUST
	};

	static public final IAction HIT = new DefaultAction("hit", 'h', null);
	static public final IAction STAND = new DefaultAction("stand", 'd', null);

	protected final Set<IAction> AVAILABLE_ACTIONS = new HashSet<>();
	protected final int _initialBet;
	protected final TwentyOneListeners _listeners = new TwentyOneListeners();

	public TwentyOneEngine(int initialBet) {
		AVAILABLE_ACTIONS.add(HIT);
		AVAILABLE_ACTIONS.add(STAND);
		_initialBet = initialBet;
	}

	@Override
	public void playHand(ITable table) {

		_listeners.newHand(this, table.getDealer(), table.getPlayers().toArray(new IPlayer[0]));

		// initial dealing
		table.getPlayers().forEach((player) -> {
			// initial bets
			player.payout(-_initialBet);
			table.bet(player, _initialBet);
			_listeners.bet(player, _initialBet);

			ICard card = table.getShoe().card();
			player.getHand().add(card);
			player.getVisibleHand().add(card);
			_listeners.dealt(player, card);
		});

		// face down dealer card
		IDealer dealer = table.getDealer();
		ICard dCard = table.getShoe().card();
		dealer.getHand().add(dCard);
		_listeners.dealt(dealer, dCard);

		// second card
		table.getPlayers().forEach((player) -> {
			ICard card = table.getShoe().card();
			player.getHand().add(card);
			player.getVisibleHand().add(card);
			_listeners.dealt(player, card);
		});

		dCard = table.getShoe().card();
		dealer.getHand().add(dCard);
		dealer.getVisibleHand().add(dCard);
		_listeners.dealt(dealer, dCard);

		/*
		 * each player gets their turn
		 */
		table.getPlayers().forEach((player) -> {
			_listeners.turn(player, AVAILABLE_ACTIONS);
			if (!checkForEarlyExit(player))
				while (playHand(player, table))
					if (checkForEarlyExit(player))
						break;
		});

		/*
		 * and the flip and dealer's turn
		 */
		_listeners.turn(dealer, AVAILABLE_ACTIONS);
		dealer.reveal();
		_listeners.reveal(dealer);
		if (!checkForEarlyExit(dealer))
			while (playHand(dealer, table))
				if (checkForEarlyExit(dealer))
					break;

		table.getPlayers().forEach((player) -> {
			payout(score(dealer, player), player, table);
		});

		// clear the table
		table.getPlayers().forEach((player) -> {
			player.getHand().clear();
			player.getVisibleHand().clear();
		});

		dealer.getVisibleHand().clear();
		dealer.getHand().clear();
	}

	/**
	 * did they bust or 21
	 */
	protected boolean checkForEarlyExit(IPlayer player) {
		Sum playerSum = TwentyOneUtilities.greatestNotBusting(TwentyOneUtilities.sums(player.getHand()));
		boolean bust = playerSum == null;
		boolean hit = playerSum != null && playerSum.value() == 21;

		if (bust)
			_listeners.bust(player);

		if (hit)
			_listeners.twentyOne(player);

		return bust || hit;
	}

	protected void payout(Result result, IPlayer player, ITable table) {
		int payout = 0;

		switch (result) {
		case BLACKJACK:
			payout = table.getBet(player) + table.getBet(player) / 2 * 3; // 3:2
			break;
		case WIN:
			payout = 2 * table.getBet(player);
			break;
		case PUSH:
			payout = table.getBet(player);
			break;
		case BUST: 	
		case LOSE:
			payout = 0;
			break;
		}

		player.payout(payout);
		_listeners.payout(player, result, payout);
	}

      public Result score(IDealer dealer, IPlayer player) {
		Sum dealerSum = TwentyOneUtilities.greatestNotBusting(TwentyOneUtilities.sums(dealer.getHand()));
		Sum playerSum = TwentyOneUtilities.greatestNotBusting(TwentyOneUtilities.sums(player.getHand()));
		// bust
		if (playerSum == null)
			return Result.BUST;

		// if dealer busts, everyone wins
		if (dealerSum == null) {
			if (playerSum.value() == 21)
				return Result.BLACKJACK;
			return Result.WIN;
		}

		// beat
		if (playerSum.value() > dealerSum.value()) {
			if (playerSum.value() == 21)
				return Result.BLACKJACK;
			return Result.WIN;
		}
		// tie
		if (playerSum.value() == dealerSum.value()) {
			return Result.PUSH;
		}

		// lose
		return Result.LOSE;
	}

	protected boolean playHand(final IPlayer player, final ITable table) {
		/*
		 * there is some decision time
		 */
		CompletableFuture<IAction> actionToTake = player.turn(table, AVAILABLE_ACTIONS);

		CompletableFuture<Boolean> andThen = actionToTake.thenApply((action) -> {
			return takeAction(table, player, action);
		});

		try {
			return andThen.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param table
	 * @param player
	 * @param action
	 * @return true if we will have another turn
	 */
	protected boolean takeAction(ITable table, IPlayer player, IAction action) {
		if (STAND.equals(action)) {
			_listeners.stand(player);
			return false;
		}
		if (HIT.equals(action)) {
			ICard card = table.getShoe().card();
			player.getHand().add(card);
			player.getVisibleHand().add(card);
			_listeners.hit(player);
			_listeners.dealt(player, card);
			return true;
		}
		return false;
	}

	public IShoe newShoe() {
		IShoe shoe = new Shoe(5, true);
		shoe.shuffle();
		return shoe;
	}

	@Override
	public void addListener(ICardGameListener listener) {
		if (listener instanceof ITwentyOneListener)
			_listeners.add((ITwentyOneListener) listener);
	}

	@Override
	public void removeListener(ICardGameListener listener) {
		_listeners.remove(listener);
	}

}
