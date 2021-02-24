package org.jactr.tutorial.unit4.paired.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;

/**
 * statistical analysis code for Unit 3 sperling.
 * 
 * @author harrison
 *
 */
public class Analyzer implements ISliceAnalyzer {

	/** 
	 * reference data
	 */
	static private final double[] ACCURACY_DATA = { 0, 0.526, 0.667, 0.798, 0.887, 0.924, 0.958 };
	static private final double[] LATENCY_DATA = { 0, 2.156, 1.967, 1.762, 1.680, 1.552, 1.467 };

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		//make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Collection<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != ACCURACY_DATA.length)
			throw new RuntimeException("Unexpected number of conditions " + conditions.size());

		/**
		 * generate a simple summary report file for this analysis
		 */
		saveGroupNumbers(sliceAnalysis);
		
		/**
		 * repackage the data for fitsß
		 */
		double[][] accuracyFitData = new double[ACCURACY_DATA.length][2];
		double[][] latencyFitData = new double[LATENCY_DATA.length][2];
		int index = 0;
		long n = 0;
		for (String condition : conditions) {
			n = (long) Math.max(n, DataCollection.get().getGroupAccuracy(condition).getN());
			accuracyFitData[index][0] = DataCollection.get().getGroupAccuracy(condition).getMean();
			accuracyFitData[index][1] = ACCURACY_DATA[index];
			
			latencyFitData[index][0] = DataCollection.get().getGroupLatency(condition).getMean();
			latencyFitData[index][1] = LATENCY_DATA[index];
			
			index++;
		}

		/**
		 * clear the data for the next slice
		 */
		DataCollection.get().clear();

		/**
		 * calculate the various numbers
		 */
		FitStatistics fitStatistics = new FitStatistics(accuracyFitData);

		double r2 = fitStatistics.getRSquared();
		double rmse = fitStatistics.getRMSE();

		sliceAnalysis.addFitStatistics("Accuracy", rmse, r2, n, r2 > 0.9);
		
		fitStatistics = new FitStatistics(latencyFitData);

		r2 = fitStatistics.getRSquared();
		rmse = fitStatistics.getRMSE();

		sliceAnalysis.addFitStatistics("Latency", rmse, r2, n, r2 > 0.9);

		return null;
	}

	
	/**
	 * save an analysis file local to the slice analysis
	 * @param sliceAnalysis
	 */
	private void saveGroupNumbers(ISliceAnalysis sliceAnalysis) {

		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "accuracy.txt")));
			Collection<String> conditions = DataCollection.get().getConditions();
			conditions.forEach(c -> {
				analysisRoot.print(c + ".Mean\t" + c + ".Stdev\t");
			});
			analysisRoot.println();
			conditions.forEach(c -> {
				analysisRoot.format("%.2f\t%.2f\t", DataCollection.get().getGroupAccuracy(c).getMean(),
						DataCollection.get().getGroupAccuracy(c).getStandardDeviation());
			});
			analysisRoot.println();
			analysisRoot.close();

			sliceAnalysis.addDetail("Group Accuracy", "accuracy.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "latency.txt")));
			Collection<String> conditions = DataCollection.get().getConditions();
			conditions.forEach(c -> {
				analysisRoot.print(c + ".Mean\t" + c + ".Stdev\t");
			});
			analysisRoot.println();
			conditions.forEach(c -> {
				analysisRoot.format("%.2f\t%.2f\t", DataCollection.get().getGroupLatency(c).getMean(),
						DataCollection.get().getGroupLatency(c).getStandardDeviation());
			});
			analysisRoot.println();
			analysisRoot.close();

			sliceAnalysis.addDetail("Group Latency", "latency.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
