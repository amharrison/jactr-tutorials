package org.jactr.examples.custom;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

/**
 * Delayed execution action that fires with the completion of the production firing. That is, it executes in
 * the same time frame as the production finish, which is different than when the production
 * starts firing (+50ms). It accomplishes this by queueing up a {@link ITimedEvent} to fire
 * 50ms after the action's firing time. All chunk or buffer manipulating actions should do this as it will
 * ensure that it is properly sequenced with the default add, modify, and remove actions. <br/>
 * 
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
public class SetResultAction extends DefaultSlotAction
{

  public SetResultAction()
  {
  }

  /**
   * calls the {@link DefaultSlotAction} full constructor which will perform the variable resolution
   * @param variableBindings
   * @param slots
   * @throws CannotInstantiateException
   */
  private SetResultAction(VariableBindings variableBindings,
      Collection<? extends ISlot> slots) throws CannotInstantiateException
  {
    super(variableBindings, slots);
    
    Object value = getSlot("sum").getValue();
    if(!(value instanceof Number))
      throw new CannotInstantiateException("sum must be a number");
    
    /*
     * let's make sure count-chunk is correct
     */
    value = getSlot("add-chunk").getValue();
    if(!(value instanceof IChunk))
      throw new CannotInstantiateException("add-chunk needs to be a chunk");
    
    /*
     * it's a chunk, but is it the correct type?
     */
    IChunk chunk = (IChunk) value;
    IChunkType chunkType = chunk.getSymbolicChunk().getChunkType();
    if(!chunkType.getSymbolicChunkType().getName().equals("add"))
      throw new CannotInstantiateException(String.format("%s is not add", chunkType));
  }
  
  /**
   * just check to see if the count-chunk and sum slots exists.
   */
  public IAction bind(VariableBindings variableBindings)
  throws CannotInstantiateException
  {
    ISlot slot = getSlot("add-chunk");
    if(slot==null)
      throw new CannotInstantiateException("slot add-chunk must be defined");
    
    slot = getSlot("sum");
    if(slot==null)
      throw new CannotInstantiateException("slot sum must be defined");
    
    /*
     * this constructor will resolve the variables in the slots based on
     * the variable bindings
     */
    return new SetResultAction(variableBindings, getSlots());
  }

  /**
   * here we will change the sum slot of the count-chunk to the actual sum value. 
   * Instead of doing it immediately, we delay until the production finish time (typically
   * 50ms after the firingTime). 
   * @see IAction#fire(IInstantiation, double)
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    /*
     * first we create the timed event to do the work. This will be fired
     * on the model thread after the clock has advanced but before the conflict
     * resolution phase
     */
    IModel model = instantiation.getModel();
    IProceduralModule procMod = model.getProceduralModule();
    
    double finishTime = firingTime + procMod.getDefaultProductionFiringTime();
    
    final IChunk chunk = (IChunk) getSlot("add-chunk").getValue();
    final double sum = ((Number)getSlot("sum").getValue()).doubleValue();
    
    ITimedEvent futureEvent = new AbstractTimedEvent(firingTime, finishTime){
      public void fire(double currentTime)
      {
        super.fire(currentTime);
        
        /*
         * we only manipulate unencoded, non disposed chunks. It could
         * have been encoded if it was removed from the buffer before
         * this was called. Disposal is rare, but is good to check for
         * just in case.
         */
        if(!chunk.isEncoded() && !chunk.hasBeenDisposed())
        {
          /*
           * contains the symbolic portion of the chunk: name, type, slots
           */
          ISymbolicChunk sChunk = chunk.getSymbolicChunk();
          /*
           * now we need to get the correct slot
           */
          ISlot sumSlot = sChunk.getSlot("sum");
          /*
           * since the chunk isn't encoded, it's safe to assume that this
           * slot is actually mutable
           */
          ((IMutableSlot)sumSlot).setValue(sum);
          
          /*
           * and the model doesnt terminate until count==arg2
           */
          ((IMutableSlot)sChunk.getSlot("count")).setValue(sChunk.getSlot("arg2").getValue());
        }
      }
    };
    
    /*
     * now we queue up the timed event
     */
    model.getTimedEventQueue().enqueue(futureEvent);
    
    /*
     * we still return 0 because the production still only takes 50ms to fire
     */
    return 0;
  }


}
