package org.jactr.examples.timer;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * production interval timer example that records the time between two specified
 * production firings. It does this by using an
 * {@link IProceduralModuleListener} to detect the firing of productions. If the
 * production matches the start production, the timer is started and stopped
 * when the production matches the stop production. The start and stop
 * production names are specified by using the {@link IParameterized} interface.
 * 
 * @author harrison
 */
public class ProductionIntervalTimer implements IInstrument, IParameterized
{

  static public final String START_PATTERN = "StartProductionPattern";
  static public final String STOP_PATTERN = "StopProductionPattern";
  
  /*
   * we store the recorded times in org.apache.commons.math.stat package
   */
 
  private final SummaryStatistics         _sampleTimes =  new SummaryStatistics();

  /*
   * we use regex to match the start and stop production names
   */
  private Pattern                         _startProductionName;

  private Pattern                         _stopProductionName;

  /*
   * when the start production was fired
   */
  private double                          _startTime   = Double.NaN;

  /*
   * the event handler for the procedural module which will listen for the
   * production firing
   */
  private final IProceduralModuleListener _proceduralListener;
  
  private final IModelListener _modelListener = new ModelListenerAdaptor(){
    /**
     * we use this to actually log the data
     */
    public void modelStopped(ModelEvent me)
    {
      System.out.println(String.format(
          "Average time between productions : %.2f (%.2f)", _sampleTimes
              .getMean(), _sampleTimes.getStandardDeviation()));
      
      Aggregator.addDuration(_sampleTimes.getMean());
    }
  };

  public ProductionIntervalTimer()
  {
    /*
     * when the procedural module fires events, this will be called. We're only
     * interested in the productionFired event, all the others are Noops.
     */
    _proceduralListener = new ProceduralModuleListenerAdaptor() {
      public void productionFired(ProceduralModuleEvent pme)
      {
        /*
         * pass on the production (actually the IInstantiation) and
         * the simulated time that the event occured at.
         */
        checkProduction(pme.getProduction(), pme.getSimulationTime());
      }
    };
  }

  /**
   * called when the instrument is installed, this is the time to attach any
   * event listeners
   */
  public void install(IModel model)
  {
    /*
     * When the procedural module does something important, it fires off an
     * event. We can register a listener for that event quite simply. But we
     * need to consider how that even will be delivered. It can come
     * synchronously (on the model thread as the event occurs) or asynchronously
     * (to be delievered later). Since this is an instrument, which should not
     * affect model execution, we will attach asynchronously
     */
    IProceduralModule procMod = model.getProceduralModule();

    /*
     * the background executor is a low priority shared thread that is often
     * used for listeners. If we wanted to listen synchronously we could use
     * ExecutorServices.INLINE_EXECUTOR
     */
    procMod.addListener(_proceduralListener, ExecutorServices
        .getExecutor(ExecutorServices.BACKGROUND));
    
    model.addListener(_modelListener, ExecutorServices
        .getExecutor(ExecutorServices.BACKGROUND));
  }

  /**
   * after the model has run and before it is cleaned up, instruments are
   * uninstalled. we'll output the result here
   */
  public void uninstall(IModel model)
  {
    /*
     * detach the listener
     */
    model.getProceduralModule().removeListener(_proceduralListener);

    model.removeListener(_modelListener);
  }

  /**
   * after all instruments are installed, they are initialized. perform any long
   * running operations here.
   */
  public void initialize()
  {
    // noop
  }

  /**
   * test the production that fired to see if it start or stop and adjust the
   * clock accordingly
   * 
   * @param firedProduction
   * @param firingTime
   */
  private void checkProduction(IProduction firedProduction, double firingTime)
  {
    // timer hasn't been started, check for start
    if (Double.isNaN(_startTime))
    {
      if (_startProductionName.matcher(
          firedProduction.getSymbolicProduction().getName()).matches())
        _startTime = firingTime;
    }
    else
    {
      //timer has started
      if (_stopProductionName.matcher(
          firedProduction.getSymbolicProduction().getName()).matches())
      {
        _sampleTimes.addValue(firingTime - _startTime);
        _startTime = Double.NaN;
      }
    }
  }

  public String getParameter(String key)
  {
    if(START_PATTERN.equalsIgnoreCase(key))
      return _startProductionName.pattern();
    if(STOP_PATTERN.equalsIgnoreCase(key))
      return _stopProductionName.pattern();
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Arrays.asList(new String[]{START_PATTERN, STOP_PATTERN});
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * we set the start/stop regex patterns here
   */
  public void setParameter(String key, String value)
  {
    if(START_PATTERN.equalsIgnoreCase(key))
      _startProductionName = Pattern.compile(value);
    else if(STOP_PATTERN.equalsIgnoreCase(key))
      _stopProductionName = Pattern.compile(value);
  }

}
