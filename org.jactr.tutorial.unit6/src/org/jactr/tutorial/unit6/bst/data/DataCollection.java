package org.jactr.tutorial.unit6.bst.data;

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

	private Map<String, SummaryStatistics> _groupProductionUtilities = new TreeMap<>();

	private SummaryStatistics EMPTY = new SummaryStatistics();

	private DataCollection() {

	}

	public void logData(String condition, boolean usedOvershoot) {

		SummaryStatistics stats = _scoreStatistics.computeIfAbsent(condition, key -> {
			return new SummaryStatistics();
		});

		if (usedOvershoot)
			stats.addValue(1);
	}

	public Set<String> getConditions() {
		return new TreeSet<>(_groupScoreStatistics.keySet());
	}

	public SummaryStatistics getGroupOvershoot(String condition) {
		return _groupScoreStatistics.getOrDefault(condition, EMPTY);
	}

	public SummaryStatistics getGroupExpectedUtility(String productionName) {
		return _groupProductionUtilities.getOrDefault(productionName, EMPTY);
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
	 * let's grab the utilities of relevant productions
	 * 
	 * @param model
	 */
	public void modelCompleted(IModel model) {
		String[] productions = { "decide-over", "decide-under", "force-over", "force-under" };
		for (String productionName : productions)
			try {
				IProduction production = model.getProceduralModule().getProduction(productionName).get();
				//utilities are a version 6 feature, so we need that type of subsymbolicProduction
				ISubsymbolicProduction6 ssp = production.getAdapter(ISubsymbolicProduction6.class);

				SummaryStatistics stats = _groupProductionUtilities.computeIfAbsent(productionName, key -> {
					return new SummaryStatistics();
				});

				double expectedUtility = ssp.getExpectedUtility(); //this has no noise
				if (Double.isNaN(expectedUtility)) // never fired
					expectedUtility = ssp.getUtility();
				
				
				stats.addValue(expectedUtility);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		_groupProductionUtilities.clear();
		_groupScoreStatistics.clear();
	}
}
