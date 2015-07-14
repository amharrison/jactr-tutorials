package org.commonreality.sensors.example;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.sensors.base.BaseSensor;
import org.commonreality.sensors.example.creators.StringObjectCreator;
import org.commonreality.sensors.example.processors.StringObjectProcessor;

public class StringSensor extends BaseSensor
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StringSensor.class);

  private List<String>               _wordsToSend;

  private String                     _lastWord;

  /**
   * what time to send the next word
   */
  private double                     _sendNextWordAt;

  @Override
  public String getName()
  {
    return "example";
  }

  /**
   * ideally you'd perform some configuration here. these options are provided
   * (typically) from the environment configuration file at the start of the
   * run.
   */
  @Override
  public void configure(Map<String, String> options) throws Exception
  {
    super.configure(options);

    _wordsToSend = new ArrayList<String>();
    /*
     * TextSource property is defined in the plugin.xml extension and can be
     * customized through the run configuration
     */
    for (String word : options.get("TextSource").split(" "))
      _wordsToSend.add(word);
    
    /**
     * install the default creator and processor
     */
    getPerceptManager().install(new StringObjectCreator());
    getPerceptManager().install(new StringObjectProcessor());
    /*
     * just for giggles, let's make it realtime (ish)
     */
    setRealtimeClockEnabled(true);
  }

  /**
   * at the top of the cycle, we peel of a new word and create a percept for it,
   * removing the old word in the process. This is a silly single threaded
   * example. Normally, you'd have some other thread churning along in response
   * to other events and interacting with the percept manager
   */
  @Override
  protected void startOfCycle()
  {
    if (_sendNextWordAt <= getClock().getTime())
    {
      if (_lastWord != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Removing %s", _lastWord));

        getPerceptManager().flagForRemoval(_lastWord);
      }
      _lastWord = null;

      if (_wordsToSend.size() > 0)
      {
        /*
         * peel off a word, notify the percept manager to remove the previous
         * one and add the new one
         */
        String newWord = _wordsToSend.remove(0);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Perceiving new word %s", newWord));

        // percept manager will handle all the heavy lifting for us
        getPerceptManager().markAsDirty(newWord);

        _lastWord = newWord;

        _sendNextWordAt = getClock().getTime() + 1; // 1 second later
      }
      else
        // setting this to + inf will prevent any further processing
        _sendNextWordAt = Double.POSITIVE_INFINITY;
    }
  }
  
  

  /**
   * if there were any afferent processing yet, it would go here. The returned
   * value is the estimate of the next time that the clock can be accerated to.
   * return NaN if you have no idea or would rather just use BaseSensor's time
   * increment mechanism
   */
  @Override
  protected double processMotor()
  {
    return Double.NaN;
  }

  

}
