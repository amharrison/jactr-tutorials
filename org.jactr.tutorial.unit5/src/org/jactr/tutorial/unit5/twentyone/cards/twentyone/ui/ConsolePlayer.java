package org.jactr.tutorial.unit5.twentyone.cards.twentyone.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;
import org.jactr.tutorial.unit5.twentyone.cards.ITable;
import org.jactr.tutorial.unit5.twentyone.cards.impl.AbstractPlayer;

public class ConsolePlayer extends AbstractPlayer {

	private Executor _executor;

	public ConsolePlayer(Executor executor, int bank) {
		super("Player", bank);
		_executor = executor;
	}

	@Override
	public CompletableFuture<IAction> turn(ITable table, Set<IAction> possibleActions) {

		Supplier<IAction> supplier = () -> {
			IAction rtn = null;
//				Scanner scanner = new Scanner(System.in);
			BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
			while (rtn == null) {
				String input = null;
				try {
					input = scanner.readLine();
					if (input.length() == 1) {
						char c = input.charAt(0);
						for (IAction action : possibleActions)
							if (action.getShortCut() == c) {
								rtn = action;
								break;
							}
					} else {
						for (IAction action : possibleActions)
							if (action.getName().equals(input)) {
								rtn = action;
								break;
							}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				scanner.close();
			}
			return rtn;
		};

		return CompletableFuture.supplyAsync(supplier, _executor);
	}

}
