
package org.jactr.tutorial.unit3.sperling.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
import org.jactr.tutorial.unit3.sperling.IExperimentInterface;
import org.jactr.tutorial.unit3.sperling.data.DataCollection;
import org.jactr.tutorial.unit3.sperling.sim.SimulatedExperimentInterface;
import org.jactr.tutorial.unit3.sperling.ui.GUIExperimentInterface;

public class DisplayTrial extends Trial {

	static public IExperimentInterface _interface;

	private char[][] _display;

	private double DISPLAY_DURATION = 0.5;
	private double INTERTRIAL_DELAY = 30;
	private double MAX_TRIAL_TIME = 660; // 11min why so long? the low retrieval threshold means long retrievals
	private int _cueRow;
	private double _delayInSeconds;
	private double _timeOfFirstResponse;
	private StringBuilder _response = new StringBuilder();

	public DisplayTrial(String id, IExperiment experiment, double delayInSecond, int row, String... rows) {
		super(id, experiment);

		_cueRow = row;
		_delayInSeconds = delayInSecond;
		_display = new char[rows.length][rows[0].length()];
		for (int i = 0; i < rows.length; i++)
			for (int j = 0; j < rows[i].length(); j++)
				_display[i][j] = rows[i].charAt(j);

		configure(experiment);
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
				_interface = new GUIExperimentInterface(getExperiment(), _display.length, _display[0].length);
		}

		// we use this for the delayed clearing of the display
		ITrigger delayed = new TimeTrigger(DISPLAY_DURATION, true, experiment);
		delayed.add(new IAction() {

			@Override
			public void fire(IVariableContext arg0) {
				_interface.clear();
			}
		});

		ITrigger delayedCue = new TimeTrigger(_delayInSeconds, true, experiment);
		delayedCue.add(new IAction() {

			@Override
			public void fire(IVariableContext arg0) {
				_interface.beep(_cueRow);
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

				_interface.configure(DisplayTrial.this::consumeKey, _display);

				_interface.show();
			}
		});

		trigger.add(delayed);
		trigger.add(delayedCue);
		trigger.add(timeout);
		setStartTrigger(trigger);

		trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Stopping " + getId(), experiment));

		/*
		 * these are all executed at the end of the trial
		 */
		trigger = new ImmediateTrigger(experiment);

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
		/*
		 * consume each character until space to finish
		 */
		if (keyPressed.equals('\n'))
			return;

		System.out.println("Pressed : " + keyPressed);
		if (_timeOfFirstResponse == 0)
			_timeOfFirstResponse = getExperiment().getClock().getTime();

		if (keyPressed.equals(' ')) {
			stop(); // stop the trial after which we can get the responseTime

			/*
			 * collect data to the log file
			 */
			IDataLogger collector = getExperiment().getDataCollector();
			int score = computeScore();
			String condition = String.format("%.2fs", _delayInSeconds);
			double now = getExperiment().getClock().getTime();
			Map<String, String> attr = new TreeMap<>();

			attr.put("time", String.format("%.2f", now));
			attr.put("delta", String.format("%.2f", now - getStartTime()));
			attr.put("response", _response.toString());
			attr.put("score", "" + score);
			attr.put("firstResponse", String.format("%.2f", _timeOfFirstResponse - getStartTime()));
			collector.simple("response", attr, getExperiment().getVariableContext());

			/*
			 * and also to the internal data collection singleton we created just for this
			 * project
			 */
			DataCollection.get().logData(condition, score);

		} else {

			_response.append(Character.toLowerCase(keyPressed));
		}
	}

	/**
	 * not strictly correct as it doesn't account for duplicates.. but the model
	 * shouldn't produce any.
	 * 
	 * @return
	 */
	private int computeScore() {
		int score = 0;
		Set<Character> processed = new TreeSet<>();
		for (int i = 0; i < _response.length(); i++) {
			char ch = _response.charAt(i);
			if (!processed.contains(ch)) {
				if (matchesCuedRow(ch))
					score++;
				processed.add(ch);
			}
		}
		return score;
	}

	private boolean matchesCuedRow(char c) {
		for (char ch : _display[_cueRow - 1])
			if (ch == c)
				return true;
		return false;
	}

	private void timedOut(String subjectId) {
		//DataCollection.get().wipeSubject();
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
