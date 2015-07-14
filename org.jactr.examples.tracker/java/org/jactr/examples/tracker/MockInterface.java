package org.jactr.examples.tracker;

/*
 * default logging
 */
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MockInterface
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(MockInterface.class);

  static private final long          CONNECTION_LAG = 250;

  private boolean                    _isConnected;

  private volatile boolean                    _requestedData = false;

  // just used to randomly have data available for processing
  private volatile double                     _requestTime;

  synchronized public boolean isConnected()
  {
    return _isConnected;
  }

  public void connect(String connectionInfo) throws IOException
  {
    try
    {
      Thread.sleep(CONNECTION_LAG * 4);
    }
    catch (InterruptedException e)
    {
    }
    synchronized(this)
    {
      _isConnected = true;
    }
  }

  public void disconnect() throws IOException
  {
    synchronized (this)
    {
      _isConnected = false;
    }
    try
    {
      Thread.sleep(CONNECTION_LAG * 4);
    }
    catch (InterruptedException e)
    {
    }
  }

  /**
   * make a data request. takes 250 ms to do its thing
   * 
   * @throws IOException
   */
  public void requestUpdate(double modelTime) throws IOException
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("requesting update @ %.2f",modelTime));

    
    
    try
    {
      Thread.sleep(CONNECTION_LAG);
    }
    catch (InterruptedException e)
    {
    }
    
    synchronized (this)
    {
      _requestedData = true;
      _requestTime = modelTime;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("requested"));
  }

  /**
   * is there any data available to be processed? Only if we are connected, a
   * request has been made
   * 
   * @return
   */
  synchronized public boolean hasNewData()
  {
    return isConnected() && _requestedData;
  }

  /**
   * extract the newly available data
   * 
   * @return
   */
  public Map<String, Object> getNewData()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("getting new data"));
    try
    {
      Thread.sleep(CONNECTION_LAG);
    }
    catch (InterruptedException e)
    {
    }


    
    synchronized (this)
    {
      Map<String, Object> rtn = new TreeMap<String, Object>();  
      long time = System.currentTimeMillis();
      rtn.put("time", time);
      rtn.put("requested-at", _requestTime);
      rtn.put("changed", Boolean.TRUE);
      
      if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("gotten %d for %.2f", time, _requestTime));
      
      _requestedData = false;
      return rtn;
    }
  }

}
