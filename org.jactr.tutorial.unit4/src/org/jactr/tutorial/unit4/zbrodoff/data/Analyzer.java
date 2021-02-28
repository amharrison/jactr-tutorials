package org.jactr.tutorial.unit4.zbrodoff.data;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.jactr.tutorial.unit4.paired.data.DataCollection;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
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

	
	static private final Map<String, Double> LATENCY_DATA = new TreeMap<>();
	static private final Map<String, Double> TUTORIAL_DATA = new TreeMap<>();
	static {
		LATENCY_DATA.put("1:2", 1.84);
		LATENCY_DATA.put("1:3", 2.46);
		LATENCY_DATA.put("1:4", 2.82);
		LATENCY_DATA.put("2:2", 1.21);
		LATENCY_DATA.put("2:3", 1.45);
		LATENCY_DATA.put("2:4", 1.42);
		LATENCY_DATA.put("3:2", 1.14);
		LATENCY_DATA.put("3:3", 1.21);
		LATENCY_DATA.put("3:4", 1.17);
		
		TUTORIAL_DATA.put("1:2",1.87);
		TUTORIAL_DATA.put("1:3",2.179);
		TUTORIAL_DATA.put("1:4",2.558);
		TUTORIAL_DATA.put("2:2",1.376);
		TUTORIAL_DATA.put("2:3",1.52);
		TUTORIAL_DATA.put("2:4",1.632);
		TUTORIAL_DATA.put("3:2",1.215);
		TUTORIAL_DATA.put("3:3",1.282);
		TUTORIAL_DATA.put("3:4", 1.359);
	}

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		// make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Collection<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != LATENCY_DATA.size())
			throw new RuntimeException("Unexpected number of conditions " + conditions.size());

		/**
		 * generate a simple summary report file for this analysis
		 */
		saveGroupNumbers(sliceAnalysis);
		saveImages(sliceAnalysis);

		/**
		 * repackage the data for fits
		 */
		Map<String, Double> model = new TreeMap<>();
		conditions.forEach(c -> {
			model.put(c, DataCollection.get().getGroupLatency(c).getMean());
		});
		long n = DataCollection.get().getGroupLatency(conditions.iterator().next()).getN();

		/**
		 * clear the data for the next slice
		 */
		DataCollection.get().clear();

		/**
		 * calculate the various numbers
		 */
		FitStatistics fitStatistics = new FitStatistics(LATENCY_DATA.keySet(), model, LATENCY_DATA, false);

		double r2 = fitStatistics.getRSquared();
		double rmse = fitStatistics.getRMSE();

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
		// accuracy first
		double[] xData = new double[] { 2,3,4};
		double[][] model = packageDataForGraphs(false);
		double[][] data = packageDataForGraphs(LATENCY_DATA);
		double[][]tutorial = packageDataForGraphs(TUTORIAL_DATA);
		XYChart chart = generateChart("Latency", xData, data, model, tutorial);
		saveChart(sliceAnalysis, "Latency", "latency", chart);
	}

	/**
	 * package the empirical data for graphing
	 * 
	 * @return
	 */
	private double[][] packageDataForGraphs(Map<String, Double> data) {
		Collection<String> conditions = DataCollection.get().getConditions();
		double[][] rtn = new double[3][3];
		
		for (String cond : conditions) {
			int block = 1;
			int addend = 2;
			
			String[] components = cond.split(":");
			block = Integer.parseInt(components[0]);
			addend = Integer.parseInt(components[1]);
			
			double value = data.get(cond);
			rtn[block-1][addend-2] = value;
		}
		
		return rtn;
	}

	private double[][] packageDataForGraphs(boolean collectAccuracy) {
		Collection<String> conditions = DataCollection.get().getConditions();
		double[][] rtn = new double[3][3];
		
		for (String cond : conditions) {
			int block = 1;
			int addend = 2;
			
			String[] components = cond.split(":");
			block = Integer.parseInt(components[0]);
			addend = Integer.parseInt(components[1]);
			
			double value = DataCollection.get().getGroupLatency(cond).getMean();
			if (Double.isNaN(value))
				value = 0;
			rtn[block-1][addend-2] = value;
		}
		return rtn;
	}

	private XYChart generateChart(String label, double[] xData, double[][] empirical, double[][] model, double[][] tutorial) {
		XYChart chart = QuickChart.getChart(label, "Addends", label, "Data.Block.1", xData, empirical[0]);
		chart.getSeriesMap().entrySet().iterator().next().getValue().setLineColor(Color.BLACK);

		Marker[] markers = new Marker[] {SeriesMarkers.NONE, SeriesMarkers.CIRCLE, SeriesMarkers.DIAMOND};
		for (int i = 1; i < empirical.length; i++) {
			XYSeries series = chart.addSeries(String.format("Data.Block.%d", i+1), xData, empirical[i]);
			series.setMarker(markers[i]);
			series.setLineStyle(SeriesLines.SOLID);
			series.setLineColor(Color.BLACK);
			series.setFillColor(Color.BLACK);
			series.setMarkerColor(Color.BLACK);
			
		}

		for (int i = 0; i < model.length; i++) {
			XYSeries series = chart.addSeries(String.format("Model.Block.%d", i+1), xData, model[i]);
			series.setMarker(markers[i]);
			series.setLineStyle(SeriesLines.DASH_DASH);
			series.setLineColor(Color.GRAY);
			series.setFillColor(Color.GRAY);
			series.setMarkerColor(Color.GRAY);
		}
		
		for (int i = 0; i < tutorial.length; i++) {
			XYSeries series = chart.addSeries(String.format("Tutorial.Block.%d", i+1), xData, tutorial[i]);
			series.setMarker(markers[i]);
			series.setLineStyle(SeriesLines.DOT_DOT);
			series.setLineColor(Color.RED);
			series.setFillColor(Color.RED);
			series.setMarkerColor(Color.RED);
		}
		
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
