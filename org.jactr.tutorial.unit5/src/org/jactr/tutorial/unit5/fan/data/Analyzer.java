package org.jactr.tutorial.unit5.fan.data;

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
	static private Map<String, Double> LATENCY_DATA = new TreeMap<>();
	static {
		// {t/f:person:location}
		LATENCY_DATA.put("t:1:1", 1.111);
		LATENCY_DATA.put("t:2:1", 1.174);
		LATENCY_DATA.put("t:3:1", 1.222);
		LATENCY_DATA.put("t:1:2", 1.167);
		LATENCY_DATA.put("t:2:2", 1.198);
		LATENCY_DATA.put("t:3:2", 1.222);
		LATENCY_DATA.put("t:1:3", 1.153);
		LATENCY_DATA.put("t:2:3", 1.233);
		LATENCY_DATA.put("t:3:3", 1.357);

		LATENCY_DATA.put("f:1:1", 1.197);
		LATENCY_DATA.put("f:2:1", 1.221);
		LATENCY_DATA.put("f:3:1", 1.264);
		LATENCY_DATA.put("f:1:2", 1.250);
		LATENCY_DATA.put("f:2:2", 1.356);
		LATENCY_DATA.put("f:3:2", 1.291);
		LATENCY_DATA.put("f:1:3", 1.262);
		LATENCY_DATA.put("f:2:3", 1.471);
		LATENCY_DATA.put("f:3:3", 1.465);
	}

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		// make sure all the data is collected
		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Set<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != LATENCY_DATA.size())
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
			n = (long) Math.max(n, DataCollection.get().getGroupAccuracy(condition).getN());
			modelData.put(condition, DataCollection.get().getGroupLatency(condition).getMean());
		}
		
		saveImages(sliceAnalysis, modelData);

		/**
		 * clear the data for the next slice
		 */
		DataCollection.get().clear();

		/**
		 * calculate the various numbers
		 */
		FitStatistics fitStatistics = new FitStatistics(conditions, modelData, LATENCY_DATA, false);

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

			for (String tf : new String[] { "Target", "Foil" }) {
				analysisRoot.println(tf);
				analysisRoot.println("Location\t\tPerson Fan");
				analysisRoot.print("Fan\t\t");
				for (int person = 1; person <= 3; person++)
					analysisRoot.print(person + "\t\t\t");
				analysisRoot.println();

				char ctf = Character.toLowerCase(tf.charAt(0));
				for (int location = 1; location <= 3; location++) {
					analysisRoot.print(location + "\t\t\t");
					for (int person = 1; person <= 3; person++) {
						String condition = String.format("%c:%d:%d", ctf, person, location);
						double value = DataCollection.get().getGroupLatency(condition).getMean();
						analysisRoot.print(String.format("%.2f\t", value));
					}
					analysisRoot.println();
				}

				analysisRoot.println();
			}

			analysisRoot.close();

			sliceAnalysis.addDetail("Group Latency", "latency.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveImages(ISliceAnalysis sliceAnalysis, Map<String, Double> modelData) {
		double[] xData = new double[] {1,2,3};
		double[][] md = packageDataForGraphs(true,true,  modelData);
		double[][] ed = packageDataForGraphs(true,true, LATENCY_DATA);
		
		XYChart chart = generateChart("Target Latencies", "Latency", xData, ed, md);
		saveChart(sliceAnalysis, "Target Latency", "target", chart);

		md = packageDataForGraphs(true,false,  modelData);
		ed = packageDataForGraphs(true,false, LATENCY_DATA);
		chart = generateChart("Foil Latencies", "Latency", xData, ed, md);
		saveChart(sliceAnalysis, "Foil Latency", "foil", chart);
		
	}

	private double[][] packageDataForGraphs(boolean personMajor, boolean extractTargets, Map<String, Double> data) {
		Collection<String> conditions = DataCollection.get().getConditions();
		double[][] rtn = new double[3][3];

		for (String cond : conditions) {
			int person = 1;
			int location = 2;

			String[] components = cond.split(":");
			person = Integer.parseInt(components[1]);
			location = Integer.parseInt(components[2]);
			
			if(extractTargets && !components[0].equals("t")) continue;
			if(!extractTargets && !components[0].equals("f")) continue;

			double value = data.get(cond);
			if (personMajor)
				rtn[person - 1][location - 1] = value;
			else
				rtn[location - 1][person - 1] = value;
		}

		return rtn;
	}
	
	private XYChart generateChart(String title, String label, double[] xData, double[][] empirical, double[][] model) {
		XYChart chart = QuickChart.getChart(title, "Person Fan", label, "Data.LocationFan.1", xData, empirical[0]);
		chart.getSeriesMap().entrySet().iterator().next().getValue().setLineColor(Color.BLACK);

		Marker[] markers = new Marker[] {SeriesMarkers.NONE, SeriesMarkers.CIRCLE, SeriesMarkers.DIAMOND};
		for (int i = 1; i < empirical.length; i++) {
			XYSeries series = chart.addSeries(String.format("Data.LocationFan.%d", i+1), xData, empirical[i]);
			series.setMarker(markers[i]);
			series.setLineStyle(SeriesLines.SOLID);
			series.setLineColor(Color.BLACK);
			series.setFillColor(Color.BLACK);
			series.setMarkerColor(Color.BLACK);
			
		}

		for (int i = 0; i < model.length; i++) {
			XYSeries series = chart.addSeries(String.format("Model.LocationFan.%d", i+1), xData, model[i]);
			series.setMarker(markers[i]);
			series.setLineStyle(SeriesLines.DASH_DASH);
			series.setLineColor(Color.GRAY);
			series.setFillColor(Color.GRAY);
			series.setMarkerColor(Color.GRAY);
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
