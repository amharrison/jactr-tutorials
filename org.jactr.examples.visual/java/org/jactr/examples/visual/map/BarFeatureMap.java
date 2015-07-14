package org.jactr.examples.visual.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.visual.memory.impl.map.AbstractSortedVisualFeatureMap;
import org.jactr.modules.pm.visual.memory.impl.map.AbstractVisualFeatureMap;

/**
 * A custom, ordered, visual feature map. Unordered data can use
 * {@link AbstractVisualFeatureMap}. Data storage, updating, indexing,
 * retrieval, and normalization is all taken care of by the superclass.
 * Extenders merely provide a means to extract the data from the percept (
 * {@link #extractInformation(IAfferentObject)}, from slot values (
 * {@link #toData(ISlot)}), and finally to check to see if a slot value has a
 * valid value to be extracted ({@link #isValidValue(ISlot)}. <br/>
 * 
 * @author harrison
 */
public class BarFeatureMap extends AbstractSortedVisualFeatureMap<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(BarFeatureMap.class);

  /**
   * the name of the property of the percept that contains the value
   */
  static private String              CR_PROPERTY_NAME  = "bar.feature";

  static private String              VIS_LOC_SLOT_NAME = "bar";

  public BarFeatureMap()
  {
    super(VIS_LOC_SLOT_NAME, CR_PROPERTY_NAME);
  }

  @Override
  protected Double toData(ISlot slot)
  {
    return ((Number) slot.getValue()).doubleValue();
  }

  @Override
  protected boolean isValidValue(ISlot slot)
  {
    return slot.getValue() instanceof Number;
  }

  /**
   * this will only be called if there is a property matching the
   * CR_PROPERTY_NAME, so we just pull out the value and do some error checking.
   */
  @Override
  protected Double extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().getDouble(CR_PROPERTY_NAME, afferentObject);
    }
    catch (Exception e)
    {
      return Double.NaN;
    }
  }
}
