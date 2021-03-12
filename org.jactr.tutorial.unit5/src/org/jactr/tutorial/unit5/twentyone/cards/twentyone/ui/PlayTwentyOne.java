package org.jactr.tutorial.unit5.twentyone.cards.twentyone.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

import org.jactr.tutorial.unit5.twentyone.cards.ICardEngine;
import org.jactr.tutorial.unit5.twentyone.cards.IDealer;
import org.jactr.tutorial.unit5.twentyone.cards.impl.Table;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneDealer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine;

public class PlayTwentyOne {

	public PlayTwentyOne() {
	}

	public static void main(String[] args) {

		IDealer dealer = new TwentyOneDealer();
		ConsolePlayer player = new ConsolePlayer(Executors.newSingleThreadExecutor(), 100);
		
		ConsoleDisplay display = new ConsoleDisplay(player, dealer);
		
		ICardEngine engine = new TwentyOneEngine(10);
		engine.addListener(display);
		
		Table table = new Table(dealer, engine);

		table.getPlayers().add(player);
		boolean quit = false;

		while (!quit) {
			table.playHand();
			System.out.println("Another game? y)es, n)o");
			System.out.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			Scanner scanner = new Scanner(System.in);
			try {
				String input = reader.readLine();
				quit = input.charAt(0) != 'y';
				//reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.exit(1);

	}

}
