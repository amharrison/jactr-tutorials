package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IPlayer {

	public String getName();
	
	public List<ICard> getHand();
	
	public List<ICard> getVisibleHand();
	
	CompletableFuture<IAction> turn(ITable table, Set<IAction> possibleActions);
	
	/**
	 * makes hidden cards visible
	 */
	public void reveal();
	
	/**
	 * increment (or decrement) available cash
	 * a negative payout is called when betting
	 * @param amount
	 */
	public void payout(int amount);
	
	public int getCash();
}
