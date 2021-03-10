package org.jactr.tutorial.unit5.siegler.handler;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.DelayAction;
import org.jactr.tools.experiment.actions.common.EndExperimentAction;
import org.jactr.tools.experiment.actions.common.EndTrialAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.jactr.tutorial.unit5.siegler.IExperimentInterface;
import org.jactr.tutorial.unit5.siegler.data.DataCollection;
import org.jactr.tutorial.unit5.siegler.sim.SimulatedExperimentInterface;

public class AuralTrial extends Trial {

	static public IExperimentInterface _interface;

	static public final double MAX_TRIAL_TIME = 20;
	static public final double INTERTRIAL_DURATION = 2; 

	private final int _addend1;
	private final int _addend2;
	private boolean _responded;

	public AuralTrial(IExperiment experiment, int addend1, int addend2) {
		super(String.format("%d+%d", (int) Math.min(addend1, addend2), (int) Math.max(addend1, addend2)), experiment);
		_addend1 = addend1;
		_addend2 = addend2;
	}

	public void initialize() {

		if (_interface == null)
			_interface = new SimulatedExperimentInterface();

		_interface.configure(this::spokenResponse, _addend1, _addend2);

		IExperiment experiment = getExperiment();

		/**
		 * these are executed at the start of each trial
		 */
		ITrigger trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {
				_interface.announce();
			}
		});
		ITrigger endTrial = new TimeTrigger(MAX_TRIAL_TIME, true, experiment);
		endTrial.add(new EndTrialAction(this, getExperiment()));
		trigger.add(endTrial);
		setStartTrigger(trigger);

		trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Stopping " + getId(), experiment));
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				if (!_responded)
					noResponse();
			}

		});
		trigger.add(new DelayAction(INTERTRIAL_DURATION, experiment));

		setEndTrigger(trigger);

	}

	private void noResponse() {
		IDataLogger collector = getExperiment().getDataCollector();

		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		// we collapse true/false trials and letter
		String trial = String.format("%d+%d", (int) Math.min(_addend1, _addend2), (int) Math.max(_addend1, _addend2));
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("condition", trial);
		collector.simple("no-response", attr, getExperiment().getVariableContext());

		new EndExperimentAction(getExperiment()).fire(getExperiment().getVariableContext());
	}

	private void spokenResponse(String response) {
		if (_responded || !isRunning())
			return;

		_responded = true;
		stop();

		System.out.println("Responded : " + response);

		int recode = recode(response);

		IDataLogger collector = getExperiment().getDataCollector();

		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		boolean accurate = recode == _addend1 + _addend2;

		String trial = String.format("%d+%d", (int) Math.min(_addend1, _addend2), (int) Math.max(_addend1, _addend2));
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("response", response);
		attr.put("accurate", "" + accurate);
		attr.put("condition", trial);
		collector.simple("response", attr, getExperiment().getVariableContext());

		/*
		 * and also to the internal data collection singleton we created just for this
		 * project
		 */
		DataCollection.get().logData(trial, recode);
	}

	private int recode(String response) {
		switch (response) {
		case "zero":
			return 0;
		case "one":
			return 1;
		case "two":
			return 2;
		case "three":
			return 3;
		case "four":
			return 4;
		case "five":
			return 5;
		case "six":
			return 6;
		case "seven":
			return 7;
		case "eight":
			return 8;
		case "nine":
			return 9;
		}
		return -1;
	}

}
