package org.jactr.tutorial.unit3.subitize.handler;

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
		return new DisplayTrial(element.getAttribute("id"), experiment, Integer.parseInt(element.getAttribute("targets")));
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
