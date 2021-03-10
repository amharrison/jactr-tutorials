package org.jactr.tutorial.unit5.siegler;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<String> speechHandler, int addend1, int addend2);
	
	public void announce();
	
	
	public void dispose();
}
