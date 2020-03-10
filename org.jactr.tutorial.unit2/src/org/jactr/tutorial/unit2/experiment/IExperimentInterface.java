package org.jactr.tutorial.unit2.experiment;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyConsumer, String ...labels);
	
	public void show();
	
	public void hide();
	
}
