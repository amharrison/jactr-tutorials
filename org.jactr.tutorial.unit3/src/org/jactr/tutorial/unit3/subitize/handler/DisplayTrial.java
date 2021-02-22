
package org.jactr.tutorial.unit3.subitize.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.DelayAction;
import org.jactr.tools.experiment.actions.common.EndExperimentAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.ImmediateTrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.jactr.tutorial.unit3.sperling.data.DataCollection;
import org.jactr.tutorial.unit3.subitize.IExperimentInterface;
import org.jactr.tutorial.unit3.subitize.sim.SimulatedExperimentInterface;
import org.jactr.tutorial.unit3.subitize.ui.GUIExperimentInterface;

public class DisplayTrial extends Trial {

	static public IExperimentInterface _interface;

	private double DISPLAY_DURATION = 1;
	private double INTERTRIAL_DELAY = 5;
	private double MAX_TRIAL_TIME = 660; 
	private int _targetCount;

	public DisplayTrial(String id, IExperiment experiment, int targets) {
		super(id, experiment);
		_targetCount = targets;

		configure(experiment);
	}
	
	public void initialize() {
		_interface.configure(DisplayTrial.this::consumeKey, _targetCount);
	}

	protected void configure(final IExperiment experiment) {
		/*
		 * we want to use the simulated interface when doing bulk runs
		 */
		boolean useSimulated = ACTRRuntime.getRuntime().getModels().size() > 0;
		if (_interface == null) {
			if (useSimulated)
				_interface = new SimulatedExperimentInterface(getExperiment());
			else
				_interface = new GUIExperimentInterface(10);
		}

		// we use this for the delayed clearing of the display
		ITrigger delayed = new TimeTrigger(DISPLAY_DURATION, true, experiment);
		delayed.add(new IAction() {

			@Override
			public void fire(IVariableContext arg0) {
				_interface.clear();
			}
		});

		ITrigger timeout = new TimeTrigger(MAX_TRIAL_TIME, true, experiment);
		timeout.add(new LogAction("Timing out model, terminating.", experiment));
		timeout.add(new EndExperimentAction(experiment));
		timeout.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {

				timedOut(context.get("SubjectId").toString());
			}

		});

		ITrigger trigger = new ImmediateTrigger(experiment);
		/*
		 * these are executed at the start of each trial
		 */
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {

				

				_interface.show();
			}
		});

		trigger.add(delayed);
		trigger.add(timeout);
		setStartTrigger(trigger);

		trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Stopping " + getId(), experiment));

		/*
		 * these are all executed at the end of the trial
		 */
		trigger.add(new DelayAction(INTERTRIAL_DELAY, experiment));

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {

				_interface.hide();
			}
		});

		setEndTrigger(trigger);
	}

	protected void consumeKey(Character keyPressed) {

		if (keyPressed < '0' || keyPressed > '9')
			return; // ignore

		System.out.println("Pressed : " + keyPressed);
		stop(); // stop the trial after which we can get the responseTime

		/*
		 * collect data to the log file
		 */
		IDataLogger collector = getExperiment().getDataCollector();

		String condition = String.format("%d", _targetCount);
		double now = getExperiment().getClock().getTime();
		double latency = now - getStartTime();
		Map<String, String> attr = new TreeMap<>();

		attr.put("time", String.format("%.2f", now));
		attr.put("delta", String.format("%.2f", latency));
		attr.put("response", "" + keyPressed);
		attr.put("condition", condition);
		collector.simple("response", attr, getExperiment().getVariableContext());

		/*
		 * and also to the internal data collection singleton we created just for this
		 * project
		 */
		int count = keyPressed - '0';
		if (count == 0)
			count = 10;

		if (count == _targetCount)
			DataCollection.get().logData(condition, latency);
	}

	private void timedOut(String subjectId) {
		DataCollection.get().wipeSubject();
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("timedoutModels.txt", true));
			pw.format("%s\t%s\n", subjectId, ACTRRuntime.getRuntime().getWorkingDirectory().getName());
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
