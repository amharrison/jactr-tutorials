package org.jactr.tutorial.unit3.sperling;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyConsumer, char[][] rows);
	
	public void beep(int beepCount);
	
	public void show();
	
	public void clear();
	
	public void hide();
	
	public void dispose();
	
}
