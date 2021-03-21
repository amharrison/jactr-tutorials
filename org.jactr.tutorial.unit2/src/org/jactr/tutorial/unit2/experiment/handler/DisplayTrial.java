
package org.jactr.tutorial.unit2.experiment.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.LockAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.actions.common.UnlockAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tutorial.unit2.experiment.IExperimentInterface;
import org.jactr.tutorial.unit2.experiment.ui.GUIExperimentInterface;

/**
 * The DisplayTrial handles the majority of the lifting on the experiment side.
 * This represents one trial of either unit2 experiment (single character recognition,
 * and unique character recognition).
 * 
 * @author harrison
 *
 */
public class DisplayTrial extends Trial {

	/**
	 * The actual implementation of the experimental trial we are using. In this
	 * case it will be GUIExperimentInterface
	 */
	static public IExperimentInterface _interface;

	private Character _target;
	private Character _foil;

	/**
	 * constructor for the single character recognition experiment
	 * @param id
	 * @param experiment
	 * @param target
	 */
	public DisplayTrial(String id, IExperiment experiment, Character target) {
		super(id, experiment);
		_target = target;
		configure(experiment);
	}

	/**
	 * constructor for the unique character recognition experiment
	 * @param id
	 * @param experiment
	 * @param target
	 * @param foil
	 */
	public DisplayTrial(String id, IExperiment experiment, Character target, Character foil) {
		super(id, experiment);
		_target = target;
		_foil = foil;
		configure(experiment);
	}

	/**
	 * Called at the start of the experimental trial. Let's use this time to set the
	 * variables for resolution.
	 */
	public void start() {
		getExperiment().getVariableContext().set("target", "" + _target);
		if (_foil != null)
			getExperiment().getVariableContext().set("foil", "" + _foil);
		
		super.start();
	}

	protected void configure(IExperiment experiment) {
		
		/**
		 * We only have one interace, but usually there are two: one for the human
		 * experiment and one for the simulated experiment. We can determine which to use
		 * by interrogatting ACTRRuntime.getRuntime().getModels().size() or IterativeMain.isRunning()
		 */
		if (_interface == null) {
			_interface = new GUIExperimentInterface(_foil == null ? 1 : 3);
		}

		/*
		 * these are executed at the start of each trial
		 */
		ITrigger trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {
				/**
				 * configure the experiment then show it.
				 */
				if (_foil == null)
					_interface.configure(DisplayTrial.this::consumeKey, "" + _target);
				else {
					List<String> list = Arrays.asList("" + _foil, "" + _target, "" + _foil);
					Collections.shuffle(list);
					_interface.configure(DisplayTrial.this::consumeKey, list.toArray(new String[3]));
				}
				_interface.show();
			}
		});
		/**
		 * unlock the model. This is like opening the gates on a model, letting it run free
		 */
		trigger.add(new UnlockAction("demo", experiment));

		setStartTrigger(trigger);
		
		
		/**
		 * these are all executed at the end of the trial
		 */
		trigger = new ImmediateTrigger(experiment);
		//regain control of the model preventing it from running beyond find-unattended-letter
		trigger.add(new LockAction("demo", experiment));
		trigger.add(new LogAction("Stopping " + getId(), experiment));

		trigger = new ImmediateTrigger(experiment);

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {
				_interface.hide();
			}
		});

		setEndTrigger(trigger);
	}

	/**
	 * Called when the key is pressed. This logs our data
	 * @param keyPressed
	 */
	protected void consumeKey(Character keyPressed) {
		System.out.println("Pressed "+keyPressed);
		
		stop(); // stop the trial after which we can get the responseTime

		/**
		 * save to the default data xml writer.
		 */
		IDataLogger collector = getExperiment().getDataCollector();
		double now = getExperiment().getClock().getTime();
		Map<String, String> attr = new TreeMap<>();
		attr.put("time", String.format("%.2f", now));
		attr.put("delta", String.format("%.2f", now - getStartTime()));
		attr.put("key", "" + keyPressed);

		collector.simple("response", attr, getExperiment().getVariableContext());
	}

}
