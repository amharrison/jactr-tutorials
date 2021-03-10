package org.jactr.tutorial.unit5.siegler.sim;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.commonreality.modalities.aural.ICommonTypes;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.object.IAgentObject;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.aural.DefaultAuralSensor;
import org.commonreality.sensors.speech.DefaultSpeechSensor;
import org.commonreality.sensors.speech.ISpeaker;
import org.jactr.tutorial.unit5.siegler.IExperimentInterface;

public class SimulatedExperimentInterface implements IExperimentInterface, ISpeaker {

	private Consumer<String> _speechHandler;
	private int _addend1;
	private int _addend2;

	public SimulatedExperimentInterface() {

		getVocalSensor().ifPresent(das -> {
			das.setSpeaker(this);
		});
	}

	@Override
	public void speak(IAgentObject speaker, VocalizationCommand vocalization) {
		_speechHandler.accept(vocalization.getText());
	}

	@Override
	public void subvocalize(IAgentObject speaker, VocalizationCommand vocalization) {
		// noop
	}

	@Override
	public void configure(DefaultSpeechSensor sensor, Map<String, String> options) {
		// noop
	}

	@Override
	public void configure(Consumer<String> speechHandler, int addend1, int addend2) {
		_speechHandler = speechHandler;
		_addend1 = addend1;
		_addend2 = addend2;
	}

	@Override
	public void announce() {
		Optional<DefaultAuralSensor> das = getAuralSensor();
		das.ifPresent(as -> {
			double onset = as.getClock().getTime();
			double duration = 1;
			as.queueSound(as.newSound(new String[] { ICommonTypes.DIGIT }, "" + _addend1, onset, duration));
			as.queueSound(as.newSound(new String[] { ICommonTypes.DIGIT }, "" + _addend2, onset + duration, duration));
		});
	}

	@Override
	public void dispose() {
		// noop
	}

	private Optional<DefaultAuralSensor> getAuralSensor() {
		return CommonReality.getSensors().stream().filter(DefaultAuralSensor.class::isInstance).map(s -> {
			return (DefaultAuralSensor) s;
		}).findFirst();
	}

	private Optional<DefaultSpeechSensor> getVocalSensor() {
		return CommonReality.getSensors().stream().filter(DefaultSpeechSensor.class::isInstance).map(s -> {
			return (DefaultSpeechSensor) s;
		}).findFirst();
	}

}
