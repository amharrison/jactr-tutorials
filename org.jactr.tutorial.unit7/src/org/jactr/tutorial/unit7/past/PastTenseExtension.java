package org.jactr.tutorial.unit7.past;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.commonreality.time.IClock;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.six.learning.IProceduralLearningModule6;
import org.jactr.core.module.procedural.six.learning.event.IProceduralLearningModule6Listener;
import org.jactr.core.module.procedural.six.learning.event.ProceduralLearningEvent;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.DoubleParameterProcessor;
import org.jactr.core.utils.parameter.IntegerParameterProcessor;
import org.jactr.core.utils.parameter.ParameterHelper;
import org.jactr.fluent.FluentChunk;
import org.jactr.tutorial.unit7.past.data.DataCollection;
import org.jactr.tutorial.unit7.past.internal.VerbInfo;

public class PastTenseExtension implements IExtension {

	private List<VerbInfo> _wordFrequencyList;

	private final boolean _debug = false;
	private int _maxTrials = 50000;
	private int _reportInterval = 100;

	private IModel _model;
	private ParameterHelper _parameterHelper = new ParameterHelper();
	private IModelListener _modelListener;
	private IProceduralLearningModule6Listener _procLearningListener;

	private IChunkType _pastTense;
	private IChunkType _goalType;
	private IChunk _blank;
	private Map<String, IChunk> _verbChunks;

	private Runnable _finishTrial; // fired 100 seconds after start
	private Runnable _startTrial;
	private VerbInfo _currentQuery;
	private int _count;

	private boolean _rewarded;

	private double _regularlyInflected = 0;
	private double _notInflected = 0;
	private double _correctlyInflexted = 0;
	private DescriptiveStatistics _averageCorrectlyInflected = new DescriptiveStatistics(10);

	public PastTenseExtension() {

		/**
		 * parameterHelper helps deal with the requirements of IParameterized, which
		 * enables full serialization of parameters. Or in this case, tracking of
		 * interval values. We will use these parameters to graph the performance of the
		 * model in the ide.
		 */
		_parameterHelper.addProcessor(
				new IntegerParameterProcessor("MaximumTrials", this::setMaximumTrials, this::getMaximumTrials));
		_parameterHelper.addProcessor(
				new IntegerParameterProcessor("ReportInterval", this::setReportInterval, this::getReportInterval));

		_parameterHelper.addProcessor(new DoubleParameterProcessor("RegularlyInflected", this::setRegularlyInflected,
				this::getRegularlyInflected));
		_parameterHelper.addProcessor(new DoubleParameterProcessor("CorrectInflected", this::setCorrectlyInflected,
				this::getCorrectlyInflected));
		_parameterHelper.addProcessor(
				new DoubleParameterProcessor("NotInflected", this::setNotInflected, this::getNotInflected));
		_parameterHelper.addProcessor(
				new DoubleParameterProcessor("AverageCorrectlyInflected", null, this::getAverageCorrectlyInflected));

		/**
		 * What we do at the start of a trial
		 */
		_startTrial = () -> {

			double now = getCurrentTime();

			_rewarded = false;
			_count++;
			_currentQuery = randomVerb();

			addPastTenseToMemory();
			addPastTenseToMemory();
			makeGoalAndImaginal(_currentQuery);

			/*
			 * queue the timeout
			 */
			_model.getTimedEventQueue().enqueue(new RunnableTimedEvent(now + 100, _finishTrial));
		};

		/**
		 * And this at the end of the 100second window
		 */
		_finishTrial = () -> {
			IActivationBuffer goal = _model.getActivationBuffer(IActivationBuffer.GOAL);
			IActivationBuffer imaginal = _model.getActivationBuffer(IActivationBuffer.IMAGINAL);
			IChunk imaginalChunk = imaginal.getSourceChunk();
			/*
			 * we set state to null after the reward.
			 */

			Object state = goal.getSourceChunk().getSymbolicChunk().getSlot("state").getValue();
			if (null == state) {
				if (!_rewarded)
					System.err.println("Warning : Model not rewarded?");

				recordResponse(imaginalChunk);
			} else
				System.err.format("Warning : Model not done (%s) after 100s? Ignoring\n", state);

			imaginal.removeSourceChunk(imaginalChunk);

			/*
			 * update our numbers
			 */
			if (_count % _reportInterval == 0)
				updateStats();
			/*
			 * queue up the next trial, if possible. At the end, throw MTE to cleanly exit.
			 */
			if (_count < _maxTrials)
				_model.getTimedEventQueue().enqueue(new RunnableTimedEvent(getCurrentTime() + 100, _startTrial));
			else
				throw new ModelTerminatedException("Max trials reached");
		};

		/*
		 * used to get the start of the model run.
		 */
		_modelListener = new ModelListenerAdaptor() {
			@Override
			public void modelStarted(ModelEvent me) {
				/*
				 * install verbs if not already available
				 */
				encodeVerbs();
				_startTrial.run();
			}

		};
		/*
		 * used to track reward status
		 */
		_procLearningListener = new IProceduralLearningModule6Listener() {

			@Override
			public void stopReward(ProceduralLearningEvent event) {
				_rewarded = true;
			}

			@Override
			public void startReward(ProceduralLearningEvent event) {

			}

			@Override
			public void rewarded(ProceduralLearningEvent event) {

			}

			@Override
			public void productionCompiled(ProceduralLearningEvent event) {

			}

			@Override
			public void productionNotCompiled(ProceduralLearningEvent event) {

			}
		};
	}

