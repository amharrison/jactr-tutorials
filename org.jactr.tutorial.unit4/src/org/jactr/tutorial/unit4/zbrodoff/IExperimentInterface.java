package org.jactr.tutorial.unit4.zbrodoff;

import java.util.function.Consumer;

public interface IExperimentInterface {

	public void configure(Consumer<Character> keyHandler, char alpha, int offset, char answer);
	
	public void show();
	
	public void clear();
	
	public void dispose();
}
