package org.jactr.examples.custom;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;

import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.slot.ISlot;

/**
 * Simple bare-bones action that just depends on having one slot: output, which it pipes to
 * stdout. This is an example of an action that executes immediately. That is, it executes in
 * the same time frame as the production firing, which is different than when the production
 * finishes firing (+50ms). <br/>
 * Once an instantiation has been fully instantiated, the actions for it are created by
 * calling the {@link #bind(Map)} method of the action contained by the template production. If for
 * any reason the action cannot be instantiated (all variables will have been resolved), it can throw
 * a {@link CannotInstantiateException}. <br/>
 * When the winning instantiation is selected, the {@link #fire(IInstantiation, double)} method is called. If
 * the action takes additional theoretic time to complete, it can return that value which will increment the
 * production firing time duration. <br/> 
 * 
 * @author harrison
 *
 */
public class OutputSumAction extends DefaultSlotAction
{

  public OutputSumAction()
  {
  }

  /**
   * calls the {@link DefaultSlotAction} full constructor which will perform the variable resolution
   * @param variableBindings
   * @param slots
   * @throws CannotInstantiateException
   */
  private OutputSumAction(VariableBindings variableBindings,
      Collection<? extends ISlot> slots) throws CannotInstantiateException
  {
    super(variableBindings, slots);
  }

  /**
   * just check to see if the output slot exists.
   */
  public IAction bind(VariableBindings variableBindings)
  throws CannotInstantiateException
  {
    ISlot slot = getSlot("output");
    if(slot==null)
      throw new CannotInstantiateException("slot output must be defined");
    
    /*
     * this constructor will resolve the variables in the slots based on
     * the variable bindings
     */
    return new OutputSumAction(variableBindings, getSlots());
  }
  
  /**
   * merely print the value of the output slot.
   * @see IAction#fire(IInstantiation, double);
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    Object value = getSlot("output").getValue();
    
    System.out.println(String.format("Output value %s @ %.2f", value, firingTime));
    
    return 0;
  }


}
