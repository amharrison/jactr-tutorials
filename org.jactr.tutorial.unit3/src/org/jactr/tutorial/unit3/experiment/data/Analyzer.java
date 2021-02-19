package org.jactr.tutorial.unit3.experiment.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.jactr.tools.itr.fit.FitStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;

public class Analyzer implements ISliceAnalyzer {

	static private final double[] SPERLING_DATA = { 3.03, 2.40, 2.0, 1.50 };

	@Override
	public Object analyze(ISliceAnalysis sliceAnalysis) {

		if (DataCollection.get().hasSubject())
			DataCollection.get().subjectCompleted();

		Collection<String> conditions = DataCollection.get().getConditions();
		if (conditions.size() != SPERLING_DATA.length)
			throw new RuntimeException("Unexpected number of conditions " + conditions.size());

		double[][] fitData = new double[SPERLING_DATA.length][2];

		int index = 0;
		long n = 0;
		for (String condition : conditions) {
			n = (long) Math.max(n, DataCollection.get().getGroupScoreStatistics(condition).getN());
			fitData[index][0] = DataCollection.get().getGroupScoreStatistics(condition).getMean();
			fitData[index][1] = SPERLING_DATA[index];
			index++;
		}

		saveGroupNumbers(sliceAnalysis);

		DataCollection.get().clear();

		FitStatistics fitStatistics = new FitStatistics(fitData);

		double r2 = fitStatistics.getRSquared();
		double rmse = fitStatistics.getRMSE();

		sliceAnalysis.addFitStatistics("Recalled", rmse, r2, n, r2 > 0.9);

		return null;
	}

	private void saveGroupNumbers(ISliceAnalysis sliceAnalysis) {

		try {
			PrintWriter analysisRoot = new PrintWriter(
					new FileWriter(new File(new File(sliceAnalysis.getWorkingDirectory()), "recalled.txt")));
			Collection<String> conditions = DataCollection.get().getConditions();
			conditions.forEach(c -> {
				analysisRoot.print(c + ".Mean\t" + c + ".Stdev\t");
			});
			analysisRoot.println();
			conditions.forEach(c -> {
				analysisRoot.format("%.2f\t%.2f\t", DataCollection.get().getGroupScoreStatistics(c).getMean(),
						DataCollection.get().getGroupScoreStatistics(c).getStandardDeviation());
			});
			analysisRoot.println();
			analysisRoot.close();

			sliceAnalysis.addDetail("Group Numbers", "recalled.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
