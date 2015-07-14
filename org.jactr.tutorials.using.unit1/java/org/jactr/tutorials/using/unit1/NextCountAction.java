package org.jactr.tutorials.using.unit1;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.ISlot;
import org.jactr.tools.misc.ChunkUtilities;

/**
 * this is a custom production fired action that will create a new count-order
 * chunk when none can be retrieved. This is an illustration of how you can 
 * extend and add conditions/actions with the best performance (as opposed to
 * using scripted code).
 * <br/>
 * A {@link IProduction} is a named collection of {@link ICondition}s and {@link IAction}s. 
 * During conflict resolution, relevant productions will attempt to be instantiated.
 * This is done by binding ({@link ICondition#bind(org.jactr.core.model.IModel, VariableBindings, boolean)})
 * the conditions. If successful, the actions are bound as well. The bound copies of the
 * conditions and actions make up the production's {@link IInstantiation}
 * 
 * @author harrison
 */
public class NextCountAction extends DefaultSlotAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NextCountAction.class);

  /**
   * Must always have an empty, default constructor for building from model
   * files
   */
  public NextCountAction()
  {
  }

  /**
   * It is common to provide this implementation for any custom bindings
   * 
   * @param variableBindings
   * @param slots
   * @throws CannotInstantiateException
   */
  public NextCountAction(VariableBindings variableBindings,
      Collection<? extends ISlot> slots) throws CannotInstantiateException
  {
    // this will copy the slots and then bind them
    super(variableBindings, slots);

    /*
     * let's verify that everything is defined. Specifically, we need a numeric
     * value for 'first' Since this is an action, conditional slots are not
     * expected, we can do this
     */
    ISlot slot = getSlot("first");
    Number value = null;
    if (slot != null && slot.getValue() instanceof Number)
      value = (Number) slot.getValue();

    if (slot == null || value == null)
      throw new CannotInstantiateException(
          "NextCountAction requires slot first be a defined numeric");

    // otherwise, everything is good
  }

  /**
   * When an action is bound, it creates a copy of itself - binds the variables,
   * returning the copy. In this manner, the production keeps this instance, and
   * the production's instantiation gets the bound instance.
   */
  @Override
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return new NextCountAction(variableBindings, getSlots());
  }

  /**
   * Do the actual work, returning a hypothetical amount of work time taken (0
   * default)
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    double valueOfFirst = ((Number) getBoundSlotValue("first")).doubleValue();
    double valueOfSecond = valueOfFirst+1;

    try
    {
      IChunkType chunkType = instantiation.getModel().getDeclarativeModule()
          .getChunkType("count-order").get();

      Map<String, Object> slotValues = new TreeMap<String, Object>();
      slotValues.put("first", valueOfFirst);
      slotValues.put("second", valueOfSecond);

      /*
       * now we create the chunk & encode it. The model will then retry the
       * retrieval
       */
      ChunkUtilities.createConfigureAndEncode(chunkType,
          String.format("count-%d", (int) valueOfSecond), slotValues);
    }
    catch (Exception e)
    {
      // this is for the chunktype retrieval.
      LOGGER.error("Failed to get chunktype ", e);
    }
    return 0;
  }

  public Object getBoundSlotValue(String slotName)
  {
   ISlot slot = getSlot(slotName);
   if(slot!=null) return slot.getValue();
   return null;
  }

}
