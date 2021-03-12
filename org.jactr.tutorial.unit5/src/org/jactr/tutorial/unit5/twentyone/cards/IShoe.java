package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.stream.Stream;

public interface IShoe {

	public void add(IDeck deck);
	public void add(Stream<ICard> cards);
	public void shuffle();
	public boolean isEmpty();
	public ICard card();
}
