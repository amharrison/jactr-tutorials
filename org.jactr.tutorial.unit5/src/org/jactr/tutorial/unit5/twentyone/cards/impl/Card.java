package org.jactr.tutorial.unit5.twentyone.cards.impl;

import java.util.Objects;
import java.util.UUID;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.Rank;
import org.jactr.tutorial.unit5.twentyone.cards.Suit;

public class Card implements ICard {

	@Override
	public int hashCode() {
		return Objects.hash(_id, _rank, _suite);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		return Objects.equals(_id, other._id) && _rank == other._rank && _suite == other._suite;
	}

	private final Rank _rank;
	private final Suit _suite;
	private final UUID _id;

	public Card(Rank rank, Suit suite) {
		_rank = rank;
		_suite = suite;
		_id = UUID.randomUUID();
	}

	public Rank getRank() {
		return _rank;
	}

	public Suit getSuit() {
		return _suite;
	}
	
	public UUID getID() {
		return _id;
	}

	public String toString() {
		String suite = "♠";
		switch (getSuit()) {
		case CLUBS:
			suite = "♣";
			break;
		case DIAMONDS:
			suite = "♦";
			break;
		case HEARTS:
			suite = "♥";
			break;
		case SPADES:
			suite = "♠";
			break;
		}
		
		String rank = "";
		Rank r = getRank();
		if(r.getValue()<10)
			rank = rank+r.getValue();
		else
		{
			if(r!=Rank.TEN)
				rank = ""+r.name().charAt(0);
			else
				rank = "10";
		}

		return String.format("%s%s", rank, suite);
	}
}
