package org.jactr.tutorial.unit2.experiment.sim;

import java.util.function.Consumer;

import org.commonreality.identifier.IIdentifier;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.common.LockAction;
import org.jactr.tools.experiment.actions.common.UnlockAction;
import org.jactr.tools.experiment.actions.sensor.XMLSensorAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tutorial.unit2.experiment.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private String[] _labels;
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
		IIdentifier agent = ACTRRuntime.getRuntime().getConnector().getAgent(model).getIdentifier();
		return agent;
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, String... labels) {
		_labels = labels;
		//wire in the consumer
        _keyboard.consumers.put(getAgentIdOfExperimentModel(), keyConsumer);
	}

	@Override
	public void show() {
		IVariableContext context = _experiment.getVariableContext();

		// set the variables
		context.set("target", _labels[0]);
		if (_labels.length > 1)
			context.set("foil", _labels[1]);

		// inject percepts
		if (_labels.length == 1)
			new XMLSensorAction("org/jactr/tutorial/unit2/experiment/sim/single.xml", true, _experiment).fire(context);
		else
			new XMLSensorAction("org/jactr/tutorial/unit2/experiment/sim/triple.xml", true, _experiment).fire(context);

		// unlock
		new UnlockAction("demo", _experiment).fire(context);
	}

	@Override
	public void hide() {

		new LockAction("demo", _experiment).fire(_experiment.getVariableContext());
		new XMLSensorAction("org/jactr/tutorial/unit2/experiment/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void dispose() {
		_keyboard.consumers.remove(getAgentIdOfExperimentModel());
	}

}
