package org.jactr.tutorial.unit4.paired.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

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

	private Map<String, SummaryStatistics[]> _scoreStatistics = new TreeMap<>();
	private Map<String, SummaryStatistics[]> _groupScoreStatistics = new TreeMap<>();
	private SummaryStatistics[] EMPTY = new SummaryStatistics[] { new SummaryStatistics(), new SummaryStatistics() };

	private DataCollection() {

	}

	public void logData(String condition, boolean onlyLogAccurateTimes, boolean accurate, double latency) {

		SummaryStatistics[] stats = _scoreStatistics.computeIfAbsent(condition, (key) -> {
			return new SummaryStatistics[] { new SummaryStatistics(), new SummaryStatistics() };
		});

		stats[0].addValue(accurate ? 1 : 0);
		if (accurate && onlyLogAccurateTimes)
			stats[1].addValue(latency);
		else if (!onlyLogAccurateTimes)
			stats[1].addValue(latency);
	}

	public Collection<String> getConditions() {
		return new ArrayList<>(_groupScoreStatistics.keySet());
	}

	public SummaryStatistics getGroupAccuracy(String condition) {
		return _groupScoreStatistics.getOrDefault(condition, EMPTY)[0];
	}

	public SummaryStatistics getGroupLatency(String condition) {
		return _groupScoreStatistics.getOrDefault(condition, EMPTY)[1];
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
			SummaryStatistics[] stats = _groupScoreStatistics.computeIfAbsent(entry.getKey(), (key) -> {
				return new SummaryStatistics[] { new SummaryStatistics(), new SummaryStatistics() };
			});
			double value = entry.getValue()[0].getMean();
			if (Double.isNaN(value))
				value = 0;
			stats[0].addValue(value);

			value = entry.getValue()[1].getMean();
			if (Double.isNaN(value))
				value = 0;
			stats[1].addValue(value);
		});
		_scoreStatistics.clear();
	}

	public void clear() {
		_scoreStatistics.clear();
		_groupScoreStatistics.clear();
	}
}
