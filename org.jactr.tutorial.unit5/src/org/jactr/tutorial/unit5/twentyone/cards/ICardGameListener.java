package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.Set;

public interface ICardGameListener {

	public void newHand(ICardEngine engine, IDealer dealer, IPlayer[] players);
	/**
	 * player's turn
	 * @param player
	 */
	public void turn(IPlayer player, Set<IAction> possibleActions);
	/**
	 * called for each player, including dealer
	 * @param player
	 */
	public void dealt(IPlayer player, ICard card);
	
	public void bet(IPlayer player, int bet);
}
