
package org.jactr.tutorial.unit3.sperling.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import org.commonreality.time.IClock;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.entry.iterative.IterativeMain;
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


/**
 * Actual experimental trial for the unit 3 sperling experiment
 * 
 * @author harrison
 *
 */
public class DisplayTrial extends Trial {

	static public IExperimentInterface _interface;

	/*
	 * various constants
	 */
	final private double DISPLAY_DURATION = 0.5; //s
	final private double INTERTRIAL_DELAY = 30; //s
	final private double MAX_TRIAL_TIME = 660; // 11min why so long? the low retrieval threshold means long retrievals
	
	/*
	 * trial variables
	 */
	final private char[][] _display;
	final private int _cueRow;
	final private double _delayInSeconds;
	private double _timeOfFirstResponse;
	final private StringBuilder _response = new StringBuilder();

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

	/**
	 * called before start of trial. We configure here because the configuration
	 * can take some time. If it were part of start() we would loose precious
	 * time to the configuration. By doing it initialize() we are performing
	 * the configuration before the clock starts.
	 */
	public void initialize() {
		_interface.configure(DisplayTrial.this::consumeKey, _display);
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
				_interface = new GUIExperimentInterface(getExperiment(), _display.length, _display[0].length);
		}
		
		/**
		 * time out after certain amount of time. We will log the model to a file
		 * of problematic models for future analysis.
		 */
		ITrigger timeout = new TimeTrigger(MAX_TRIAL_TIME, true, experiment);
		timeout.add(new LogAction("Timing out model, terminating.", experiment));
		timeout.add(new EndExperimentAction(experiment));
		timeout.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {

				timedOut(context.get("SubjectId").toString());
			}

		});

		/*
		 * these are executed at the start of each trial
		 */
		ITrigger trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Starting " + getId(), experiment));

		trigger.add(new IAction() {

			@Override
			public void fire(IVariableContext context) {

				_interface.show();

				/**
				 * for timing tolerances this tight, this is the preferred
				 * way to do time triggers. TimedTrigger is a little too sloppy
				 * for this time tolerance.
				 */
				IClock clock = getExperiment().getClock();

				/*
				 * the beep
				 */
				CompletableFuture<Double> future = clock.waitForTime(clock.getTime() + _delayInSeconds);
				future.thenAccept(time -> {
					_interface.beep(_cueRow);
				});
				
				/*
				 * clear the screen
				 */
				future = clock.waitForTime(clock.getTime() + DISPLAY_DURATION);
				future.thenAccept(time -> {
					_interface.clear();
				});

			}
		});
		
		trigger.add(timeout);
				
		setStartTrigger(trigger);

		/*
		 * these are all executed at the end of the trial
		 */
		trigger = new ImmediateTrigger(experiment);
		trigger.add(new LogAction("Stopping " + getId(), experiment));

		trigger = new ImmediateTrigger(experiment);

		trigger.add(new IAction() {
			@Override
			public void fire(IVariableContext context) {

				_interface.hide();
			}
		});
		trigger.add(new DelayAction(INTERTRIAL_DELAY, experiment));

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
			attr.put("condition", condition);
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

	/**
	 * timeout tracking is useful when developing a model as it high lights edge
	 * conditions.
	 * 
	 * @param subjectId
	 */
	private void timedOut(String subjectId) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("timedoutModels.txt", true));
			pw.format("%s\t%s\n", subjectId, ACTRRuntime.getRuntime().getWorkingDirectory().getName());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
