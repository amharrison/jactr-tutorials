package org.jactr.tutorial.unit5.twentyone.cards.impl;

import java.util.ArrayList;
import java.util.List;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;

abstract public class AbstractPlayer implements IPlayer {

	private final List<ICard> _hand;
	private final List<ICard> _visibleHand;
	private int _cash;
	private String _name;

	public AbstractPlayer(String name, int bank) {
		_name = name;
		_hand = new ArrayList<ICard>(5);
		_visibleHand = new ArrayList<ICard>(5);
		_cash = bank;
	}
	
	public String getName() {
		return _name;
	}

	@Override
	public List<ICard> getHand() {
		return _hand;
	}

	@Override
	public List<ICard> getVisibleHand() {
		return _visibleHand;
	}

	public void reveal() {
		_visibleHand.clear();
		_visibleHand.addAll(_hand);
	}

	public void payout(int amount) {
		_cash += amount;
	}

	public int getCash() {
		return _cash;
	}
}
