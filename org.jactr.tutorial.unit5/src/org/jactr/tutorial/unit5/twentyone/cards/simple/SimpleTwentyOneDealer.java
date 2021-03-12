package org.jactr.tutorial.unit5.twentyone.cards.simple;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.AbstractPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities.Sum;

/**
 * hits on soft 17
 * 
 * @author harrison
 *
 */
public class SimpleTwentyOneDealer extends AbstractPlayer implements IDealer {

	
	private int _standOn = 15;
	
	public SimpleTwentyOneDealer(int standOn) {
		super("Dealer", 0);
		_standOn = standOn;
	}

	@Override
	public CompletableFuture<IAction> turn(ITable table, Set<IAction> possibleActions) {
		return CompletableFuture.completedFuture(playHand(table,possibleActions));
	}

	protected IAction playHand(ITable table, Set<IAction> possibleActions) {
		List<ICard> hand = table.getDealer().getHand();

		Collection<Sum> sums = TwentyOneUtilities.sums(hand);
		Sum largestOrTwentyOne = TwentyOneUtilities.greatestNotBusting(sums);
		int value = largestOrTwentyOne.value();

		// if >= 18, stand, includes 21 and bust
		if (value >= _standOn)
			return TwentyOneEngine.STAND;
		else
			return TwentyOneEngine.HIT;
	}
}
