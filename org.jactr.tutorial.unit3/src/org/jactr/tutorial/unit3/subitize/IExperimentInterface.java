package org.jactr.tutorial.unit3.subitize;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyConsumer, int targets);
	
	public void show();
	
	public void clear();
	
	public void hide();
	
	public void dispose();
	
}
