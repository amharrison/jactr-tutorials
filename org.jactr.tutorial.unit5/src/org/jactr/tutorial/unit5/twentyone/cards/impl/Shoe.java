package org.jactr.tutorial.unit5.twentyone.cards.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IDeck;
import org.jactr.tutorial.unit5.twentyone.cards.IShoe;

/**
 * multi deck shoe
 * 
 * @author harrison
 *
 */
public class Shoe implements IShoe {

	private final List<ICard> _cards;
	private int _initialDecks;
	private boolean _autoRefill;

	public Shoe(int decks, boolean autoRefill) {
		_initialDecks = decks;
		_autoRefill = autoRefill;
		_cards = new ArrayList<ICard>(52 * decks);
		for (int i = 0; i < decks; i++)
			add(new Deck());
	}

	@Override
	public void add(IDeck deck) {
		deck.cards().collect(Collectors.toCollection(() -> {
			return _cards;
		}));
	}

	@Override
	public void shuffle() {
		Collections.shuffle(_cards);

	}

	public boolean isEmpty() {
		return _cards.isEmpty();
	}

	public ICard card() {
		// remove from the back of the deck for efficency's sake
		if (_cards.size() == 0)
			if (_autoRefill) {
				for (int i = 0; i < _initialDecks; i++)
					add(new Deck());
				shuffle();
			} else
				return null;

		return _cards.remove(_cards.size() - 1);
	}

	@Override
	public void add(Stream<ICard> cards) {
		cards.collect(Collectors.toCollection(() -> {
			return _cards;
		}));
	}

}
