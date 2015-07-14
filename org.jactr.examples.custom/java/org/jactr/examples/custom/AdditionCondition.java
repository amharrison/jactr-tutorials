package org.jactr.examples.custom;

/*
 * default logging
 */
import java.util.Map;

import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.AbstractSlotCondition;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

/**
 * example addition condition that expects three slots: arg1, arg2, result. The
 * args can be numbers or variables (ultimately resolved to numbers), and result
 * should be a variable that will be resolved on binding. <br/>
 * <br/>
 * The initial instance of the condition is held by the actual production.
 * {@link #clone(IModel, Map)} is called during the production instantiation
 * process (before the instantiation is bound). The {@link #clone(IModel, Map)}
 * method can throw the {@link CannotMatchException} if it cannot possibly be
 * bound. The clone method should return a new instance of the condition if it
 * holds any member data. If the condition just contains logic, it may return
 * itself. <br/>
 * Once all of the conditions have been cloned for the instantiation, the actual
 * binding process begins. The {@link #bind(IModel, Map, boolean)} method is
 * called iteratively as the instantiation binding is resolved.<br/>
 * <br/>
 * In this example, the {@link #clone(IModel, Map)} method is a noop, simply
 * return a copy the original condition (necessary since the slots will change
 * during binding). {@link #bind(IModel, Map, boolean)} is delegated to the
 * {@link SlotBasedRequest} that is managed by the {@link AbstractSlotCondition}
 * . The {@link SlotBasedRequest} contains all the slots specified and also
 * includes methods to assist in the binding resolution process. Once the
 * bindings are complete, we compute the sum and set the variable and return.
 * 
 * @author harrison
 */
public class AdditionCondition extends AbstractSlotCondition
{

  /**
   * public zero arg constructor required for the production
   */
  public AdditionCondition()
  {
  }

  /**
   * check that the required slots are present and numbers/variables then copy
   * 
   * @see ICondition#clone(IModel, Map) for details
   */
  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    /*
     * let's make sure arg1, arg2, and sum exist and that they are variables or
     * numbers
     */
    for (String slotName : new String[] { "arg1", "arg2", "sum" })
    {
      ISlot slot = getSlot(slotName);
      if (slot == null)
        throw new CannotMatchException(String.format(
            "slot %s must be defined (number or variable)", slotName));

      if (!slot.isVariableValue() && !(slot.getValue() instanceof Number))
        throw new CannotMatchException(String.format(
            "slot %s must be a number or variable", slotName));
    }

    AdditionCondition clone = new AdditionCondition();
    /*
     * the slot based request manages all the slots for us, but we do need to
     * copy them.
     */
    clone.setRequest(new SlotBasedRequest(getSlots()));
    return clone;
  }

  /**
   * @see ICondition#bind(IModel, Map, boolean) for more details
   */
  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    //let the slot based request handle most of the binding
    int unresolved = getRequest().bind(model, variableBindings, isIterative);

    /*
     * there are three possible unresolved slots here: arg1, arg2, and sum. we
     * can test and calculate once unresolved is <=1 or isIterative is false, as
     * that means sum is unresolved (unresolved=1), needs to be compared in a
     * test (i.e. arg1 1 arg2 2 sum 3), or this is the last iteration
     */
    if (unresolved <= 1 || !isIterative)
    {
      ISlot arg1 = getSlot("arg1");
      ISlot arg2 = getSlot("arg2");

      /*
       * none of those can be null, otherwise we'd never have gotten through
       * clone, but they may have been variables that have been resolved to
       * something other than a number
       */
      double one = getValue(arg1);
      double two = getValue(arg2);

      double trueSum = one + two;

      /*
       * now one of two things could be true about the sum slot. It could still
       * be unresolved, in which case we resolve it ourselves and decrement
       * unresolved. Or we need to compare a resolved sum value to the true
       * value. To do this, we need to snag all the slots that are named sum
       */
      for (IConditionalSlot cSlot : getConditionalSlots())
        if (cSlot.getName().equals("sum"))
        {
          if (cSlot.isVariableValue())
          {
            // resolve
            if (cSlot.getCondition() == IConditionalSlot.EQUALS)
            {
              //add the binding
              variableBindings.bind((String) cSlot.getValue(), trueSum);
              //and resolve the value
              cSlot.setValue(trueSum);
              unresolved--;
            }
            else if (!isIterative)
              throw new CannotMatchException(String.format("%s is unresolved",
                  cSlot.getValue()));
          }
          else
          {
            /*
             * the slot already has a value, which means we need to make a
             * comparison against trueSum
             */
            if (!cSlot.matchesCondition(trueSum))
              throw new CannotMatchException(String.format(
                  "%s=%.2f does not match condition %s.", cSlot.getName(),
                  trueSum, cSlot));
          }
        }
    }

    return unresolved;
  }

  /**
   * return the named slot, iff it is an equality slot
   * 
   * @param slotName
   * @return
   */
  private ISlot getSlot(String slotName)
  {
    for (IConditionalSlot cSlot : getConditionalSlots())
      if (cSlot.getCondition() == IConditionalSlot.EQUALS)
        if (cSlot.getName().equals(slotName)) return cSlot;
    return null;
  }

  /**
   * return the numeric value of the slot or throw a cannot match if it is not a
   * number
   * 
   * @param slot
   * @return
   * @throws CannotMatchException
   */
  private double getValue(ISlot slot) throws CannotMatchException
  {
    if (!(slot.getValue() instanceof Number))
      throw new CannotMatchException(String.format(
          "slot %s must resolve to a number. %s is invalid.", slot.getName(),
          slot.getValue()));
    return ((Number) slot.getValue()).doubleValue();
  }

}
