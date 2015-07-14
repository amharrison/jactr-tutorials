package org.commonreality.sensors.example.creators;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IMutableObject;
import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.base.PerceptManager;
import org.commonreality.sensors.base.impl.AbstractObjectCreator;
import org.commonreality.sensors.base.impl.DefaultObjectKey;

public class StringObjectCreator extends AbstractObjectCreator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StringObjectCreator.class);

  private BaseSensor _sensor;
  
  public StringObjectCreator()
  {
  }

  public boolean handles(Object object)
  {
    return object instanceof String;
  }
  
  public void installed(PerceptManager manager)
  {
   _sensor = manager.getSensor();
  }

  /**
   * initialize some default values
   */
  @Override
  protected void initialize(DefaultObjectKey objectKey,
      IMutableObject afferentPercept)
  {
    // make it a visual percept
//    afferentPercept.setProperty(IVisualPropertyHandler.IS_VISUAL, Boolean.TRUE);
    afferentPercept.setProperty(IAuralPropertyHandler.IS_AUDIBLE, Boolean.TRUE);
    afferentPercept.setProperty(IAuralPropertyHandler.AURAL_MODALITY, Boolean.TRUE);
    afferentPercept.setProperty(IAuralPropertyHandler.ONSET, _sensor.getClock().getTime());
  }
}
