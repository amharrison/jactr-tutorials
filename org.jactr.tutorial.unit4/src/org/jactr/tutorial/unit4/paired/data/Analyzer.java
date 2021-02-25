package org.jactr.tutorial.unit4.paired.data;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
	static private final double[] ACCURACY_DATA = { 0, 0.526, 0.667, 0.798, 0.887, 0.924, 0.958, 0.954 };
	static private final double[] LATENCY_DATA = { 0, 2.156, 1.967, 1.762, 1.680, 1.552, 1.467, 1.402 };

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		// make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Collection<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != ACCURACY_DATA.length)
			throw new RuntimeException("Unexpected number of conditions " + conditions.size());

		/**
		 * generate a simple summary report file for this analysis
		 */
		saveGroupNumbers(sliceAnalysis);
		saveImages(sliceAnalysis);

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
	 * 
	 * @param sliceAnalysis
	 */
	private void saveGroupNumbers(ISliceAnalysis sliceAnalysis) {

		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "accuracy.txt")));
			Collection<String> conditions = DataCollection.get().getConditions();
			conditions.forEach(c -> {
				analysisRoot.print(c + ".Mean\t");
			});
			analysisRoot.println();
			conditions.forEach(c -> {
				analysisRoot.format("%.2f\t", DataCollection.get().getGroupAccuracy(c).getMean());
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
				analysisRoot.print(c + ".Mean\t");
			});
			analysisRoot.println();
			conditions.forEach(c -> {
				analysisRoot.format("%.2f\t", DataCollection.get().getGroupLatency(c).getMean());
			});
			analysisRoot.println();
			analysisRoot.close();

			sliceAnalysis.addDetail("Group Latency", "latency.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveImages(ISliceAnalysis sliceAnalysis) {
		//accuracy first
		double[] xData = new double[] {1,2,3,4,5,6,7,8};
		double[] model = packageDataForGraphs(true);
		XYChart chart = generateChart("Accuracy", xData, ACCURACY_DATA, model);
		saveChart(sliceAnalysis, "Accuracy", "accuracy", chart);
		
		model = packageDataForGraphs(false);
		chart = generateChart("Latency", xData, LATENCY_DATA, model);
		saveChart(sliceAnalysis, "Latency", "latency", chart);

	}

	private double[] packageDataForGraphs(boolean collectAccuracy) {
		Collection<String> conditions = DataCollection.get().getConditions();
		double[] rtn = new double[conditions.size()];
		int index = 0;
		for (String cond : conditions) {
			double value = DataCollection.get().getGroupAccuracy(cond).getMean();
			if (!collectAccuracy)
				value = DataCollection.get().getGroupLatency(cond).getMean();
			rtn[index++] = value;
		}
		return rtn;
	}

	private XYChart generateChart(String label, double[] xData, double[] empirical, double[] model) {
		XYChart chart = QuickChart.getChart(label, "Trial", label, "Data", xData, empirical);
		chart.getSeriesMap().entrySet().iterator().next().getValue().setLineColor(Color.BLACK);
		
		XYSeries series = chart.addSeries("Model", model);
		series.setMarker(SeriesMarkers.CIRCLE);
		series.setLineStyle(SeriesLines.DASH_DASH);
		series.setLineColor(Color.GRAY);
		series.setMarkerColor(Color.GRAY);
		return chart;
	}

	private void saveChart(ISliceAnalysis sliceAnalysis, String label, String fileName, XYChart chart) {
		File fp = new File(new File(sliceAnalysis.getWorkingDirectory()), fileName);
		try {
			BitmapEncoder.saveBitmap(chart, fp.getAbsolutePath(), BitmapFormat.PNG);
			sliceAnalysis.addImage(label, fileName+".png");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
