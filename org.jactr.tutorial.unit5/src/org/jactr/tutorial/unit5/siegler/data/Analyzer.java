package org.jactr.tutorial.unit5.siegler.data;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.BubbleSeries;
import org.knowm.xchart.internal.chartpart.Chart;

/**
 * statistical analysis code for Unit 4 paired associate.
 * 
 * @author harrison
 *
 */
public class Analyzer implements ISliceAnalyzer {

	/**
	 * reference data
	 */
	static private Map<String, Double> ACCURACY_DATA = new TreeMap<>();
	static {
		// {t/f:person:location}
		ACCURACY_DATA.put("1+1:0", 0.0);
		ACCURACY_DATA.put("1+1:1", 0.05);
		ACCURACY_DATA.put("1+1:2", 0.86);
		ACCURACY_DATA.put("1+1:3", 0.0);
		ACCURACY_DATA.put("1+1:4", 0.02);
		ACCURACY_DATA.put("1+1:5", 0.0);
		ACCURACY_DATA.put("1+1:6", 0.02);
		ACCURACY_DATA.put("1+1:7", 0.0);
		ACCURACY_DATA.put("1+1:8", 0.0);
		ACCURACY_DATA.put("1+1:9", 0.06);

		ACCURACY_DATA.put("1+2:0", 0.0);
		ACCURACY_DATA.put("1+2:1", 0.04);
		ACCURACY_DATA.put("1+2:2", 0.07);
		ACCURACY_DATA.put("1+2:3", 0.75);
		ACCURACY_DATA.put("1+2:4", 0.04);
		ACCURACY_DATA.put("1+2:5", 0.0);
		ACCURACY_DATA.put("1+2:6", 0.02);
		ACCURACY_DATA.put("1+2:7", 0.0);
		ACCURACY_DATA.put("1+2:8", 0.0);
		ACCURACY_DATA.put("1+2:9", 0.09);

		ACCURACY_DATA.put("1+3:0", 0.0);
		ACCURACY_DATA.put("1+3:1", 0.02);
		ACCURACY_DATA.put("1+3:2", 0.0);
		ACCURACY_DATA.put("1+3:3", 0.1);
		ACCURACY_DATA.put("1+3:4", 0.75);
		ACCURACY_DATA.put("1+3:5", 0.05);
		ACCURACY_DATA.put("1+3:6", 0.01);
		ACCURACY_DATA.put("1+3:7", 0.03);
		ACCURACY_DATA.put("1+3:8", 0.0);
		ACCURACY_DATA.put("1+3:9", 0.06);

		ACCURACY_DATA.put("2+2:0", 0.02);
		ACCURACY_DATA.put("2+2:1", 0.0);
		ACCURACY_DATA.put("2+2:2", 0.04);
		ACCURACY_DATA.put("2+2:3", 0.05);
		ACCURACY_DATA.put("2+2:4", 0.80);
		ACCURACY_DATA.put("2+2:5", 0.04);
		ACCURACY_DATA.put("2+2:6", 0.0);
		ACCURACY_DATA.put("2+2:7", 0.05);
		ACCURACY_DATA.put("2+2:8", 0.0);
		ACCURACY_DATA.put("2+2:9", 0.0);

		ACCURACY_DATA.put("2+3:0", 0.0);
		ACCURACY_DATA.put("2+3:1", 0.0);
		ACCURACY_DATA.put("2+3:2", 0.07);
		ACCURACY_DATA.put("2+3:3", 0.09);
		ACCURACY_DATA.put("2+3:4", 0.25);
		ACCURACY_DATA.put("2+3:5", 0.45);
		ACCURACY_DATA.put("2+3:6", 0.08);
		ACCURACY_DATA.put("2+3:7", 0.01);
		ACCURACY_DATA.put("2+3:8", 0.01);
		ACCURACY_DATA.put("2+3:9", 0.06);

		ACCURACY_DATA.put("3+3:0", 0.04);
		ACCURACY_DATA.put("3+3:1", 0.0);
		ACCURACY_DATA.put("3+3:2", 0.0);
		ACCURACY_DATA.put("3+3:3", 0.05);
		ACCURACY_DATA.put("3+3:4", 0.21);
		ACCURACY_DATA.put("3+3:5", 0.09);
		ACCURACY_DATA.put("3+3:6", 0.48);
		ACCURACY_DATA.put("3+3:7", 0.0);
		ACCURACY_DATA.put("3+3:8", 0.02);
		ACCURACY_DATA.put("3+3:9", 0.11);
	}

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		// make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Set<String> conditions = DataCollection.get().getConditions();

		/**
		 * generate a simple summary report file for this analysis
		 */
		saveGroupNumbers(sliceAnalysis);

		/**
		 * repackage the data for fits
		 */
		Map<String, Double> modelData = new TreeMap<>();
		long n = 0;
		for (String condition : conditions) {
			SummaryStatistics[] stats = DataCollection.get().getGroupAccuracy(condition);
			n = (long) Math.max(n, stats[0].getN());

			for (int i = 0; i < stats.length; i++)
				modelData.put(condition + ":" + i, stats[i].getMean());
		}
		
		/**
		 * clear the data for the next slice
		 */
		DataCollection.get().clear();

		/**
		 * calculate the various numbers
		 */
		FitStatistics fitStatistics = new FitStatistics(modelData.keySet(), modelData, ACCURACY_DATA, false);

		double r2 = fitStatistics.getRSquared();
		double rmse = fitStatistics.getRMSE();

		sliceAnalysis.addFitStatistics("Accuracy", rmse, r2, n, r2 > 0.9);

		return null;
	}

	/**
	 * save an analysis file local to the slice analysis
	 * 
	 * @param sliceAnalysis
	 */
	private void saveGroupNumbers(ISliceAnalysis sliceAnalysis) {

		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "accuracy.txt")));

			for(int i=0;i<10;i++)
				analysisRoot.print("\t\t\t"+i);
			analysisRoot.println();
			
			Set<String> conditions = DataCollection.get().getConditions();
			for(String condition : conditions)
			{
				analysisRoot.print(condition+"\t");
				SummaryStatistics[] stats = DataCollection.get().getGroupAccuracy(condition);
				for(int i=0;i<stats.length;i++)
					analysisRoot.format("%.2f\t", stats[i].getMean());
				analysisRoot.println();
			}
			
			analysisRoot.close();

			sliceAnalysis.addDetail("Group Accuracy", "accuracy.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
