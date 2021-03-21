package org.jactr.tutorial.unit2.experiment.handler;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.handlers.INodeHandler;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;


/**
 * This handler enables the experiment parser to deal with the <display.. />
 * tag. 
 * <code>
 * <trial-handler
		class="org.jactr.tutorial.unit2.experiment.handler.DisplayHandler" />
 * </code>
 * 
 * @author harrison
 *
 */
public class DisplayHandler implements INodeHandler<ITrial> {

	/**
	 * the tag name for us to use
	 */
	@Override
	public String getTagName() {
		return "display";
	}

	/**
	 * convert the display tag into a DisplayTrial which handles the logic of the experiment
	 */
	@Override
	public ITrial process(Element element, IExperiment experiment) {
		/**
		 * if we have an attribute "letters" we assume the second experiment. "letter" implies
		 * the first experiment. Both are handled by DisplayTrial
		 */
		if (element.hasAttribute("letters")) {
			String[] letters = element.getAttribute("letters").split(",");

			return new DisplayTrial(element.getAttribute("id"), experiment, letters[0].trim().charAt(0),
					letters[1].trim().charAt(0));
		} else {
			return new DisplayTrial(element.getAttribute("id"), experiment, element.getAttribute("letter").charAt(0));
		}
	}

	/**
	 * should we descend [sic]
	 */
	@Override
	public boolean shouldDecend() {
		return false;
	}

}
