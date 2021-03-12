package org.jactr.tutorial.unit5.twentyone.cards;

public interface IAction {

	public String getName();
	public Character getShortCut();
	public Object getParameter();
	public IAction clone(Object parameter);
}
