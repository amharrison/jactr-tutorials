package org.jactr.tutorial.unit4.zbrodoff.handler;

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
		return new ConditionAssignment(experiment, Integer.parseInt(element.getAttribute("repetitions")),
				(char) Integer.parseInt(element.getAttribute("addends")),
				Integer.parseInt(element.getAttribute("letter-groups")), 
				Integer.parseInt(element.getAttribute("block")));
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
