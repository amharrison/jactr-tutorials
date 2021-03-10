package org.jactr.tutorial.unit5.siegler.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.w3c.dom.Element;

public class ConditionAssignmentHandler implements INodeHandler<IAction> {

	@Override
	public String getTagName() {
		return "assign";
	}

	@Override
	public IAction process(Element element, IExperiment experiment) {
		return new ConditionAssignment(experiment, Integer.parseInt(element.getAttribute("repetitions")));
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
