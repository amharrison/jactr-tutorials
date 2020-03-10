package org.jactr.tutorial.unit2.experiment.handler;

import java.util.concurrent.Executors;

import org.commonreality.time.impl.RealtimeClock;
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

		if (experiment.getClock() == null) {
			experiment.setClock(new RealtimeClock(Executors.newSingleThreadScheduledExecutor()));
		}
		
		if (element.hasAttribute("letters")) {
			String[] letters = element.getAttribute("letters").split(",");

			return new DisplayTrial(element.getAttribute("id"), experiment, letters[0].trim().charAt(0),
					letters[1].trim().charAt(0));
		} else {
			return new DisplayTrial(element.getAttribute("id"), experiment, element.getAttribute("letter").charAt(0));
		}
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
