
package org.jactr.tutorial.unit4.paired.handler;

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
import org.jactr.tutorial.unit4.paired.IExperimentInterface;
import org.jactr.tutorial.unit4.paired.data.DataCollection;
import org.jactr.tutorial.unit4.paired.sim.SimulatedExperimentInterface;
import org.jactr.tutorial.unit4.paired.ui.GUIExperimentInterface;


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
	final private double TRIAL_DURATION = 5; //s
	
	/*
	 * trial variables
	 */
	final private int _number;
	final private String _word;
	final private int _trial;
	private boolean _responded = false;
	
	public DisplayTrial(String id, int trial, String word, int number, IExperiment experiment) {
		super(id, experiment);
		_number = number;
		_word = word;
		_trial = trial;

		configure(experiment);
	}

	/**
	 * called before start of trial. We configure here because the configuration
	 * can take some time. If it were part of start() we would loose precious
	 * time to the configuration. By doing it initialize() we are performing
	 * the configuration before the clock starts.
	 */
	public void initialize() {
		_interface.configure(DisplayTrial.this::consumeKey, _trial, _word, _number);
	}

	protected void configure(final IExperiment experiment) {
		/**
		 * we want to use the simulated interface when doing bulk runs
		 */
		boolean useSimulated = IterativeMain.isRunning();
		if (_interface == null) {
			if (useSimulated)
				_interface = new SimulatedExperimentInterface(getExperiment());
			else
				_interface = new GUIExperimentInterface();
		}
		
	
		ITrigger endTrial = new TimeTrigger(TRIAL_DURATION, true, experiment);
		endTrial.add(new EndTrialAction(getExperiment()));

		/*
		 * these are executed at the start of each trial
		 */
		ITrigger trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {
				_interface.showWord();
			}
		});
		
		trigger.add(endTrial);
				
		setStartTrigger(trigger);

		
		
		/*
		 * these are all executed at the end of the trial
		 */
		trigger = new ImmediateTrigger(experiment);
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				_interface.clear();		
			}
		});
				
		trigger.add(new DelayAction(1, experiment)); //one second blank
		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {
				_interface.showDigit();
			}
		});
		trigger.add(new DelayAction(TRIAL_DURATION, experiment));
		trigger.add(new LogAction("Stopping " + getId(), experiment));
		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				_interface.clear();
				
				/*
				 * while we are here, check to see if they've responded..
				 * 
				 */
				if(!_responded)
					noResponse();
			}
		});
		trigger.add(new DelayAction(1, experiment));

		setEndTrigger(trigger);
	}
	
	/**
	 * called when there is no response.
	 */
	protected void noResponse()
	{
		IDataLogger collector = getExperiment().getDataCollector();

		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("condition", ""+_trial);
		collector.simple("no-response", attr, getExperiment().getVariableContext());
		
		DataCollection.get().logData(_trial, false, 0);
	}

	protected void consumeKey(Character keyPressed) {
		
		if (!Character.isDigit(keyPressed) || _responded)
			return;
		
		_responded = true;

		System.out.println("Pressed : " + keyPressed);
		
			stop(); // stop the trial after which we can get the responseTime

			/*
			 * collect data to the log file
			 */
			IDataLogger collector = getExperiment().getDataCollector();

			double now = getExperiment().getClock().getTime();
			double latency = now - getStartTime();
			Map<String, String> attr = new TreeMap<>();

			attr.put("time", String.format("%.2f", now));
			attr.put("latency", String.format("%.2f", latency));
			attr.put("response", ""+keyPressed);
			attr.put("condition", ""+_trial);
			collector.simple("response", attr, getExperiment().getVariableContext());

			/*
			 * and also to the internal data collection singleton we created just for this
			 * project
			 */
			boolean accurate = (_number + '0') == keyPressed;
			
			DataCollection.get().logData(_trial, accurate, latency);
	}
}
