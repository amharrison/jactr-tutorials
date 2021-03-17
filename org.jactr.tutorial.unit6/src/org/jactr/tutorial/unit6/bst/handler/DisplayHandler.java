package org.jactr.tutorial.unit6.bst.handler;

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
		return new DisplayTrial(element.getAttribute("id"), Integer.parseInt(element.getAttribute("a")),
				Integer.parseInt(element.getAttribute("b")), Integer.parseInt(element.getAttribute("c")),
				Integer.parseInt(element.getAttribute("goal")), experiment);

	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
