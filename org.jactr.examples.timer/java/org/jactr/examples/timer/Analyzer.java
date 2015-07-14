package org.jactr.examples.timer;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;
import org.jactr.tools.itr.ortho.ISliceIntegrator;

/**
 * Demo slice (orthogonal slice of the parameter space) analyzer. This is called
 * for each {@link ISliceAnalysis} which represents the collection of a block of
 * parameter values, all the directories for the runs ({date}/{time}/{run#}),
 * and the working directory into which all images, analyses and other outputs
 * should be written ({date}/{time}/report/{slice#}).<br/>
 * <br/>
 * Textual notes can also be set for the analysis, which is viewable & editable
 * in the analysis report.<br/>
 * <br/>
 * If secondary analyses are required after all the slices have been processed,
 * the analyzer can return an arbitrary object which is associated with the slice (say a map 
 * of values). An {@link ISliceIntegrator} can be provided to perform those analyses.
 * 
 * @author harrison
 */
public class Analyzer implements ISliceAnalyzer
{

  public Object analyze(ISliceAnalysis sliceAnalysis)
  {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    Collection<Number> durations = new ArrayList<Number>();

    /**
     * We snag all the data from the aggregator, then clear it for the next
     * block of runs.
     */

    for (Number duration : Aggregator.getDurations(durations))
      stats.addValue(duration.doubleValue());

    Aggregator.clear();

    /**
     * you can return any statistic you'd like, grouped by some variable. In
     * this case, we're returning the N, Mean, and Stdev for Duration. We could
     * return additional statistics for other variables if we'd collected them.
     */
    Map<String, String> summaries = new TreeMap<String, String>();
    summaries.put("n", "" + stats.getN());
    summaries.put("mean", "" + stats.getMean());
    summaries.put("stdv", "" + stats.getStandardDeviation());

    /**
     * here we associate the variable with the statistics. We can also flag
     * (boolean) the statistics, which will then be highlighted in the summary
     */
    sliceAnalysis.addFitStatistics("Duration", summaries, false);

    /**
     * we can also attach additional details to the analysis (i.e. summary or
     * analysis files) which are labeled with the working directory relative
     * path to the file. <br/>
     * sliceAnalysis.addDetail(label, workingRelativePath);
     */

    /**
     * similarly, we can attach images to the analysis <br/>
     * sliceAnalysis.addImage(label, workingRelativePath);
     */

    return null;
  }

}
