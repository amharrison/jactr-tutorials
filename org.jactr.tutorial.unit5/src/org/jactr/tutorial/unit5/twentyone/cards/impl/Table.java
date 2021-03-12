package org.jactr.tutorial.unit5.twentyone.cards.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.IShoe;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;

public class Table implements ITable {

	private List<IPlayer> _players;
	private Map<IPlayer, Integer> _bets = new HashMap<>();
	private IDealer _dealer;
	private ICardEngine _engine;
	private IShoe _shoe;
	private Stack<ICard> _faceUp = new Stack<>();
	private Stack<ICard> _faceDown = new Stack<>();

	public Table(IDealer dealer, ICardEngine engine) {
		_engine = engine;
		_dealer = dealer;
		_players = new ArrayList<>();
		_shoe = _engine.newShoe();
	}

	@Override
	public List<IPlayer> getPlayers() {
		return _players;
	}

	@Override
	public IDealer getDealer() {
		return _dealer;
	}

	public IShoe getShoe() {
		return _shoe;
	}

	@Override
	public int getBet(IPlayer player) {
		return _bets.getOrDefault(player, 0);
	}

	@Override
	public void bet(IPlayer player, int betIncrement) {
		_bets.compute(player, (p,old)->{
			if(old==null) return betIncrement;
			return old+betIncrement;
		});
	}
	
	public void playHand() {
		try
		{
		_engine.playHand(this);
		_bets.clear();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Stack<ICard> faceUpDiscard() {
		return _faceUp;
	}

	@Override
	public Stack<ICard> faceDownDiscard() {
		return _faceDown;
	}
}
