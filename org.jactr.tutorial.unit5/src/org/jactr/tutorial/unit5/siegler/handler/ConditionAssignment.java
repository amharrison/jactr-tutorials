package org.jactr.tutorial.unit5.siegler.handler;

import java.util.ArrayList;
import java.util.List;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.ICompoundTrial;
import org.jactr.tools.experiment.trial.ITrial;

public class ConditionAssignment implements IAction {

	private final int _startAddend = 1;
	private final int _maxAddend = 4;
	private final int _maxValue = 9;
	private final int _repetitions;
	private final IExperiment _experiment;

	public ConditionAssignment(IExperiment experiment, int reps) {
		_repetitions = reps;
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
		ICompoundTrial current = (ICompoundTrial) _experiment.getTrial();
		trials.forEach(current::add);
	}

	private List<ITrial> createTrials() {
		List<ITrial> rtn = new ArrayList<>();
		for (int i = 0; i < _repetitions; i++) {
			for (int x = _startAddend; x < _maxAddend; x++)
				for (int y = _startAddend; y < _maxAddend; y++) {
					if(x+y > _maxValue) continue;
					rtn.add(new AuralTrial(_experiment, x,y));
				}
		}
		return rtn;
	}
}
