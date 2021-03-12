package org.jactr.tutorial.unit5.twentyone.cards.twentyone;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.AbstractPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities.Sum;

/**
 * hits on soft 17
 * 
 * @author harrison
 *
 */
public class TwentyOneDealer extends AbstractPlayer implements IDealer {

	public TwentyOneDealer() {
		super("Dealer", 0);
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
		if (value >= 18)
			return TwentyOneEngine.STAND;

		// if <=16, hit
		if (value <= 16)
			return TwentyOneEngine.HIT;

		// all that's left is 17, is it soft?
		if (largestOrTwentyOne.isSoft())
			return TwentyOneEngine.HIT;
		else
			return TwentyOneEngine.STAND;
	}
}
