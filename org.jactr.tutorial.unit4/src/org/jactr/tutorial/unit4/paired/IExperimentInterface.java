package org.jactr.tutorial.unit4.paired;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyConsumer, int trial, String word, int digit);
	
	public void showWord();
	public void showDigit();
	
	public void clear();
	
	public void hide();
	
	public void dispose();
	
}
