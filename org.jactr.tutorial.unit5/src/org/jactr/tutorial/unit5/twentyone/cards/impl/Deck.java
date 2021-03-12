package org.jactr.tutorial.unit5.twentyone.cards.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IDeck;
import org.jactr.tutorial.unit5.twentyone.cards.Rank;
import org.jactr.tutorial.unit5.twentyone.cards.Suit;

public class Deck implements IDeck {

	public Deck() {
	}

	@Override
	public Stream<ICard> cards() {
		Collection<ICard> cards = new ArrayList<ICard>(52);

		for (Suit suite : Suit.values())
			for (Rank rank : Rank.values())
				cards.add(new Card(rank, suite));

		return cards.stream();
	}

}
