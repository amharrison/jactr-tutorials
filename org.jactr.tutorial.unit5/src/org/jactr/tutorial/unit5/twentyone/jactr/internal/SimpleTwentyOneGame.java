package org.jactr.tutorial.unit5.twentyone.jactr.internal;

import java.util.Set;

import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.Table;
import org.jactr.tutorial.unit5.twentyone.cards.simple.SimpleTwentyOneDealer;
import org.jactr.tutorial.unit5.twentyone.cards.simple.SimpleTwentyOneEngine;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;
import org.jactr.tutorial.unit5.twentyone.jactr.SimpleTwentyOneExtension;

public class SimpleTwentyOneGame {

	
	private IDealer _dealer;
	private ITable _table;
	private ICardEngine _engine;
	private ModelPlayer _player;
	private Listener _listener;
	
	public SimpleTwentyOneGame(ModelPlayer player, SimpleTwentyOneExtension extension)
	{
		_dealer = new SimpleTwentyOneDealer(extension.getDealerStandsOn());
		_engine = new SimpleTwentyOneEngine();
		_table = new Table(_dealer, _engine);
		_player = player;
		_table.getPlayers().add(_player);
		_engine.addListener(_listener = new Listener(_player, extension));
	}
	
	public IDealer getDealer() {
		return _dealer;
	}
	
	public void startHand() {
		_listener.reset();
		//run the hand asynchronously since it may block
		ExecutorServices.getExecutor(ExecutorServices.POOL).submit(_table::playHand);
		//but then block at a known point
		_listener.waitForStart();
		_listener.waitForPlayersTurn();
	}
	
	public Result finishHand(String response)
	{
		Set<IAction> actions = _player.getPossileActions();
		IAction action = actions.stream().filter(a->{
			return a.getName().equals(response);
		}).findFirst().get();
		return finishHand(action);
	}
	
	protected Result finishHand(IAction modelsAction)
	{
		_player.getFuture().complete(modelsAction);
		return _listener.waitForResult();
	}
	
	
	
}
