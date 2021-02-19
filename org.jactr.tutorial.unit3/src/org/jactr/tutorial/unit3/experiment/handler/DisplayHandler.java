package org.jactr.tutorial.unit3.experiment.handler;

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

		double delay = Double.parseDouble(experiment.getVariableResolver()
				.resolve(element.getAttribute("delay"), experiment.getVariableContext()).toString());

		return new DisplayTrial(element.getAttribute("id"), experiment, delay,
				Integer.parseInt(element.getAttribute("cue")), element.getAttribute("row1"),
				element.getAttribute("row2"), element.getAttribute("row3"));
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
