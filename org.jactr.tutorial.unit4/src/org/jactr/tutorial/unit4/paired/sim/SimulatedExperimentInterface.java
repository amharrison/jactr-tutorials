package org.jactr.tutorial.unit4.paired.sim;

import java.util.function.Consumer;

import org.commonreality.identifier.IIdentifier;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.sensor.XMLSensorAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tutorial.unit4.paired.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private String _word;
	private String _digit;
	private SimulatedKeyboard _keyboard;

	public SimulatedExperimentInterface(IExperiment experiment) {
		_experiment = experiment;

		// wire the consumer to the simulated keyboard
		// find the keyboard sensor
		DefaultKeyboardSensor keyboardSensor = (DefaultKeyboardSensor) CommonReality.getSensors().stream()
				.filter((s) -> {
					return s instanceof DefaultKeyboardSensor;
				}).findFirst().get();
		_keyboard = (SimulatedKeyboard) keyboardSensor.getActuator();
	}

	protected IIdentifier getAgentIdOfExperimentModel() {
		IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
		if(model==null)
			model = ACTRRuntime.getRuntime().getModels().iterator().next();
		
		IIdentifier agent = ACTRRuntime.getRuntime().getConnector().getAgent(model).getIdentifier();
		return agent;
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, int trial, String word, int digit) {
		_word = word;
		_digit = ""+digit;

		// wire in the consumer
		_keyboard.consumers.put(getAgentIdOfExperimentModel(), keyConsumer);
	}

	@Override
	public void showWord() {
		IVariableContext context = _experiment.getVariableContext();

		context.set("word", _word);

		new XMLSensorAction("org/jactr/tutorial/unit4/paired/sim/word.xml", true, _experiment).fire(context);

	}
	
	@Override
	public void showDigit() {
		IVariableContext context = _experiment.getVariableContext();

		context.set("digit", _digit);

		new XMLSensorAction("org/jactr/tutorial/unit4/paired/sim/digit.xml", true, _experiment).fire(context);

	}

	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit4/paired/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		_keyboard.consumers.clear();
	}

}
