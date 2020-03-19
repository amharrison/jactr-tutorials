package org.jactr.tutorial.unit2.experiment.sim;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.commonreality.identifier.IIdentifier;
import org.commonreality.sensors.handlers.EfferentCommandHandler;
import org.commonreality.sensors.keyboard.DefaultActuator;
import org.commonreality.sensors.keyboard.PressCommand;

public class SimulatedKeyboard extends DefaultActuator {

	static final public Map<IIdentifier, Consumer<Character>> consumers = new HashMap<>();

	static private Consumer<Character> _default = new Consumer<Character>() {

		@Override
		public void accept(Character t) {

		}

	};

	protected void press(PressCommand command, EfferentCommandHandler handler) {
		int keyCode = getCode(command, handler);
		char keyPressed = Character.toLowerCase(KeyEvent.getKeyText(keyCode).charAt(0));

		Consumer<Character> consumer = consumers.getOrDefault(command.getIdentifier().getAgent(), _default);
		consumer.accept(keyPressed);
	}

}
