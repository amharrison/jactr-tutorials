package org.jactr.tutorial.unit6.bst;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<String> responseSequence, int a, int b, int c, int goal);
	
	public void show();
	
	public void clear();
	
	public void dispose();
}
