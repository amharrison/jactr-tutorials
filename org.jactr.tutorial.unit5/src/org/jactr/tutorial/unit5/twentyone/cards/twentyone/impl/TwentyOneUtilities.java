package org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.Rank;

public class TwentyOneUtilities {

	static public final boolean hasAce(Collection<ICard> hand) {
		return hand.stream().filter((card) -> {
			return card.getRank().equals(Rank.ACE);
		}).findAny().isPresent();
	}

	/*
	 * 
	 */
	static public final Collection<Sum> sums(List<ICard> hand) {
		Collection<Sum> allSums = new ArrayList<>();
		calculateSum(hand, allSums);
		return allSums;
	}

	static private Collection<Sum> calculateSum(List<ICard> hand, Collection<Sum> allSums) {
		if (hand.size() == 0) {
			Sum currentSum = new Sum();
			allSums.add(currentSum);
			return Collections.singleton(currentSum);
		}

		ICard card = hand.get(0);
		// sublist for next call
		hand = hand.subList(1, hand.size());

		if (card.getRank().equals(Rank.ACE)) {
			Collection<Sum> softSums = calculateSum(hand, allSums);
			for (Sum currentSum : softSums) {
				currentSum._sum += card.getRank().getValue(); // 11
				currentSum._isSoft = true;
			}
			
			Collection<Sum> hardSums = calculateSum(hand, allSums);
			for (Sum currentSum : hardSums) {
				currentSum._sum += 1; 
			}
			Collection<Sum> sums = new ArrayList<Sum>();
			sums.addAll(softSums);
			sums.addAll(hardSums);
			
			return sums;
		} else {
			Collection<Sum> currentSums = calculateSum(hand, allSums);
			for (Sum currentSum : currentSums)
				currentSum._sum += card.getRank().getValue();
			return currentSums;
		}
	}
	
	/**
	 * 
	 * @param sums
	 * @return null if bust
	 */
	static public final Sum greatestNotBusting(Collection<Sum> sums)
	{
		int maxSum = 0;
		Sum candidate = null;
		for (Sum sum : sums) {
			int sValue = sum.value();
			if(sValue > 21) continue;
			
			if (sValue > maxSum)  {
				candidate = sum;
				maxSum = sValue;
			}
			if(sValue == 21)
				return sum;
		}
		return candidate;
	}

	static public final Sum smallestOrTwentyOne(Collection<Sum> sums) {
		int minSum = Integer.MAX_VALUE;
		Sum candidate = null;
		for (Sum sum : sums) {
			int sValue = sum.value();
			if (sValue < minSum)  {
				candidate = sum;
				minSum = sValue;
			}
			if(sValue == 21)
				return sum;
		}
		return candidate;
	}

	static public class Sum {
		int _sum;
		boolean _isSoft;

		public int value() {
			return _sum;
		}

		public boolean isSoft() {
			return _isSoft;
		}
	}
}
