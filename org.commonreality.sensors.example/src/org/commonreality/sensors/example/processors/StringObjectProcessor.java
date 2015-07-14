package org.commonreality.sensors.example.processors;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.modalities.aural.ICommonTypes;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IMutableObject;
import org.commonreality.sensors.base.impl.AbstractObjectProcessor;
import org.commonreality.sensors.base.impl.DefaultObjectKey;

public class StringObjectProcessor extends AbstractObjectProcessor
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StringObjectProcessor.class);

  public StringObjectProcessor()
  {
  }

  public boolean handles(DefaultObjectKey object)
  {
    return object.getObject() instanceof String;
  }

  public void process(DefaultObjectKey object, IMutableObject simulationObject)
  {
    /*
     * let's spoof some visual properties. Ideally you wouldn't set all the
     * properties every time, rather just those that change on each call to
     * process.
     */

//    String string = (String) object.getObject();
//
//    // currently visible, required
//    simulationObject.setProperty(IVisualPropertyHandler.VISIBLE, Boolean.TRUE);
//
//    // 1 meter in front of the eye, required
//    simulationObject.setProperty(IVisualPropertyHandler.RETINAL_DISTANCE, 1);
//
//    // 1 degree visual angle up and to the right (0,0 is center), required
//    simulationObject.setProperty(IVisualPropertyHandler.RETINAL_LOCATION,
//        new double[] { 1, 1 });
//
//    // 2 degrees visual angle in size, required
//    simulationObject.setProperty(IVisualPropertyHandler.RETINAL_SIZE,
//        new double[] { 2, 2 });
//
//    // type of visual percept, required
//    simulationObject.setProperty(IVisualPropertyHandler.TYPE,
//        new String[] { "text" });
//
//    // actual unique identifier, required
//    simulationObject.setProperty(IVisualPropertyHandler.TOKEN, string);
//
//    // text value, if available
//    simulationObject.setProperty(IVisualPropertyHandler.TEXT, string);
//
//    // horizontal, optional
//    simulationObject.setProperty(IVisualPropertyHandler.SLOPE, 0);
//
//    // RGBA, optional
//    simulationObject.setProperty(IVisualPropertyHandler.COLOR, new double[] {
//        1, 0, 0, 1 });
    
    simulationObject.setProperty(IAuralPropertyHandler.TYPE, new String[]{ICommonTypes.SPEECH});
    simulationObject.setProperty(IAuralPropertyHandler.TOKEN, (String) object.getObject());
  }
  
  public void deleted(DefaultObjectKey object)
  {
    /*
     * if we had attached any listeners to the object contained by the key, we'd
     * remove them here.
     */

  }

}
