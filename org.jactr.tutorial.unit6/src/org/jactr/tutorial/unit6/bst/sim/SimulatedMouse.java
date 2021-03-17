package org.jactr.tutorial.unit6.bst.sim;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.commonreality.modalities.motor.TranslateCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.keyboard.DefaultActuator;
import org.commonreality.sensors.keyboard.PressCommand;

public class SimulatedMouse extends DefaultActuator {

	private Map<String, Point2D> _buttonLocations = new TreeMap<>();

	private Point2D _currentPosition = new Point2D.Double();

	private Consumer<String> _buttonHandler;

	public SimulatedMouse(Consumer<String> buttonHandler) {
		_buttonHandler = buttonHandler;
	}

	public void addButton(String button, Point2D point) {
		_buttonLocations.put(button, point);
	}

	protected void press(PressCommand command, EfferentCommandHandler handler) {

		String closestButton = findClosestButton(_currentPosition, 0.5);
		if (closestButton != null)
			_buttonHandler.accept(closestButton);
	}

	protected void positionMouse(TranslateCommand command, EfferentCommandHandler handler, IEfferentObject mouse,
			double[] position) {
		_currentPosition.setLocation(position[0], position[1]);
	}

	protected String findClosestButton(Point2D reference, double tolerance) {
		String best = null;
		double min = Double.MAX_VALUE;
		for (Map.Entry<String, Point2D> entry : _buttonLocations.entrySet()) {
			double distance = reference.distance(entry.getValue());
			if (distance < min && distance < tolerance) {
				min = distance;
				best = entry.getKey();
			}
		}

		return best;
	}
}
