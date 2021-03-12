package org.jactr.tutorial.unit5.twentyone.jactr;

import org.jactr.core.utils.similarity.ISimilarityHandler;

public class NumericSimilarityHandler implements ISimilarityHandler {

	@Override
	public boolean handles(Object one, Object two) {
		boolean rtn = one instanceof Number && two instanceof Number;
		return rtn;
	}

	@Override
	public double computeSimilarity(Object one, Object two, double maxDiff, double maxSim) {
		
		double a = ((Number)one).doubleValue();
		double b = ((Number)two).doubleValue();
		
		double sim = -Math.abs(a-b)/Math.max(a, b);
		return sim;
	}

}
