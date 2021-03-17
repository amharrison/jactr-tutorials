package org.jactr.tutorial.unit6.probability.data;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;

public class Analyzer implements ISliceAnalyzer {

	static public final Map<String, Double> REFERENCE_DATA = new TreeMap<>();
	static {
		REFERENCE_DATA.put("1", 0.664);
		REFERENCE_DATA.put("2", 0.778);
		REFERENCE_DATA.put("3", 0.804);
		REFERENCE_DATA.put("4", 0.818);
	}

	/**
	 * run any analyses that you desire. Besure to clear your data collection
	 * mechanism between runs.
	 */
	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {
		
		return null;
	}

}
