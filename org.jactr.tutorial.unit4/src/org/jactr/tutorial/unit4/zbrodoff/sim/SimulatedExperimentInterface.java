package org.jactr.tutorial.unit4.zbrodoff.sim;

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
import org.jactr.tutorial.unit4.paired.sim.SimulatedKeyboard;
import org.jactr.tutorial.unit4.zbrodoff.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private char _alpha;
	private int _offset;
	private char _answer;
	
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
	public void configure(Consumer<Character> keyConsumer, char alpha, int offset, char answer) {
		
		_alpha = alpha;
		_offset = offset;
		_answer = answer;
		// wire in the consumer
		_keyboard.consumers.put(getAgentIdOfExperimentModel(), keyConsumer);
	}

	@Override
	public void show() {
		IVariableContext context = _experiment.getVariableContext();

		context.set("alpha", ""+_alpha);
		context.set("digit", ""+_offset);
		context.set("answer", ""+_answer);
		
		new XMLSensorAction("org/jactr/tutorial/unit4/zbrodoff/sim/expression.xml", true, _experiment).fire(context);
	}
	

	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit4/zbrodoff/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void dispose() {
		_keyboard.consumers.clear();
	}

}
