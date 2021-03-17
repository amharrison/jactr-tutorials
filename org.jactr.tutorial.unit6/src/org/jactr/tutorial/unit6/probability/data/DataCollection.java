package org.jactr.tutorial.unit6.probability.data;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;

/**
 * Data collection singleton that works for any named conditions, collecting
 * accuracy and latency.
 * 
 * @author harrison
 *
 */
public class DataCollection {

	static public DataCollection get() {
		return _instance;
	}

	static private DataCollection _instance = new DataCollection();

	private Map<String, SummaryStatistics> _scoreStatistics = new TreeMap<>();
	private Map<String, SummaryStatistics> _groupScoreStatistics = new TreeMap<>();

	
	private SummaryStatistics EMPTY = new SummaryStatistics();

	private DataCollection() {

	}

	public void logData(String condition, boolean choseHeads) {

		SummaryStatistics stats = _scoreStatistics.computeIfAbsent(condition, key -> {
			return new SummaryStatistics();
		});

		if (choseHeads)
			stats.addValue(1);
	}

	public Set<String> getConditions() {
		return new TreeSet<>(_groupScoreStatistics.keySet());
	}

	public SummaryStatistics getGroupOvershoot(String condition) {
		return _groupScoreStatistics.getOrDefault(condition, EMPTY);
	}

	
	public boolean hasSubject() {
		return _scoreStatistics.size() > 0;
	}

	/**
	 * if something goes wrong, wipe the subject's data
	 */
	public void wipeSubject() {
		_scoreStatistics.clear();
	}

	
	/**
	 * migrate data from individual to group summaries prepare for the next subject
	 */
	public void subjectCompleted() {

		_scoreStatistics.entrySet().stream().forEach((entry) -> {
			SummaryStatistics stats = _groupScoreStatistics.computeIfAbsent(entry.getKey(), (key) -> {
				return new SummaryStatistics();
			});

			double value = entry.getValue().getMean();
			if (Double.isNaN(value))
				value = 0;
			stats.addValue(value);
		});
		_scoreStatistics.clear();
	}

	public void clear() {
		_scoreStatistics.clear();
		_groupScoreStatistics.clear();
	}
}
