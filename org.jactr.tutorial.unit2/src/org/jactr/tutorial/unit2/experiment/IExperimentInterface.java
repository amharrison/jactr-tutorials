package org.jactr.tutorial.unit2.experiment;

import java.util.function.Consumer;


/**
 * Experiment interface for unit2. We implement interfaces so that implementations can
 * be swapped out based on the environment or simulated interfaces used.
 * @author harrison
 *
 */
public interface IExperimentInterface {

	/**
	 * configure the display for the current trial. 
	 * @param keyConsumer who to notify when a key is pressed
	 * @param labels one or two strings to be displayed. The first is the target,
	 * the second is the optional foil
	 */
	public void configure(Consumer<Character> keyConsumer, String ...labels);
	
	/**
	 * show the display window
	 */
	public void show();
	
	/**
	 * hide the display window
	 */
	public void hide();
	
	/**
	 * release any resources
	 */
	public void dispose();
	
}
