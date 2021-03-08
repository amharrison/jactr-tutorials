package org.jactr.tutorial.unit5.fan.handler;

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

		return new DisplayTrial(element.getAttribute("id"), element.getAttribute("person"), element.getAttribute("location"),
				Boolean.parseBoolean(element.getAttribute("target")), experiment);
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
