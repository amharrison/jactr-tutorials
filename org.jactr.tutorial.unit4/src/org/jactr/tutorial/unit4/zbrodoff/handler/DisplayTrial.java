package org.jactr.tutorial.unit4.zbrodoff.handler;

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
import org.jactr.tutorial.unit4.paired.data.DataCollection;
import org.jactr.tutorial.unit4.zbrodoff.IExperimentInterface;
import org.jactr.tutorial.unit4.zbrodoff.sim.SimulatedExperimentInterface;

public class DisplayTrial extends Trial {

	static public IExperimentInterface _interface;

	static public final double MAX_TRIAL_TIME = 10;
	static public final double INTERTRIAL_DURATION = 2; // from their paper

	private final char _alpha;
	private final char _digit;
	private final char _answer;
	private final int _block;
	private final boolean _isTrue;
	private boolean _responded;

	public DisplayTrial(IExperiment experiment, int block, char alpha, char digit, boolean isTrue) {
		super(String.format("%c%d-%s", alpha, (int) digit, isTrue), experiment);
		_alpha = alpha;
		_digit = digit;
		_block = block;
		_answer = (char) (isTrue ? (_alpha + _digit) : (_alpha + _digit + 1));
		_isTrue = isTrue;
	}

	public void initialize() {

		if (_interface == null)
			_interface = new SimulatedExperimentInterface(getExperiment());

		_interface.configure(this::keyPressed, _alpha, _digit, _answer);

		IExperiment experiment = getExperiment();

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
		ITrigger endTrial = new TimeTrigger(MAX_TRIAL_TIME, true, experiment);
		endTrial.add(new EndTrialAction(this, getExperiment()));
		trigger.add(endTrial);
		setStartTrigger(trigger);

		trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Stopping " + getId(), experiment));
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				_interface.clear();

				/*
				 * while we are here, check to see if they've responded..
				 * 
				 */
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
		String trial = String.format("%d:%d", _block, (int)_digit);
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("condition", trial);
		collector.simple("no-response", attr, getExperiment().getVariableContext());

		new EndExperimentAction(getExperiment()).fire(getExperiment().getVariableContext());
	}

	private void keyPressed(Character keyPressed) {

		keyPressed = Character.toLowerCase(keyPressed);
		if ((keyPressed != 'd' && keyPressed != 'k') || _responded)
			return;

		_responded = true;
		stop();
		
		System.out.println("Pressed : "+keyPressed);
		

		IDataLogger collector = getExperiment().getDataCollector();

		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		boolean accurate = (_isTrue && keyPressed == 'k') || (!_isTrue && keyPressed == 'd');

		String trial = String.format("%d:%d", _block, (int)_digit);
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("response", "" + keyPressed);
		attr.put("accurate", ""+accurate);
		attr.put("condition", trial);
		collector.simple("response", attr, getExperiment().getVariableContext());

		/*
		 * and also to the internal data collection singleton we created just for this
		 * project
		 */
		DataCollection.get().logData(trial, false, accurate, latency);
	}

}
