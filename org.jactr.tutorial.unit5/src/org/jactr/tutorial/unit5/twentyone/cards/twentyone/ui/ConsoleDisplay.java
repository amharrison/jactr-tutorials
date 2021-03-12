package org.jactr.tutorial.unit5.twentyone.cards.twentyone.ui;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.ITwentyOneListener;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;

public class ConsoleDisplay implements ITwentyOneListener {

	IPlayer _player;
	IDealer _dealer;

	public ConsoleDisplay(IPlayer player, IDealer dealer) {
		_dealer = dealer;
		_player = player;
	}

	private String asString(List<ICard> hand) {
		return hand.stream().map((c) -> {
			return c.toString();
		}).collect(Collectors.joining(","));
	}

	@Override
	public void turn(IPlayer player, Set<IAction> possibleActions) {
		if (player == _player) {
			StringBuilder prompt = new StringBuilder();
			for (IAction possible : possibleActions)
				prompt.append(String.format("%c) %s\n", possible.getShortCut(), possible.getName()));
			System.out.println(prompt.toString());
			System.out.flush();
		}
	}

	@Override
	public void dealt(IPlayer player, ICard card) {
		System.out.println(String.format("%s : %s", player.getName(), asString(player.getVisibleHand())));
		
		System.out.flush();
	}

	@Override
	public void hit(IPlayer player) {

	}

	@Override
	public void stand(IPlayer player) {

	}

	@Override
	public void payout(IPlayer player, Result result, int payout) {
		System.out.println(result.name() +" (payout : "+payout+" ["+player.getCash()+"])");
	}

	@Override
	public void bet(IPlayer player, int bet) {
		System.out.println(String.format("%s bet %d [%d]", player.getName(), bet, player.getCash()));
		System.out.flush();
	}

	@Override
	public void reveal(IPlayer player) {
		System.out.println(String.format("%s : %s", player.getName(), asString(player.getVisibleHand())));
		System.out.flush();
	}

	@Override
	public void bust(IPlayer player) {
		System.out.println(String.format("%s Bust!", player.getName()));
		System.out.flush();
	}

	@Override
	public void twentyOne(IPlayer player) {
		System.out.println(String.format("%s BlackJack!", player.getName()));
		System.out.flush();
	}

	@Override
	public void newHand(ICardEngine engine, IDealer dealer, IPlayer[] players) {
		// TODO Auto-generated method stub
		
	}

}
