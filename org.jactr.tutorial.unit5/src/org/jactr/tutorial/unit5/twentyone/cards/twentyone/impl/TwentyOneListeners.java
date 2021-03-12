package org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.ICardGameListener;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.ITwentyOneListener;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;

public class TwentyOneListeners implements ITwentyOneListener {

	private Collection<ITwentyOneListener> _listeners = new ArrayList<>();

	public TwentyOneListeners() {
	}

	public void add(ITwentyOneListener listener) {
		_listeners.add(listener);
	}

	public void remove(ICardGameListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void dealt(IPlayer player, ICard card) {
		_listeners.forEach((l) -> {
			l.dealt(player, card);
		});
	}

	@Override
	public void hit(IPlayer player) {
		_listeners.forEach((l) -> {
			l.hit(player);
		});
	}

	@Override
	public void stand(IPlayer player) {
		_listeners.forEach((l) -> {
			l.stand(player);
		});

	}

	@Override
	public void turn(IPlayer player, Set<IAction> possibleActions) {
		_listeners.forEach((l) -> {
			l.turn(player, possibleActions);
		});

	}

	@Override
	public void payout(IPlayer player, Result result, int payout) {
		_listeners.forEach((l) -> {
			l.payout(player, result, payout);
		});
	}

	@Override
	public void bet(IPlayer player, int bet) {
		_listeners.forEach((l) -> {
			l.bet(player, bet);
		});
	}

	@Override
	public void reveal(IPlayer player) {
		_listeners.forEach((l) -> {
			l.reveal(player);
		});
	}

	@Override
	public void bust(IPlayer player) {
		_listeners.forEach((l) -> {
			l.bust(player);
		});
		
	}

	@Override
	public void twentyOne(IPlayer player) {
		_listeners.forEach((l) -> {
			l.twentyOne(player);
		});
		
	}

	@Override
	public void newHand(ICardEngine engine, IDealer dealer, IPlayer[] players) {
		_listeners.forEach(l -> l.newHand(engine, dealer, players));
		
	}

}
