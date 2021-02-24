package org.jactr.tutorial.unit4.paired.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;

public class DisplayHandler implements INodeHandler<ITrial> {

	@Override
	public String getTagName() {
		return "display";
	}

	@Override
	public ITrial process(Element element, IExperiment experiment) {

		int trial=Integer.parseInt(element.getAttribute("trial"));
		String[] pair = element.getAttribute("pair").split(",");
		int number = Integer.parseInt(pair[1].trim());
		return new DisplayTrial(element.getAttribute("id"), trial, pair[0], number, experiment);
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
