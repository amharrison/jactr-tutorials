package org.jactr.tutorial.unit6.probability;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> response, boolean resultIsHeads);
	
	public void showPrompt();
	
	public void showFeedback();
	
	public void clear();
	
	public void dispose();
}