	private double getCurrentTime() {
		return ACTRRuntime.getRuntime().getClock(getModel()).getTime();
	}

	/**
	 * update our internal statistics
	 */
	private void updateStats() {

		double ci = DataCollection.get().getCorrectlyInflected().getSum();
		double value = DataCollection.get().getCorrectlyInflected().getMean();
		if (Double.isNaN(value))
			value = 0;
		setCorrectlyInflected(value);

		double ri = DataCollection.get().getRegularlyInflected().getSum();
		value = DataCollection.get().getRegularlyInflected().getMean();
		if (Double.isNaN(value))
			value = 0;
		setRegularlyInflected(value);

		value = DataCollection.get().getNotInflected().getMean();
		if (Double.isNaN(value))
			value = 0;
		setNotInflected(value);

		DataCollection.get().clear();

		_averageCorrectlyInflected.addValue(ci / (ci + ri));

		double percentDone = _count * 100.0 / (double) _maxTrials;
		System.out.format("[%.2f%%, %.2f%%]\n", percentDone, 100 * _averageCorrectlyInflected.getMean());
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

	public double getRegularlyInflected() {
		return _regularlyInflected;
	}

	public void setRegularlyInflected(double percentage) {
		_regularlyInflected = percentage;
	}

	public double getNotInflected() {
		return _notInflected;
	}

	public void setNotInflected(double not) {
		_notInflected = not;
	}

	public double getCorrectlyInflected() {
		return _correctlyInflexted;
	}

	public void setCorrectlyInflected(double correct) {
		_correctlyInflexted = correct;
	}

	public void setMaximumTrials(int max) {
		_maxTrials = max;
	}

	public int getMaximumTrials() {
		return _maxTrials;
	}

	public int getReportInterval() {
		return _reportInterval;
	}

	public void setReportInterval(int interval) {
		_reportInterval = interval;
	}

	public double getAverageCorrectlyInflected() {
		return _averageCorrectlyInflected.getMean();
	}

	/**
	 * populate the word frequency list. Called after install, but before run
	 */
	@Override
	public void initialize() throws Exception {
		_wordFrequencyList = new ArrayList<>(35000);
		Map<String, VerbInfo> database = VerbInfo.getDatabase();
		database.keySet().forEach(key -> {
			VerbInfo vi = database.get(key);
			for (int i = 0; i < vi.frequency; i++)
				_wordFrequencyList.add(vi);

		});
		// make sure we use the model's random generator if available
		Collections.shuffle(_wordFrequencyList, getRandomGenerator());
	}

	@Override
	public void install(IModel model) {
		_model = model;

		_model.getDeclarativeModule().getChunkType("past-tense").thenAccept(ct -> {
			_pastTense = ct;
		});
		_model.getDeclarativeModule().getChunk("blank").thenAccept(c -> {
			_blank = c;
		});
		_model.getDeclarativeModule().getChunkType("goal").thenAccept(ct -> {
			_goalType = ct;
		});

		_model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);
		((IProceduralLearningModule6) _model.getModule(IProceduralLearningModule6.class))
				.addListener(_procLearningListener, ExecutorServices.INLINE_EXECUTOR);
	}

