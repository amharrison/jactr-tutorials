package org.jactr.tutorial.unit3.subitize.sim;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.object.IAgentObject;
import org.commonreality.sensors.speech.DefaultSpeechSensor;
import org.commonreality.sensors.speech.ISpeaker;

public class SimulatedSpeaker implements ISpeaker {

	Optional<Consumer<String>> _callback = Optional.empty();
	
	public void setSpeakerCallback(Consumer<String> consumer)
	{
		_callback = Optional.of(consumer);
	}
	
	@Override
	public void speak(IAgentObject speaker, VocalizationCommand vocalization) {
		_callback.ifPresent(c->{
			c.accept(vocalization.getText());
		});
	}

	@Override
	public void configure(DefaultSpeechSensor sensor, Map<String, String> options) {
	}

	@Override
	public void subvocalize(IAgentObject speaker, VocalizationCommand vocalization) {
		
		
	}

}
