package org.jactr.tutorial.unit5.twentyone.cards;

public interface ICardEngine {

	public void playHand(ITable table);
	
	public IShoe newShoe();
	
	public void addListener(ICardGameListener listener);
	public void removeListener(ICardGameListener listener);
}
