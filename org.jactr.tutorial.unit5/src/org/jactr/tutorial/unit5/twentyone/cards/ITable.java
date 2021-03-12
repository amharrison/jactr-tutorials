package org.jactr.tutorial.unit5.twentyone.cards;

import java.util.List;
import java.util.Stack;

public interface ITable {

	public List<IPlayer> getPlayers();
	
	public IDealer getDealer();
	
	public void playHand();

	public IShoe getShoe();
	
	public int getBet(IPlayer player);
	
	public void bet(IPlayer player, int betIncrement);
	
	public Stack<ICard> faceUpDiscard();
	public Stack<ICard> faceDownDiscard();
}
