
package org.jactr.tutorial.unit6.bst.handler;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.entry.iterative.IterativeMain;
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
import org.jactr.tutorial.unit6.bst.IExperimentInterface;
import org.jactr.tutorial.unit6.bst.data.DataCollection;
import org.jactr.tutorial.unit6.bst.sim.SimulatedExperimentInterface;
import org.jactr.tutorial.unit6.bst.ui.GUIExperimentInterface;

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
	final private int _a, _b, _c, _goal;
	private boolean _responded = false;

	public DisplayTrial(String id, int a, int b, int c, int goal, IExperiment experiment) {
		super(id, experiment);
		_a = a;
		_b = b;
		_c = c;
		_goal = goal;

		configure(experiment);
	}

	/**
	 * called before start of trial. We configure here because the configuration can
	 * take some time. If it were part of start() we would loose precious time to
	 * the configuration. By doing it initialize() we are performing the
	 * configuration before the clock starts.
	 */
	public void initialize() {
		_interface.configure(DisplayTrial.this::complete, _a, _b, _c, _goal);
	}

	protected void configure(final IExperiment experiment) {
		/**
		 * we want to use the simulated interface when doing bulk runs
		 */
		boolean useSimulated = IterativeMain.isRunning();
		if (_interface == null) {
			if (useSimulated)
				_interface = new SimulatedExperimentInterface(experiment);
			else
				_interface = new GUIExperimentInterface();
		}

		/**
		 * after the probe duration expires, we end the trial regardless of keypress
		 */
		ITrigger endTrial = new TimeTrigger(TRIAL_DURATION, true, experiment);
		endTrial.add(new EndTrialAction(this, getExperiment()));

		/**
		 * these are executed at the start of each trial
		 */
		ITrigger trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {
				_interface.show();
			}
		});
		trigger.add(endTrial);

		setStartTrigger(trigger);

		/**
		 * these are all executed at the end of the trial. We clear for a second then
		 * show the target. If we went straight from word to digit, the visual system
		 * would actually miss the change and the buffer stuff would fail
		 */
		trigger = new ImmediateTrigger(experiment);
		trigger.add(new DelayAction(CLEAR_DURATION, experiment)); // one second blank
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				_interface.clear();
			}
		});

		trigger.add(new LogAction("Stopping " + getId(), experiment));
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				/*
				 * while we are here, check to see if they've responded..
				 * 
				 */
				if (!_responded)
					noResponse();
			}
		});

		setEndTrigger(trigger);
	}

	/**
	 * called when there is no response.
	 */
	protected void noResponse() {
		IDataLogger collector = getExperiment().getDataCollector();

		String condition = getId();
		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("condition", condition);
		collector.simple("no-response", attr, getExperiment().getVariableContext());
	}

	protected void complete(String actionSequence) {

		if (_responded)
			return;
		_responded = true;

		stop(); // stop the trial after which we can get the responseTime
		
		System.out.println(actionSequence);

		/*
		 * collect data to the log file
		 */
		IDataLogger collector = getExperiment().getDataCollector();

		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		String condition = getId();
		String strategy = "undershoot";
		char firstAction = actionSequence.charAt(0);
		switch (firstAction) {
		case 'A':
		case 'C':
			strategy = "undershoot";
			break;
		case 'B':
			strategy = "overshoot";
			break;
		}
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("response", "" + actionSequence);
		attr.put("condition", condition);
		attr.put("strategy", strategy);
		collector.simple("response", attr, getExperiment().getVariableContext());

		/*
		 * and also to the internal data collection singleton we created just for this
		 * project
		 */

		DataCollection.get().logData(condition, strategy.equals("overshoot"));
	}
}
