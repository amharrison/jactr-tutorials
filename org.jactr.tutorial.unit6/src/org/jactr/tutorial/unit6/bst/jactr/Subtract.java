package org.jactr.tutorial.unit6.bst.jactr;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.AbstractSlotCondition;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

public class Subtract extends AbstractSlotCondition {

	public Subtract() {
	}

	public Subtract(VariableBindings bindings, Collection<? extends ISlot> slots) {
		super();
		setRequest(new SlotBasedRequest());
		slots.forEach(this::addSlot);
	}
	
	

	@Override
	public int bind(IModel model, VariableBindings bindings, boolean iterativeCall) throws CannotMatchException {
		int unresolved = 0;

	    try
	    {
	      unresolved = getRequest().bind(model, bindings, iterativeCall);
	    }
	    catch (CannotMatchException cme)
	    {
	      cme.getMismatch().setCondition(this);
	      throw cme;
	    }
	    
	    if(unresolved==0)
	    {
	    	if(!hasSlots("arg1","arg2"))
	    		throw new CannotMatchException("Must define both arg1 and arg2");
	    	
	    	//actually apply the calculation
	    	double arg1 = ((Number)getSlot("arg1").getValue()).doubleValue();
	    	double arg2 = ((Number)getSlot("arg2").getValue()).doubleValue();
	    	double delta = arg1 - arg2;
	    	
	    	bindings.bind("=result", delta);
	    }
	    
	    return unresolved;
	}
	
	private boolean hasSlots(String...slots)
	{
		Set<String> set = new TreeSet<>();
		for(String slot : slots) 
			set.add(slot);
		
		for(ISlot slot : getSlots()) {
			if(set.contains(slot.getName()))
				set.remove(slot.getName());
		}
		
		return set.size()==0;
	}

	private ISlot getSlot(String string) {
		for(IConditionalSlot cSlot : getRequest().getConditionalSlots())
			if(cSlot.getName().equals(string)) return cSlot;
		return null;
	}

	@Override
	public ICondition clone(IModel model, VariableBindings bindings) throws CannotMatchException {
		
		return new Subtract(bindings, getSlots());
	}

}
