package org.jactr.tutorial.unit5.fan;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyConsumer, String person, String location);
	
	public void show();
	
	public void clear();
	
	public void hide();
	
	public void dispose();
	
}
