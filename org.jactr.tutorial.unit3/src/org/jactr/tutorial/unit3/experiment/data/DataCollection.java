package org.jactr.tutorial.unit3.experiment.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class DataCollection {

	static public DataCollection get() {
		return _instance;
	}

	static private DataCollection _instance = new DataCollection();
	
	private Map<String,SummaryStatistics> _scoreStatistics = new TreeMap<>();
	private Map<String,SummaryStatistics> _groupScoreStatistics = new TreeMap<>();
    private SummaryStatistics EMPTY = new SummaryStatistics();
	private DataCollection() {

	}
	
	public void logScore(String condition, int score)
	{
		_scoreStatistics.computeIfAbsent(condition, (key)->{
			return new SummaryStatistics();
		}).addValue(score);
	}
	
	public Collection<String> getConditions(){
		return new ArrayList<>(_groupScoreStatistics.keySet());
	}
	
	public SummaryStatistics getGroupScoreStatistics(String condition) {
		return _groupScoreStatistics.getOrDefault(condition, EMPTY);
	}
	
	public boolean hasSubject() {
		return _scoreStatistics.size()>0;
	}
	/**
	 * if something goes wrong, wipe the subject's data
	 */
	public void wipeSubject() {
		_scoreStatistics.clear();
	}
	
	/**
	 * migrate data from individual to group summaries
	 * prepare for the next subject
	 */
	public void subjectCompleted() {
		
		_scoreStatistics.entrySet().stream().forEach((entry)->{
			_groupScoreStatistics.computeIfAbsent(entry.getKey(), (key)->{
				return new SummaryStatistics();
			}).addValue(entry.getValue().getMean());
		});
		_scoreStatistics.clear();
	}
	
	public void clear()
	{
		_scoreStatistics.clear();
		_groupScoreStatistics.clear();
	}
}
