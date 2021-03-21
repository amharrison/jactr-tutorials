package org.jactr.tutorial.unit2.experiment.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.w3c.dom.Element;

/**
 * This handler enables the experiment parser to understand the <cleanup/>
 * tag used in experiment.xml. 
 * It is contributed by <code>
 * <action-handler
		class="org.jactr.tutorial.unit2.experiment.handler.CleanupHandler" />
 * </code>
 * 
 * @author harrison
 *
 */
public class CleanupHandler implements INodeHandler<IAction> {

	/**
	 * the name of the tag to parse
	 */
	@Override
	public String getTagName() {
		return "cleanup";
	}

	/**
	 * found a <cleanup/> tag, transform it into an action. Here we just 
	 * create an annonymous inner class implementation of {@link IAction}
	 */
	@Override
	public IAction process(Element element, IExperiment experiment) {
		return new IAction() {

			/**
			 * cleanup any resources used by the IExperimentInterface implementation
			 */
			@Override
			public void fire(IVariableContext context) {
				if(DisplayTrial._interface!=null)
					DisplayTrial._interface.dispose();
				DisplayTrial._interface = null;   
			}
			
		};
	}

	/**
	 * should the parser recursively descend. [sic]
	 */
	@Override
	public boolean shouldDecend() {
		return false;
	}

}
