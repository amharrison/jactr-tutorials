
package org.jactr.tutorial.unit6.probability.handler;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.DelayAction;
import org.jactr.tools.experiment.actions.common.EndTrialAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.jactr.tutorial.unit6.probability.IExperimentInterface;
import org.jactr.tutorial.unit6.probability.data.DataCollection;
import org.jactr.tutorial.unit6.probability.ui.GUIExperimentInterface;


/**
 * Actual experimental trial for the unit 4 paired associates experiment
 * 
 * @author harrison
 *
 */
public class DisplayTrial extends Trial {

	static public IExperimentInterface _interface;

	/*
	 * various constants
	 */
	final private double TRIAL_DURATION = 30; // seconds
	final private double CLEAR_DURATION = 1;
	
	/*
	 * trial variables
	 */
	final private double _trueRate;
	private String _block;
    private boolean _resultIsHeads;	
	private boolean _responded = false;

	static private int _count = 0;
	
	public DisplayTrial(IExperiment experiment, String block, double trueRate) {
		super(""+_count++, experiment);
		_trueRate = trueRate;
		_block =block;
		_resultIsHeads = Math.random()<_trueRate;

		configure(experiment);
	}

	/**
	 * called before start of trial. We configure here because the configuration can
	 * take some time. 
	 */
	public void initialize() {
		_interface.configure(DisplayTrial.this::complete, _resultIsHeads);
	}

	protected void configure(final IExperiment experiment) {
		
		/*
		 * configure the experiment using start, stop, and timed triggers
		 */
	}

	/**
	 * called when there is no response.
	 */
	protected void noResponse() {
		/*
		 * it's a good idea to track noResponses as they typically indicate
		 * something is wrong with your model
		 */
	}

	protected void complete(Character response) {

		/*
		 * final data collection and termination of the trial.
		 * don't forget to call stop()
		 */
	}
}
