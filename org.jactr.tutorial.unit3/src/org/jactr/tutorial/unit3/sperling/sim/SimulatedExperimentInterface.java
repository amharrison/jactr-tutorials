package org.jactr.tutorial.unit3.sperling.sim;

import java.util.Optional;
import java.util.function.Consumer;

import org.commonreality.identifier.IIdentifier;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.aural.DefaultAuralSensor;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.sensor.XMLSensorAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tutorial.unit3.sperling.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private String[][] _labels;
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
	public void configure(Consumer<Character> keyConsumer, char[][] rows) {
		_labels = new String[rows.length][rows[0].length];
		for (int row = 0; row < rows.length; row++)
			for (int col = 0; col < rows[row].length; col++)
				_labels[row][col] = "" + rows[row][col];

		// wire in the consumer
		_keyboard.consumers.put(getAgentIdOfExperimentModel(), keyConsumer);
	}

	@Override
	public void show() {
		IVariableContext context = _experiment.getVariableContext();

		// set the variables
		for (int row = 0; row < _labels.length; row++)
			for (int col = 0; col < _labels[row].length; col++)
				context.set(String.format("%d%d", row, col), _labels[row][col]);

		new XMLSensorAction("org/jactr/tutorial/unit3/sperling/sim/grid.xml", true, _experiment).fire(context);

	}

	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit3/sperling/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		_keyboard.consumers.clear();
	}

	@Override
	public void beep(int beepCount) {

		int hZ = 2000;
		if(beepCount==2) hZ = 1000;
		else if(beepCount==3) hZ = 500;
		Optional<DefaultAuralSensor> aural = CommonReality.getSensors().stream()
				.filter(DefaultAuralSensor.class::isInstance).map(s -> {
					return (DefaultAuralSensor) s;
				}).findFirst();

		final String fContent = ""+hZ;
		aural.ifPresent(as -> {
			as.queueSound(as.newSound(new String[] { "tone" }, fContent, as.getClock().getTime(), 0.5));
		});
	}

}
