package org.jactr.tutorial.unit3.subitize.sim;

import java.util.function.Consumer;

import org.commonreality.identifier.IIdentifier;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.speech.DefaultSpeechSensor;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.sensor.XMLSensorAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tutorial.unit3.subitize.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	int _maxTargets;
	SimulatedSpeaker _speaker;
	Consumer<Character> _consumer;

	public SimulatedExperimentInterface(IExperiment experiment) {
		_experiment = experiment;

		DefaultSpeechSensor dss = (DefaultSpeechSensor) CommonReality.getSensors().stream().filter((s) -> {
			return s instanceof DefaultSpeechSensor;
		}).findFirst().get();
		_speaker = (SimulatedSpeaker) dss.getSpeaker();
	}

	protected IIdentifier getAgentIdOfExperimentModel() {
		IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
		if (model == null)
			model = ACTRRuntime.getRuntime().getModels().iterator().next();

		IIdentifier agent = ACTRRuntime.getRuntime().getConnector().getAgent(model).getIdentifier();
		return agent;
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, int targets) {
		_maxTargets = targets;
		_consumer = keyConsumer;
		_speaker.setSpeakerCallback(this::spoke);
		IVariableContext context = _experiment.getVariableContext();

		double heightRange = 10; // -5 - 5
		double widthRange = 26; // -13 - 13

		// set the position variables for resolution
		for (int i = 0; i < _maxTargets; i++) {
			double x = Math.random() * widthRange - widthRange / 2;
			double y = Math.random() * heightRange - heightRange / 2;
			context.set("" + i, String.format("%.2f, %.2f", x, y));
		}

	}

	protected void spoke(String text) {
		char key = '\n';
		if (text.length() > 1) {
			switch (text) {
			case "ten":
				key = '0';
				break;
			case "one":
				key = '1';
				break;
			case "two":
				key = '2';
				break;
			case "three":
				key = '3';
				break;
			case "four":
				key = '4';
				break;
			case "five":
				key = '5';
				break;
			case "six":
				key = '6';
				break;
			case "seven":
				key = '7';
				break;
			case "eight":
				key = '8';
				break;
			case "nine":
				key = '9';
				break;
			}
		}
		else
		{
			key = text.charAt(0);
		}
		
		_consumer.accept(key);
	}

	@Override
	public void show() {

		new XMLSensorAction(String.format("org/jactr/tutorial/unit3/subitize/sim/%d.xml", _maxTargets - 1), true,
				_experiment).fire(_experiment.getVariableContext());

	}

	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit3/subitize/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
//		_keyboard.consumers.clear();
	}

}
