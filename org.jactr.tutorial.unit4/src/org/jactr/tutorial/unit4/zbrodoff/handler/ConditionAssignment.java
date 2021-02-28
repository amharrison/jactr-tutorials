package org.jactr.tutorial.unit4.zbrodoff.handler;

import java.util.ArrayList;
import java.util.List;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.trial.impl.CompoundTrial;

public class ConditionAssignment implements IAction {

	private final int _repetitions;
	private final char _addends;
	private final int _letterGroup;
	private final IExperiment _experiment;
	private int _block;

	public ConditionAssignment(IExperiment experiment, int reps, char addends, int letterGroup, int block) {
		_repetitions = reps;
		_addends = (char) Math.min(3, addends); //max from the study
		_letterGroup = (int) Math.min(2, letterGroup); //max from the study
		_block = block;
		_experiment = experiment;
	}

	/**
	 * this will be fired when the group starts. giving us a chance to inject the
	 * trials.
	 */
	@Override
	public void fire(IVariableContext context) {
		List<ITrial> trials = createTrials();
		// because this is a group parentage
		CompoundTrial current = (CompoundTrial) _experiment.getTrial();
		trials.forEach(current::add);
	}

	private List<ITrial> createTrials() {
		int conditionDuplication = 2; // each condition gets two
		char[] letterGroups = { 'a', 'd', 'g' };
		ArrayList<ITrial> rtn = new ArrayList<>(_repetitions * _addends);
		for (int rep = 0; rep < _repetitions; rep++)
			for (int l = 0; l < _letterGroup; l++) {
				char letter = letterGroups[l];
				for (char addend = 2; addend < 2 + _addends; addend++) {
					for (int i = 0; i < conditionDuplication; i++) {
						// always do both the true and false version
						rtn.add(new DisplayTrial(_experiment, _block, letter, addend, true));
						rtn.add(new DisplayTrial(_experiment, _block, letter, addend, false));
//						System.out.println(String.format("Created %c+%d", letter, (int)addend));
					}
					letter++;
				}
			}
		return rtn;
	}
}
