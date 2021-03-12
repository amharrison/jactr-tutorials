package org.jactr.tutorial.unit5.twentyone.data;

import java.awt.Color;
import java.io.File;
import java.util.Collections;

import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.jactr.tutorial.unit5.twentyone.jactr.PerCycleProcessor;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;

public class Analyzer implements ISliceAnalyzer {

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		/*
		 * let's look at the delta between the first and final bins
		 */
		double[] data = DataCollection.get().getBins();
		double[] stdev = DataCollection.get().getStdErrs();

		Chart chart = generateChart("Win/Loss Ratio", data, stdev);
		saveChart(sliceAnalysis, "Accuracy", "accuracy", chart);

		DataCollection.get().clear(); // and reset for the next slice

		double delta = data[data.length - 1] - data[0];

		sliceAnalysis.addFitStatistics("Delta", Collections.singletonMap("delta", "" + delta), delta > 0);
		sliceAnalysis.addFitStatistics("FinalScore", Collections.singletonMap("final", "" + data[data.length-1]),
				data[data.length-1] > 40);
		return null;
	}

	private Chart generateChart(String title, double[] data, double[] stdev) {

		CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title(title).xAxisTitle("Hands")
				.yAxisTitle("Win/Loss").build();
		chart.getStyler().setYAxisMax(1.0);
		chart.getStyler().setYAxisMin(0.0);
		chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
		
		double[] xdata = new double[PerCycleProcessor.TOTAL_BINS];
		for (int i = 0; i < xdata.length; i++)
			xdata[i] = i;

		CategorySeries series = chart.addSeries("Model", xdata, data, stdev);
		series.setFillColor(Color.BLACK);

		return chart;
	}

	private void saveChart(ISliceAnalysis sliceAnalysis, String label, String fileName, Chart chart) {
		File fp = new File(new File(sliceAnalysis.getWorkingDirectory()), fileName);
		try {
			BitmapEncoder.saveBitmap(chart, fp.getAbsolutePath(), BitmapFormat.PNG);
			sliceAnalysis.addImage(label, fileName + ".png");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
