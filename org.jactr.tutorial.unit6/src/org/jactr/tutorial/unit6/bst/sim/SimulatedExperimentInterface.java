package org.jactr.tutorial.unit6.bst.sim;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.function.Consumer;

import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.sensor.XMLSensorAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tutorial.unit6.bst.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface {

	private IExperiment _experiment;
	private int _a, _b, _c, _goal, _current;
	private SimulatedMouse _mouse;
	private Coordinates _coordinates = new Coordinates(33, 86.8);
	private StringBuilder _response = new StringBuilder();
	private Consumer<String> _responseConsumer;

	public SimulatedExperimentInterface(IExperiment experiment) {
		_experiment = experiment;
		_mouse = new SimulatedMouse(this::buttonPressed);

		// pulled from display.xml
		_mouse.addButton("A", new Point2D.Double(-2, 2));
		_mouse.addButton("B", new Point2D.Double(-2, 1));
		_mouse.addButton("C", new Point2D.Double(-2, 0));
		_mouse.addButton("Reset", new Point2D.Double(-2, -2));

		DefaultKeyboardSensor keyboardSensor = (DefaultKeyboardSensor) CommonReality.getSensors().stream()
				.filter((s) -> {
					return s instanceof DefaultKeyboardSensor;
				}).findFirst().get();

		keyboardSensor.setActuator(_mouse);
	}

	@Override
	public void configure(Consumer<String> responseSequence, int a, int b, int c, int goal) {
		_a = a;
		_b = b;
		_c = c;
		_current = 0;
		_goal = goal;
		_response = new StringBuilder();
		_responseConsumer = responseSequence;

		IVariableContext context = _experiment.getVariableContext();
		context.set("aWidth", "1," + guessWidth(_a));
		context.set("aFeature", (double) _a);
		context.set("bWidth", "1," + guessWidth(_b));
		context.set("bFeature", (double) _b);
		context.set("cWidth", "1," + guessWidth(_c));
		context.set("cFeature", (double) _c);
		context.set("goalWidth", "1," + guessWidth(_goal));
		context.set("goalFeature", (double) _goal);
		context.set("currentWidth", "1," + guessWidth(0));
		context.set("currentFeature", (double) _current);
	}

	private Double guessWidth(int i) {

		// in the gui we render them 2x
		Point p = new Point(2 * i, 0);
		Point2D center = _coordinates.toCenterOfScreen(p, null);
		Point2D cm = _coordinates.toCentimeters(center);
		Point2D retino = _coordinates.toRetinotopic(cm);

		return retino.getX();
	}

	@Override
	public void show() {
		new XMLSensorAction("org/jactr/tutorial/unit6/bst/sim/display.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void clear() {
		new XMLSensorAction("org/jactr/tutorial/unit6/bst/sim/clear.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	@Override
	public void dispose() {

	}

	public void update() {
		_experiment.getVariableContext().set("currentWidth", "1," + guessWidth(_current));
		_experiment.getVariableContext().set("currentFeature", (double) _current);

		new XMLSensorAction("org/jactr/tutorial/unit6/bst/sim/update.xml", true, _experiment)
				.fire(_experiment.getVariableContext());
	}

	public void buttonPressed(String button) {

		int transform = 0;
		switch (button) {
		case "A":
			transform = _a;
			break;
		case "B":
			transform = _b;
			break;
		case "C":
			transform = _c;
			break;
		case "Reset":
			_current = 0;
			transform = _current;
			break;
		}

		_response.append(button.charAt(0));

		if (_current > _goal)
			_current = _current - transform;
		else
			_current = _current + transform;
		update();

		if (_current == _goal) {
			// inject feedback
			new XMLSensorAction("org/jactr/tutorial/unit6/bst/sim/done.xml", true, _experiment)
					.fire(_experiment.getVariableContext());

			_responseConsumer.accept(_response.toString());
		}
	}
}
