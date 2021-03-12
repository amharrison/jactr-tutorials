package org.jactr.tutorial.unit5.twentyone.jactr.internal;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.AbstractPlayer;

public class ModelPlayer extends AbstractPlayer {

	private CompletableFuture<IAction> _turn;
	private Set<IAction> _possibleActions = Collections.emptySet();
	
	public ModelPlayer(String name) {
		super(name, Integer.MAX_VALUE);
	}
	
	protected CompletableFuture<IAction> getFuture()
	{
      return _turn;
	}
	
	protected Set<IAction> getPossileActions(){
		return _possibleActions;
	}

	@Override
	public CompletableFuture<IAction> turn(ITable table, Set<IAction> possibleActions) {
		_possibleActions = possibleActions;
		_turn = new CompletableFuture<IAction>();
		return _turn;
	}

}