	@Override
	public void uninstall(IModel model) {
		model.removeListener(_modelListener);
		((IProceduralLearningModule6) model.getModule(IProceduralLearningModule6.class))
				.removeListener(_procLearningListener);
		_model = null;
	}

	@Override
	public IModel getModel() {
		return _model;
	}

	@Override
	public String getName() {
		return "PastTense";
	}

	/**
	 * create the imaginal representation of current and a new goal
	 * 
	 * @param current
	 */
	protected IChunk makeGoalAndImaginal(VerbInfo current) {

		IChunk goal = FluentChunk.from(_goalType).build();
		_model.getActivationBuffer(IActivationBuffer.GOAL).addSourceChunk(goal);

		IChunk imaginal = FluentChunk.from(_pastTense).slot("verb", _verbChunks.get(current.verb)).build();
		_model.getActivationBuffer(IActivationBuffer.IMAGINAL).addSourceChunk(imaginal);
		return imaginal;
	}

	protected void addPastTenseToMemory() {
		VerbInfo word = randomVerb();

		/*
		 * assemble the chunk then encode
		 */
		FluentChunk.from(_pastTense).slot("verb", _verbChunks.get(word.verb)).slot("stem", _verbChunks.get(word.stem))
				.slot("suffix", word.isIrregular ? _blank : "ed").encode();
	}

	private VerbInfo randomVerb() {
		int index = getRandomGenerator().nextInt(_wordFrequencyList.size());
		VerbInfo info = _wordFrequencyList.get(index);
		return info;
	}

	private Random getRandomGenerator() {
		IRandomModule randomModule = (IRandomModule) _model.getModule(IRandomModule.class);
		if (randomModule != null)
			return randomModule.getGenerator();

		return new Random();
	}

	protected void encodeVerbs() {
		Map<String, VerbInfo> database = VerbInfo.getDatabase();
		Collection<String> verbs = new TreeSet<>(database.keySet());

		/*
		 * make sure we encode the irregular stems too
		 */
		database.values().forEach(vi -> {
			if (!vi.verb.equals(vi.stem))
				verbs.add(vi.stem);
		});

		/*
		 * asynchronous use of getChunkType.
		 */
		_model.getDeclarativeModule().getChunkType("chunk").thenAccept(chunkType -> {
			/*
			 * create the chunks for the verbs
			 */
			_verbChunks = FluentChunk.from(chunkType).chunks(verbs.toArray(new String[0]));
		});

	}

	/**
	 * record the response based on the imaginalChunk and the currentQuery
	 * 
	 * @param imaginalChunk
	 */
	protected void recordResponse(IChunk imaginalChunk) {
		ISymbolicChunk sc = imaginalChunk.getSymbolicChunk();

		String verb = sc.getSlot("verb").getValue().toString();

		if (!verb.equals(_currentQuery.verb)) {
			System.err.println("WARNING : Incorrectly formatted verb");
			return;
		}

		Object stem = sc.getSlot("stem").getValue(); // null or chunk
		Object suffix = sc.getSlot("suffix").getValue(); // blank or "ed" or null

		boolean isIrregular = _currentQuery.isIrregular;

		boolean respondedAtAll = suffix != null && stem != null;
		if (respondedAtAll) {
			suffix = suffix.toString();
			stem = stem.toString();
		}

		boolean isResponseBlank = "blank".equals(suffix);
		boolean regularResponse = verb.equals(stem) && "ed".equals(suffix);
		boolean correctResponse = isResponseBlank && isIrregular && _currentQuery.stem.equals(stem);
		boolean regularShouldBeIrregular = regularResponse && _currentQuery.isIrregular;

		if (_debug)
			if (correctResponse)
				System.err.print("+");
			else if (regularResponse)
				if (regularShouldBeIrregular)
					System.err.print("|");
				else
					System.err.print(".");
			else if (!respondedAtAll)
				if (_currentQuery.isIrregular)
					System.err.print("O");
				else
					System.err.print("o");

		DataCollection.get().logData(isIrregular, regularResponse, !respondedAtAll, correctResponse);
	}

}
