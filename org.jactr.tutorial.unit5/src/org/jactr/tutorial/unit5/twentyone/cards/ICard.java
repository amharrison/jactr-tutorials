package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.UUID;

public interface ICard {

	public Rank getRank();
	public Suit getSuit();
	public UUID getID(); //unique card id
}
