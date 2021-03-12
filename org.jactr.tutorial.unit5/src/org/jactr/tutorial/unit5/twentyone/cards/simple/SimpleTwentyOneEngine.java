package org.jactr.tutorial.unit5.twentyone.cards.simple;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities.Sum;

public class SimpleTwentyOneEngine extends TwentyOneEngine {

	public SimpleTwentyOneEngine() {
		super(0);
	}

	@Override
	public void playHand(ITable table) {
		IDealer dealer = table.getDealer();
		
		// clear the table
		table.getPlayers().forEach((player) -> {
			player.getHand().clear();
			player.getVisibleHand().clear();
		});

		dealer.getVisibleHand().clear();
		dealer.getHand().clear();

		_listeners.newHand(this, table.getDealer(), table.getPlayers().toArray(new IPlayer[0]));

		// initial dealing
		table.getPlayers().forEach((player) -> {
			ICard card = table.getShoe().card();
			player.getHand().add(card);
			_listeners.dealt(player, card);
		});

		// face down dealer card
		ICard dCard = table.getShoe().card();
		dealer.getHand().add(dCard);
		_listeners.dealt(dealer, dCard);

		// second card face up
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
			playHand(player, table);
		});

		/*
		 * and dealer's turn
		 */
		playHand(dealer, table);
		_listeners.turn(dealer, AVAILABLE_ACTIONS);

		dealer.reveal();
		_listeners.reveal(dealer);

		table.getPlayers().forEach((player) -> {
			player.reveal();
			_listeners.reveal(player);

			payout(score(dealer, player), player, table);
		});

	}

	protected boolean takeAction(ITable table, IPlayer player, IAction action) {
		super.takeAction(table, player, action);
		return false; // we only allow one action.
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

}
