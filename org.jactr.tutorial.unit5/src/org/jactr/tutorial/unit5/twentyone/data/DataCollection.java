package org.jactr.tutorial.unit5.twentyone.data;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jactr.tutorial.unit5.twentyone.jactr.PerCycleProcessor;

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

	private SummaryStatistics[] _groupScoreStatistics = new SummaryStatistics[PerCycleProcessor.TOTAL_BINS];

	private DataCollection() {
		for (int i = 0; i < _groupScoreStatistics.length; i++)
			_groupScoreStatistics[i] = new SummaryStatistics();
	}

	public void logData(Double[] values) {

		for (int i = 0; i < values.length; i++)
			_groupScoreStatistics[i].addValue(values[i]);
	}

	public double[] getBins() {
		double[] rtn = new double[PerCycleProcessor.TOTAL_BINS];
		for (int i = 0; i < rtn.length; i++) {
			rtn[i] = _groupScoreStatistics[i].getMean();
			if (Double.isNaN(rtn[i]))
				rtn[i] = 0;
		}
		return rtn;
	}

	public double[] getStdErrs() {
		double[] rtn = new double[PerCycleProcessor.TOTAL_BINS];

		for (int i = 0; i < rtn.length; i++) {
			rtn[i] = _groupScoreStatistics[i].getStandardDeviation() / Math.sqrt(_groupScoreStatistics[i].getN());
			if (Double.isNaN(rtn[i]))
				rtn[i] = 0;
		}

		return rtn;
	}

	public void clear() {
		for (SummaryStatistics stats : _groupScoreStatistics)
			stats.clear();
	}

}
