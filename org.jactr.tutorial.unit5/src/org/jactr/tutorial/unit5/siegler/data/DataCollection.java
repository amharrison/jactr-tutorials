package org.jactr.tutorial.unit5.siegler.data;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

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

	private int _maxBins = 10; // 0-8, +other
	private Map<String, SummaryStatistics[]> _scoreStatistics = new TreeMap<>();
	private Map<String, SummaryStatistics[]> _groupScoreStatistics = new TreeMap<>();
	private SummaryStatistics[] EMPTY = allocateStats();

	private DataCollection() {

	}

	private SummaryStatistics[] allocateStats() {
		SummaryStatistics[] rtn = new SummaryStatistics[_maxBins];
		for (int i = 0; i < _maxBins; i++)
			rtn[i] = new SummaryStatistics();
		return rtn;
	}

	public void logData(String condition, int response) {

		SummaryStatistics[] stats = _scoreStatistics.computeIfAbsent(condition, (key) -> {
			return allocateStats();
		});

		if (response < 0 || response >= _maxBins)
			response = _maxBins - 1;

		stats[response].addValue(1);
	}

	public Set<String> getConditions() {
		return new TreeSet<>(_groupScoreStatistics.keySet());
	}

	public SummaryStatistics[] getGroupAccuracy(String condition) {
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
			SummaryStatistics[] stats = _groupScoreStatistics.computeIfAbsent(entry.getKey(), (key) -> {
				return allocateStats();
			});

			// sum over the condition
			long sum = 0;
			for (SummaryStatistics stat : entry.getValue())
				sum += stat.getN();

			for (int i = 0; i < stats.length; i++) {
				if (sum == 0)
					stats[i].addValue(0);
				else {
					double percentage = (double) entry.getValue()[i].getN() / sum;
					stats[i].addValue(percentage);
				}
			}

		});
		_scoreStatistics.clear();
	}

	public void clear() {
		_scoreStatistics.clear();
		_groupScoreStatistics.clear();
	}
}
