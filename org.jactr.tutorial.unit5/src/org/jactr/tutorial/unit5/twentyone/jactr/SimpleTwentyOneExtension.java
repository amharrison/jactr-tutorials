package org.jactr.tutorial.unit5.twentyone.jactr;

import java.util.Collection;
import java.util.TreeSet;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.DoubleParameterProcessor;
import org.jactr.core.utils.parameter.IntegerParameterProcessor;
import org.jactr.core.utils.parameter.ParameterHelper;

public class SimpleTwentyOneExtension implements IExtension {

	private IModel _model;
	private double _winLossRatio;
	private IChunk _winChunk, _loseChunk, _bustChunk;
	private int _dealerStandsOn = 15;
	private int _handsToPlay = 100;
	private PerCycleProcessor _cycleProcessor = new PerCycleProcessor(this);
	private ParameterHelper _parameterHelper = new ParameterHelper();

	public SimpleTwentyOneExtension() {
		_parameterHelper.addProcessor(
				new DoubleParameterProcessor("WinLossRatio", this::setWinLossRatio, this::getWinLossRatio));
		_parameterHelper.addProcessor(
				new IntegerParameterProcessor("DealerStandsOn", this::setDealerStandsOn, this::getDealerStandsOn));
		_parameterHelper
				.addProcessor(new IntegerParameterProcessor("HandsToPlay", this::setHandsToPlay, this::getHandsToPlay));
	}

	public void setResponse(String response) {
		_cycleProcessor.setResponse(response);
	}

	@Override
	public void initialize() throws Exception {
		/*
		 * we initialize second. This is where we grab references to chunks, types and
		 * buffers
		 */
		try {
			_winChunk = getModel().getDeclarativeModule().getChunk("win").get();
			_loseChunk = getModel().getDeclarativeModule().getChunk("lose").get();
			_bustChunk = getModel().getDeclarativeModule().getChunk("bust").get();
		} catch (Exception e) {

		}
	}

	public IChunk getWinChunk() {
		return _winChunk;
	}

	public IChunk getLoseChunk() {
		return _loseChunk;
	}

	public IChunk getBustChunk() {
		return _bustChunk;
	}

	@Override
	public void install(IModel model) {
		/*
		 * called first, extensions are installed 1:1. This is where we attach our
		 * listeners
		 */
		_model = model;
		/*
		 * because we use an inline executor, the cycle processor is notified
		 * synchronously of the start of the cycle. The model will block until this
		 * event handler returns.
		 */
		_model.addListener(_cycleProcessor, ExecutorServices.INLINE_EXECUTOR);
	}

	@Override
	public void uninstall(IModel model) {
		/*
		 * finally this is called to remove our listeners
		 */
		_model = null;
	}

	@Override
	public IModel getModel() {
		return _model;
	}

	@Override
	public String getName() {
		return "SimpleTwentyOne";
	}

	public double getWinLossRatio() {
		return _winLossRatio;
	}

	public void setWinLossRatio(double ratio) {
		_winLossRatio = ratio;
	}

	public void setDealerStandsOn(int standsOn) {
		_dealerStandsOn = standsOn;
	}

	public int getDealerStandsOn() {
		return _dealerStandsOn;
	}

	public int getHandsToPlay() {
		return _handsToPlay;
	}

	public void setHandsToPlay(int handsToPlay) {
		_handsToPlay = handsToPlay;
	}

	@Override
	public void setParameter(String key, String value) {
		_parameterHelper.setParameter(key, value);
	}

	@Override
	public String getParameter(String key) {
		return _parameterHelper.getParameter(key);
	}

	@Override
	public Collection<String> getPossibleParameters() {
		return _parameterHelper.getParameterNames(new TreeSet<>());
	}

	@Override
	public Collection<String> getSetableParameters() {
		return _parameterHelper.getSetableParameterNames(new TreeSet<>());
	}

}
