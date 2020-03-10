
package org.jactr.tutorial.unit2.experiment.handler;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tutorial.unit2.experiment.ui.GUIExperimentInterface;

public class DisplayTrial extends Trial {

	static private GUIExperimentInterface _interface;
	private Character _target;
	private Character _foil;

	public DisplayTrial(String id, IExperiment experiment, Character target) {
		super(id, experiment);
		_target = target;
		configure(experiment);
	}

	public DisplayTrial(String id, IExperiment experiment, Character target, Character foil) {
		super(id, experiment);
		_target = target;
		_foil = foil;
		configure(experiment);
	}

	protected void configure(IExperiment experiment) {
		final boolean isModelRunning = ACTRRuntime.getRuntime().getModels().size() != 0;

		ITrigger trigger = new ImmediateTrigger(experiment);
		/*
		 * these are executed at the start of each trial
		 */
		trigger.add(new LogAction("Starting " + getId(), experiment));

		if (isModelRunning) {

		} else {
			if (_interface == null)
				_interface = new GUIExperimentInterface(_foil == null ? 1 : 3);
			trigger.add(new IAction() {

				@Override
				public void fire(IVariableContext context) {
					if (_foil == null)
						_interface.configure(DisplayTrial.this::consumeKey, "" + _target);
					else {
						//ideally we'd shuffle the positions of target and foil
						_interface.configure(DisplayTrial.this::consumeKey, ""+_foil, "" + _target, ""+_foil);
					}
					_interface.show();
				}
			});
		}

		setStartTrigger(trigger);
		trigger.add(new LogAction("Stopping " + getId(), experiment));

		/*
		 * these are all executed at the end of the trial
		 */
		trigger = new ImmediateTrigger(experiment);
		if (isModelRunning) {

		} else {
			trigger.add(new IAction() {
				@Override
				public void fire(IVariableContext context) {
					_interface.hide();
				}
			});
		}

		setEndTrigger(trigger);

	}

	protected void consumeKey(Character keyPressed) {
		stop(); // stop the trial after which we can get the responseTime

		IDataLogger collector = getExperiment().getDataCollector();
		double now = getExperiment().getClock().getTime();
		Map<String, String> attr = new TreeMap<>();
		attr.put("time", String.format("%.2f", now));
		attr.put("delta", String.format("%.2f", now - getStartTime()));
		attr.put("key", "" + keyPressed);

		collector.simple("response", attr, getExperiment().getVariableContext());
	}

}
