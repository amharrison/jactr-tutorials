package org.jactr.tutorial.unit5.twentyone.jactr.internal;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.ITwentyOneListener;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;
import org.jactr.tutorial.unit5.twentyone.jactr.SimpleTwentyOneExtension;

public class Listener implements ITwentyOneListener {

	private CompletableFuture<Void> _playersTurn;
	private IPlayer _player;
	private CompletableFuture<Result> _result;
	private CompletableFuture<Boolean> _started = new CompletableFuture<Boolean>();

	public Listener(IPlayer player, SimpleTwentyOneExtension extension) {
		_player = player;
		_playersTurn = new CompletableFuture<Void>();
	}
	
	public void reset() {
		_started = new CompletableFuture<Boolean>();
	}

	public void waitForStart() {
		try {
			_started.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void newHand(ICardEngine engine, IDealer dealer, IPlayer[] players) {

		_playersTurn = new CompletableFuture<Void>();
		_result = new CompletableFuture<Result>();
		_started.complete(true);
	}

	@Override
	public void turn(IPlayer player, Set<IAction> possibleActions) {
		if (_player == player) {
			_playersTurn.complete(null);
		}

	}

	@Override
	public void dealt(IPlayer player, ICard card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bet(IPlayer player, int bet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hit(IPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stand(IPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reveal(IPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bust(IPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void twentyOne(IPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void payout(IPlayer player, Result result, int payout) {
		_result.complete(result);
	}

	public Result waitForResult() {
		try {
			return _result.get();
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (ExecutionException e) {

			e.printStackTrace();
		}
		return null;
	}

	public void waitForPlayersTurn() {
		try {
			_playersTurn.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
