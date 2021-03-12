package org.jactr.tutorial.unit5.twentyone.jactr;

import java.util.Collection;

import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.slot.ISlot;

public class TwentyOneResponse extends DefaultSlotAction {

	public TwentyOneResponse() {

	}

	public TwentyOneResponse(VariableBindings variableBindings, Collection<? extends ISlot> slots)
			throws CannotInstantiateException {
		super(variableBindings, slots);
		checkForRequiredSlots("response");
	}

	@Override
	public IAction bind(VariableBindings variableBindings) throws CannotInstantiateException {

		return new TwentyOneResponse(variableBindings, getSlots());
	}

	@Override
	public double fire(IInstantiation instantiation, double firingTime) {

		SimpleTwentyOneExtension ext = (SimpleTwentyOneExtension) instantiation.getModel()
				.getExtension(SimpleTwentyOneExtension.class);
		ext.setResponse(getSlot("response").getValue().toString());

		return 0;
	}

}
