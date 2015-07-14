package org.jactr.examples.visual.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.memory.impl.encoder.AbstractVisualEncoder;

/**
 * example visual encoder for visual percepts that are of type 'foo'. Extenders
 * should extend (and call) {@link AbstractVisualEncoder#updateSlots}, which is
 * used to set the encoder slot values. The default implementation handles the
 * basics (size, location, color). <br/>
 * This example checks {@link IAfferentObject} for the presence of 'foo' in the
 * type parameter. If so, we can set the slot values to match the custom content
 * for the percept of a 'foo'. <br/>
 * updateSlots is called to assign the slot values. No other work is necessary.
 * Creation and disposal of unused percepts is handled automatically. <br/>
 * Because percepts can change during their existence, the superclass handles
 * detection of changes due to movement (isDirty()), requiring a slot update
 * (again, calling updateSlots()). It can also detect changes that require a
 * complete reencoding of the percept via isTooDirty(). Subclasses may extend
 * these two methods to check for other changes. <br/>
 * 
 * @author harrison
 */
public class FooVisualEncoder extends AbstractVisualEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(FooVisualEncoder.class);

  static private String              TYPE_NAME = "foo";

  static private String              FEATURE   = "bar.feature";

  /**
   * We presuppose the existence of the foo visual chunktype
   */
  public FooVisualEncoder()
  {
    super(TYPE_NAME);
  }

  /**
   * does the IAfferentObject have the type (string[]) property? and if so, is
   * one of them 'foo'
   */
  @Override
  protected boolean canEncodeVisualObjectType(IAfferentObject afferentObject)
  {
    try
    {
      String[] types = getHandler().getTypes(afferentObject);
      for (String kind : types)
        if (kind.equalsIgnoreCase(TYPE_NAME)) return true;

      return false;
    }
    catch (Exception e)
    {
      // if something goes wrong, we mark this as not compatible
      return false;
    }
  }

  protected void updateSlots(IAfferentObject afferentObject, IChunk encoding,
      IVisualMemory memory)
  {
    super.updateSlots(afferentObject, encoding, memory);

    /**
     * we have a foo. All foo's should have custom.feature, but they may not, so
     * we like to be robust.
     */
    try
    {
      double featureValue = getHandler().getDouble(FEATURE, afferentObject);
      ((IMutableSlot) encoding.getSymbolicChunk().getSlot("bar"))
          .setValue(featureValue);
    }
    catch (Exception e)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("%s is a foo, but is missing feature %s",
            encoding, FEATURE), e);
    }

  }

  /**
   * extend this method to add to the checks on modified percepts. If isDirty
   * returns true, updateSlots will be called for the <b>existing</b> encoding
   * of the percept. (non-Javadoc)
   * 
   * @see org.jactr.modules.pm.visual.memory.impl.encoder.AbstractVisualEncoder#isDirty(org.commonreality.object.IAfferentObject,
   *      org.jactr.core.chunk.IChunk,
   *      org.jactr.modules.pm.common.memory.IPerceptualMemory)
   */
  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    return super.isDirty(afferentObject, oldChunk, memory);
  }

  /**
   * extend this method to detect percept changes that are so major an entirely
   * new chunk is required.
   */
  protected boolean isTooDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IVisualMemory visualMemory)
  {
    return super.isTooDirty(afferentObject, oldChunk, visualMemory);
  }

}
