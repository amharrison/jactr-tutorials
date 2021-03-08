package org.jactr.tutorial.unit5.fan.sim;

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
import org.jactr.tutorial.unit5.fan.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private String _person;
	private String _location;
	private SimulatedKeyboard _keyboard;

	public SimulatedExperimentInterface(IExperiment experiment) {
		_experiment = experiment;

		// wire the consumer to the simulated keyboard
		// find the keyboard sensor
		DefaultKeyboardSensor keyboardSensor = (DefaultKeyboardSensor) CommonReality.getSensors().stream()
				.filter((s) -> {
					return s instanceof DefaultKeyboardSensor;
				}).findFirst().get();
		_keyboard = new SimulatedKeyboard();
		keyboardSensor.setActuator(_keyboard);
	}

	protected IIdentifier getAgentIdOfExperimentModel() {
		IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
		if(model==null)
			model = ACTRRuntime.getRuntime().getModels().iterator().next();
		
		IIdentifier agent = ACTRRuntime.getRuntime().getConnector().getAgent(model).getIdentifier();
		return agent;
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, String person, String location) {
		_person = person;
		_location =location;
		// wire in the consumer
		_keyboard.consumers.put(getAgentIdOfExperimentModel(), keyConsumer);
	}

	@Override
	public void show() {
		IVariableContext context = _experiment.getVariableContext();

		context.set("person", _person);
		context.set("location", _location);

		new XMLSensorAction("org/jactr/tutorial/unit5/fan/sim/sentence.xml", true, _experiment).fire(context);

	}
	
	

	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit5/fan/sim/clear.xml", true, _experiment)
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
