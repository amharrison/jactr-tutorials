package org.jactr.examples.tracker;

/*
 * default logging
 */
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.extensions.IllegalExtensionStateException;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.tools.misc.ChunkUtilities;

public class TrackerExtension implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(TrackerExtension.class);

  static public final String         ADDR_PARAM = "ConnectTo";

  private IModel                     _model;

  private String                     _connectionString;

  /**
   * model listener allows us to know when the model is about to run and when
   * each cycle is starting/ending
   */
  private IModelListener             _modelListener;

  /**
   * our interface to the pseudo network
   */
  private MockInterface              _networkInterface;

  /**
   * used to flag whether we have consumed new data or not.
   */
  private volatile boolean           _dataProcessingQueued;

  /**
   * the buffer we'll update
   */
  private IActivationBuffer          _trackerBuffer;

  /**
   * thread on which we will do the heavily lifting
   */
  private ExecutorService            _networkExecutor;

  public TrackerExtension()
  {

    _modelListener = new ModelListenerAdaptor() {

      /**
       * at each cycle, check to see if the network interface has new data for
       * us to process. If so, execute the processing on the dedicated executor.
       */
      public void cycleStarted(ModelEvent me)
      {
        if (_networkInterface.hasNewData() && !_dataProcessingQueued)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("new data is available"));

          _dataProcessingQueued = true;

          _networkExecutor.execute(new Runnable() {
            public void run()
            {
              if (LOGGER.isDebugEnabled())
                LOGGER.debug(String.format("processing data"));

              // lengthy operation
              processData();
              //reset the flag
              _dataProcessingQueued = false;
            }
          });
        }
      }


      /**
       * called on startup, this is our chance to actually connect to the
       * network interface. Again, we do it in a dedicated connection
       */
      public void modelConnected(final ModelEvent me)
      {
        _networkExecutor.execute(new Runnable() {
          public void run()
          {
            try
            {
              if (!_networkInterface.isConnected())
              {
                _networkInterface.connect(_connectionString);
                // make initial request
                _networkInterface.requestUpdate(me.getSimulationTime());
              }
            }
            catch (Exception e)
            {
              // do something
            }
          }
        });
      }

      /**
       * and disconnect on the network thread
       */
      public void modelDisconnected(ModelEvent me)
      {
        _networkExecutor.execute(new Runnable() {
          public void run()
          {
            try
            {
              if (_networkInterface.isConnected())
                _networkInterface.disconnect();
            }
            catch (Exception e)
            {
              // do something smart
            }
            finally
            {
              _networkInterface = null;
            }

          }
        });
      }
    };
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return "tracker";
  }

  public void install(IModel model)
  {
    _model = model;
    /*
     * first test to see if the tracker module is installed
     */
    TrackerModule tm = (TrackerModule) _model.getModule(TrackerModule.class);
    if (tm == null)
      throw new IllegalExtensionStateException(
          "TrackerModule must be installed");

    _trackerBuffer = _model.getActivationBuffer("tracker");

    /*
     * since network based code often is a massive bottleneck, we want to put it
     * on its own thread. We'll use this executor
     */
    _networkExecutor = Executors.newSingleThreadExecutor();

    /*
     * but we'll queue up processing on the executor based on the model events,
     * which we will handle inline with the model
     */
    _model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

  }

  public void uninstall(IModel model)
  {
    _model.removeListener(_modelListener);
    _model = null;

    if (_networkExecutor != null && !_networkExecutor.isShutdown())
    {
      _networkExecutor.shutdown();
      _networkExecutor = null;
    }
  }

  public String getParameter(String key)
  {
    if (ADDR_PARAM.equalsIgnoreCase(key)) return _connectionString;
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(ADDR_PARAM);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if (ADDR_PARAM.equalsIgnoreCase(key)) _connectionString = value;
  }

  public void initialize() throws Exception
  {
    /*
     * we do any lengthy, non-runtime related initialization here..
     */
    _networkInterface = new MockInterface();
  }

  /**
   * hypothetical processing of data.. We take the data and then (safely) modify
   * the chunk that is in the buffer to match
   */
  private void processData()
  {
    final Map<String, Object> data = _networkInterface.getNewData();

    /*
     * to safely update the buffer contents, we must do it on the model thread.
     * we could cache the data locally and use an additional, inline model
     * listener and do the work in cycleStarted(), or use a timed event.
     * ChunkUtilities.modifyLater does the later
     */
    ChunkUtilities.manipulateChunkLater(_trackerBuffer,
        new ChunkUtilities.IChunkModifier() {

          public void modify(IChunk chunk, IActivationBuffer buffer)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(String.format("Updating buffer"));

            /*
             * we are assuming that the fields in data match 1-1 to the slots of
             * the track-data chunk
             */
            ISymbolicChunk sc = chunk.getSymbolicChunk();
            for (Map.Entry<String, Object> entry : data.entrySet())
            {
              IMutableSlot slot = (IMutableSlot) sc.getSlot(entry.getKey());
              if (slot == null) continue;
              slot.setValue(entry.getValue());
            }

          }
        });

    /*
     * after we've processed the data, let's request an update
     */

    try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("requesting update"));

      _networkInterface.requestUpdate(getModel().getAge());
    }
    catch (IOException e)
    {
      // handle this gracefully
    }
  }
}
