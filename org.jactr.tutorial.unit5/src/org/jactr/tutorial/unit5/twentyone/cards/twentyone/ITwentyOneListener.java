package org.jactr.tutorial.unit5.twentyone.cards.twentyone;

import org.jactr.tutorial.unit5.twentyone.cards.ICardGameListener;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;

public interface ITwentyOneListener extends ICardGameListener {

	
	
	/**
	 * player requests hit
	 * @param player
	 */
	public void hit(IPlayer player);
	
	/**
	 * player stands
	 * @param player
	 */
	public void stand(IPlayer player);
	
	public void reveal(IPlayer player);
	
	public void bust(IPlayer player);
	
	public void twentyOne(IPlayer player);
	
	
	/**
	 * final result, payout includes table bet.
	 */
	public void payout(IPlayer player, Result result, int payout);
	
}
