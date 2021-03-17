package org.jactr.tutorial.unit6.probability.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.jactr.tools.experiment.trial.ICompoundTrial;
import org.w3c.dom.Element;

public class ConditionAssignmentHandler implements INodeHandler<IAction> {

	@Override
	public String getTagName() {
		return "assign";
	}

	@Override
	public IAction process(Element element, IExperiment experiment) {
		final int repetitions = Integer.parseInt(element.getAttribute("repetitions"));
		final double trueRate = Double.parseDouble(element.getAttribute("rate"));
		final String block = element.getAttribute("block");
		
		return new IAction() {

			@Override
			public void fire(IVariableContext context) {
				ICompoundTrial current = (ICompoundTrial) experiment.getTrial();
				for(int i=0;i<repetitions;i++)
					current.add(new DisplayTrial(experiment, block, trueRate));
			}
		};
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
