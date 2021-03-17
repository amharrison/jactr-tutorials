package org.jactr.tutorial.unit6.bst.data;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
		ACCURACY_DATA.put("1", 0.20);
		ACCURACY_DATA.put("2", 0.67);
		ACCURACY_DATA.put("3", 0.20);
		ACCURACY_DATA.put("4", 0.47);
		ACCURACY_DATA.put("5", 0.87);
		ACCURACY_DATA.put("6", 0.20);
		ACCURACY_DATA.put("7", 0.80);
		ACCURACY_DATA.put("8", 0.93);
		ACCURACY_DATA.put("9", 0.83);
		ACCURACY_DATA.put("10", 0.13);
		ACCURACY_DATA.put("11", 0.29);
		ACCURACY_DATA.put("12", 0.27);
		ACCURACY_DATA.put("13", 0.80);
		ACCURACY_DATA.put("14", 0.73);
		ACCURACY_DATA.put("15", 0.53);
	}

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		// make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Set<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != ACCURACY_DATA.size())
			throw new RuntimeException("Unexpected number of conditions " + conditions.size());

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
			n = (long) Math.max(n, DataCollection.get().getGroupOvershoot(condition).getN());
			modelData.put(condition, DataCollection.get().getGroupOvershoot(condition).getMean());
		}

		saveImages(sliceAnalysis, modelData);

		/**
		 * clear the data for the next slice
		 */
		DataCollection.get().clear();

		/**
		 * calculate the various numbers
		 */
		FitStatistics fitStatistics = new FitStatistics(conditions, modelData, ACCURACY_DATA, false);

		double r2 = fitStatistics.getRSquared();
		double rmse = fitStatistics.getRMSE();

		sliceAnalysis.addFitStatistics("%Overshoot", rmse, r2, n, r2 > 0.9);

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
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "overshoot.txt")));

			Collection<String> conditions = DataCollection.get().getConditions();
			for (String condition : conditions)
				analysisRoot.print(condition + "\t");
			analysisRoot.println();

			for (String condition : conditions)
				analysisRoot.format("%.2f\t", DataCollection.get().getGroupOvershoot(condition).getMean());
			analysisRoot.println();

			analysisRoot.close();

			sliceAnalysis.addDetail("% Overshoot", "overshoot.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * and the utilities
		 */
		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "utilities.txt")));

			String[] productions = { "decide-over", "decide-under", "force-over", "force-under" };
			for(String productionName : productions)
			{
				double value = DataCollection.get().getGroupExpectedUtility(productionName).getMean();
				analysisRoot.format("%s\t%.2f\n",productionName, value);
			}
			
			analysisRoot.close();

			sliceAnalysis.addDetail("Average Utilities", "utilities.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
	}

	private void saveImages(ISliceAnalysis sliceAnalysis, Map<String, Double> modelData) {
		double[] xData = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		double[] md = packageDataForGraphs(modelData);
		double[] ed = packageDataForGraphs(ACCURACY_DATA);

		XYChart chart = generateChart("% Overshoot", "Overshoot %", xData, ed, md);
		saveChart(sliceAnalysis, "% Overshoot", "overshoot", chart);
	}

	private double[] packageDataForGraphs(Map<String, Double> data) {
		Collection<String> conditions = data.keySet();
		double[] rtn = new double[conditions.size()];

		int index = 0;
		for (String cond : conditions) {
			rtn[index++] = data.get(cond);
		}

		return rtn;
	}

	private XYChart generateChart(String title, String label, double[] xData, double[] empirical, double[] model) {
		XYChart chart = QuickChart.getChart(title, "Problem", label, "Data", xData, empirical);
		chart.getSeriesMap().entrySet().iterator().next().getValue().setLineColor(Color.BLACK);

		XYSeries series = chart.addSeries("Model", xData, model);
		series.setMarker(SeriesMarkers.NONE);
		series.setLineStyle(SeriesLines.DASH_DASH);
		series.setLineColor(Color.GRAY);
		series.setFillColor(Color.GRAY);
		series.setMarkerColor(Color.GRAY);

		return chart;
	}

	private void saveChart(ISliceAnalysis sliceAnalysis, String label, String fileName, XYChart chart) {
		File fp = new File(new File(sliceAnalysis.getWorkingDirectory()), fileName);
		try {
			BitmapEncoder.saveBitmap(chart, fp.getAbsolutePath(), BitmapFormat.PNG);
			sliceAnalysis.addImage(label, fileName + ".png");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
