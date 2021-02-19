package org.jactr.tutorial.unit3.experiment.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.jactr.tutorial.unit3.experiment.data.DataCollection;
import org.jactr.tutorial.unit3.experiment.sim.SimulatedKeyboard;
import org.w3c.dom.Element;

public class CleanupHandler implements INodeHandler<IAction> {

	@Override
	public String getTagName() {
		return "cleanup";
	}

	@Override
	public IAction process(Element element, IExperiment experiment) {
		return new IAction() {

			@Override
			public void fire(IVariableContext context) {
				DataCollection.get().subjectCompleted();
				
				if (DisplayTrial._interface != null) {
					DisplayTrial._interface.dispose();
					DisplayTrial._interface = null;
				}
				SimulatedKeyboard.consumers.clear();

			}

		};
	}

	@Override
	public boolean shouldDecend() {
		return false;
	}

}
