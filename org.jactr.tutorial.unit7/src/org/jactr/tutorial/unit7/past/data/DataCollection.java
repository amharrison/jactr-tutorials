package org.jactr.tutorial.unit7.past.data;

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

	private SummaryStatistics[] _scoreStatistics = allocate();

	private SummaryStatistics[] allocate() {
		SummaryStatistics[] rtn = new SummaryStatistics[3];
		for (int i = 0; i < 3; i++)
			rtn[i] = new SummaryStatistics();
		return rtn;
	}

	private DataCollection() {

	}
	
	public SummaryStatistics getRegularlyInflected() {
		return _scoreStatistics[0];
	}
	
	public SummaryStatistics getNotInflected() {
		return _scoreStatistics[1];
	}
	
	public SummaryStatistics getCorrectlyInflected() {
		return _scoreStatistics[2];
	}
	

	public void logData(boolean wasIrregular, boolean irregularRegularInflection,
			boolean irregularWasNotInflected, boolean irregularCorrectInflection) {
		
		if(!wasIrregular) return;
		
	
		_scoreStatistics[0].addValue(irregularRegularInflection?1:0);
		_scoreStatistics[1].addValue(irregularWasNotInflected?1:0);
		_scoreStatistics[2].addValue(irregularCorrectInflection?1:0);
	}

	public void clear() {
		for (SummaryStatistics s : _scoreStatistics)
			s.clear();
	}
}
