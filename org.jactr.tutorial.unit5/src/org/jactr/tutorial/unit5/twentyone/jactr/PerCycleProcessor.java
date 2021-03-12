package org.jactr.tutorial.unit5.twentyone.jactr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.commonreality.time.IClock;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.fluent.FluentChunk;
import org.jactr.tutorial.unit5.twentyone.cards.ICard;
import org.jactr.tutorial.unit5.twentyone.cards.IPlayer;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.TwentyOneEngine.Result;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities;
import org.jactr.tutorial.unit5.twentyone.cards.twentyone.impl.TwentyOneUtilities.Sum;
import org.jactr.tutorial.unit5.twentyone.data.DataCollection;
import org.jactr.tutorial.unit5.twentyone.jactr.internal.ModelPlayer;
import org.jactr.tutorial.unit5.twentyone.jactr.internal.SimpleTwentyOneGame;

public class PerCycleProcessor extends ModelListenerAdaptor {

	static public final int TOTAL_BINS = 20;
	private int MAX_HANDS = 100;
	
	private SimpleTwentyOneExtension _extension;
	private SimpleTwentyOneGame _game;
	private IPlayer _player;

	private String _response;
	private Runnable _postActionPhase;
	private Runnable _postLearningPhase;

	private int _binSize;
	private SummaryStatistics _summary = new SummaryStatistics();
	private List<Double> _summaryHistory = new ArrayList<>();

	public PerCycleProcessor(SimpleTwentyOneExtension extension) {
		ModelPlayer player = new ModelPlayer("model");
		_game = new SimpleTwentyOneGame(player, extension);
		_player = player;
		_extension = extension;
		MAX_HANDS = _extension.getHandsToPlay();
		_binSize = MAX_HANDS / TOTAL_BINS;

		/*
		 * this is called 10s after the start of the hand
		 */
		_postActionPhase = () -> {
			if (_response == null)
				_response = "stand"; // force the action

			IModel model = _extension.getModel();
			Result result = _game.finishHand(_response);
			IActivationBuffer goalBuffer = model.getActivationBuffer(IActivationBuffer.GOAL);
			IChunk goalChunk = goalBuffer.getSourceChunk();

			try {
				updateGoalChunk(goalChunk, result);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result == Result.WIN || result == Result.BLACKJACK)
				_summary.addValue(1);
			else
				_summary.addValue(0);

			if (_summary.getN() % _binSize == 0) {
				double percent = _summary.getMean();
				if (Double.isNaN(percent))
					percent = 0;
				_summaryHistory.add(percent);
				_summary.clear();

				_extension.setWinLossRatio(percent);
			}

			IClock clock = ACTRRuntime.getRuntime().getClock(model);
			
			if(_summaryHistory.size()<TOTAL_BINS)
			  model.getTimedEventQueue().enqueue(new RunnableTimedEvent(clock.getTime() + 10, _postLearningPhase));
			else
			{
				DataCollection.get().logData(_summaryHistory.toArray(new Double[0]));
				
				throw new ModelTerminatedException("End of the run");
			}
		};

		/*
		 * this is called 10 after the actionphase and represents the start.
		 */
		_postLearningPhase = () -> {
			IModel model = _extension.getModel();
			IClock clock = ACTRRuntime.getRuntime().getClock(model);
			IActivationBuffer goalBuffer = model.getActivationBuffer(IActivationBuffer.GOAL);
			IChunk goalChunk = goalBuffer.getSourceChunk();

			_game.startHand();
			try {
				goalChunk = buildGoal(goalChunk, model);
				goalBuffer.addSourceChunk(goalChunk);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if(_summaryHistory.size()<TOTAL_BINS)
			  model.getTimedEventQueue().enqueue(new RunnableTimedEvent(clock.getTime() + 10, _postActionPhase));
		};
	}

	public void setResponse(String response) {
		_response = response;
	}

	public void modelStarted(ModelEvent me) {
		_postLearningPhase.run();
	}

	public void cycleStarted(ModelEvent me) {

	}

	private IChunk buildGoal(IChunk goal, IModel model) throws Exception {

		IChunk start = model.getDeclarativeModule().getChunk("start").get();
		int mc1 = valueOf(_player.getHand().get(0));
		int mc2 = valueOf(_player.getHand().get(1));
		int mstart = TwentyOneUtilities.greatestNotBusting(TwentyOneUtilities.sums(_player.getHand())).value();
		int oc1 = valueOf(_game.getDealer().getVisibleHand().get(0));
		int ostart = TwentyOneUtilities.greatestNotBusting(TwentyOneUtilities.sums(_game.getDealer().getVisibleHand()))
				.value();

		IChunkType gameState = model.getDeclarativeModule().getChunkType("game-state").get();

		FluentChunk fc = FluentChunk.from(gameState);
		if (goal != null)
			fc = FluentChunk.from(goal);

		return fc.slot("state", start).slot("mc1", mc1).slot("mc2", mc2).slot("mstart", mstart).slot("oc1", oc1)
				.slot("ostart", ostart).build();
	}

	private void updateGoalChunk(IChunk goalChunk, Result result) throws Exception {
		IModel model = goalChunk.getModel();
		IChunk results = model.getDeclarativeModule().getChunk("results").get();
		/*
		 * we can also use FluentChunk for the batch, thread safe setting of slots
		 */
		FluentChunk fc = FluentChunk.from(goalChunk).slot("state", results);

		if (_player.getHand().size() > 2)
			fc.slot("mc3", valueOf(_player.getHand().get(2)));

		Collection<Sum> playerSums = TwentyOneUtilities.sums(_player.getHand());
		Sum mtot = TwentyOneUtilities.greatestNotBusting(playerSums);
		if (mtot != null) {
			fc.slot("mtot", mtot.value());
			fc.slot("mresult", valueOf(result));
		} else {
			int max = playerSums.stream().map(s -> {
				return s.value();
			}).max(Integer::compare).get();

			fc.slot("mtot", max);
			fc.slot("mresult", _extension.getBustChunk());
		}

		fc.slot("oc2", valueOf(_game.getDealer().getHand().get(1)));
		if (_game.getDealer().getHand().size() > 2)
			fc.slot("oc3", valueOf(_game.getDealer().getHand().get(2)));

		Collection<Sum> dealerSums = TwentyOneUtilities.sums(_game.getDealer().getHand());
		Sum otot = TwentyOneUtilities.greatestNotBusting(dealerSums);
		if (otot != null) {
			fc.slot("otot", otot.value());
			fc.slot("oresult", otherValueOf(result));
		} else {
			int max = dealerSums.stream().map(s -> {
				return s.value();
			}).max(Integer::compare).get();

			fc.slot("otot", max);
			fc.slot("oresult", _extension.getBustChunk());
		}

		// and apply slot changes
		fc.build();
	}

	private IChunk valueOf(Result result) {
		switch (result) {
		case WIN:
			return _extension.getWinChunk();
		case BLACKJACK:
			return _extension.getWinChunk();
		case LOSE:
			return _extension.getLoseChunk();
		case BUST:
			return _extension.getBustChunk();
		default:
			return _extension.getLoseChunk();
		}
	}

	private IChunk otherValueOf(Result result) {
		switch (result) {
		case WIN:
			return _extension.getLoseChunk();
		case BLACKJACK:
			return _extension.getLoseChunk();
		case LOSE:
			return _extension.getWinChunk();
		case BUST:
			return _extension.getWinChunk();
		default: // push
			return _extension.getLoseChunk();
		}
	}

	private int valueOf(ICard card) {
		int value = card.getRank().getValue();
		if (value == 11)
			value = 1;
		return value;
	}

}
