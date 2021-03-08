
package org.jactr.tutorial.unit5.fan.handler;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.DelayAction;
import org.jactr.tools.experiment.actions.common.EndExperimentAction;
import org.jactr.tools.experiment.actions.common.EndTrialAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.actions.jactr.MarkerAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.jactr.tutorial.unit5.fan.IExperimentInterface;
import org.jactr.tutorial.unit5.fan.data.DataCollection;
import org.jactr.tutorial.unit5.fan.sim.SimulatedExperimentInterface;


/**
 * Actual experimental trial for the unit 5 fan experiment
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
	final private double INTERTRIAL_TIME = 5;
	/*
	 * trial variables
	 */
	final private String _person;
	final private String _location;
	final private boolean _isTarget;
	private boolean _responded = false;
	
	public DisplayTrial(String id, String person, String location, boolean isTarget, IExperiment experiment) {
		super(id, experiment);
		_person = person;
		_location = location;
		_isTarget = isTarget;

		configure(experiment);
	}

	/**
	 * called before start of trial. We configure here because the configuration
	 * can take some time. If it were part of start() we would loose precious
	 * time to the configuration. By doing it initialize() we are performing
	 * the configuration before the clock starts.
	 */
	public void initialize() {
		_interface.configure(DisplayTrial.this::consumeKey, _person, _location);
	}

	protected void configure(final IExperiment experiment) {

		if (_interface == null) {			
				_interface = new SimulatedExperimentInterface(getExperiment());
		}
		
	
		/**
		 * after the probe duration expires, we end the trial regardless of
		 * keypress
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
		//markers are used to annotate the log view based on the type of marker.
		// markers are opened and then closed.
		trigger.add(new MarkerAction("fan", _isTarget?"Target":"Foil", getId(), true, experiment));
		trigger.add(endTrial);
				
		setStartTrigger(trigger);

		
		/**
		 * end of trial
		 */
		trigger = new ImmediateTrigger(experiment);			
		trigger.add(new LogAction("Stopping " + getId(), experiment));
		trigger.add(new MarkerAction("fan", _isTarget?"Target":"Foil", getId(), false, experiment));
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
		trigger.add(new DelayAction(INTERTRIAL_TIME, experiment));

		setEndTrigger(trigger);
	}
	
	/**
	 * called when there is no response.
	 */
	protected void noResponse()
	{
		IDataLogger collector = getExperiment().getDataCollector();

		String condition = getId();
		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("latency", String.format("%.2f", latency));
		attr.put("condition", condition);
		collector.simple("no-response", attr, getExperiment().getVariableContext());
		
		DataCollection.get().logData(condition, true, false, 0);
		
		new EndExperimentAction(getExperiment()).fire(getExperiment().getVariableContext());
	}

	protected void consumeKey(Character keyPressed) {
		
		keyPressed = Character.toLowerCase(keyPressed);
		if ((keyPressed != 'd' && keyPressed != 'k') || _responded)
			return;

		_responded = true;

		System.out.println("Pressed : " + keyPressed);
		
			stop(); // stop the trial after which we can get the responseTime

			/*
			 * collect data to the log file
			 */
			IDataLogger collector = getExperiment().getDataCollector();

			boolean accurate = (keyPressed=='k' && _isTarget) || (keyPressed=='d' && !_isTarget);
			double now = getExperiment().getClock().getTime();
			double latency = now - getStartTime();
			String condition = getId();
			Map<String, String> attr = new TreeMap<>();

			attr.put("time", String.format("%.2f", now));
			attr.put("latency", String.format("%.2f", latency));
			attr.put("response", ""+keyPressed);
			attr.put("condition", condition);
			attr.put("accurate", ""+accurate);
			collector.simple("response", attr, getExperiment().getVariableContext());

			/*
			 * and also to the internal data collection singleton we created just for this
			 * project
			 */
			
			DataCollection.get().logData(condition, true, accurate, latency);
	}
}
