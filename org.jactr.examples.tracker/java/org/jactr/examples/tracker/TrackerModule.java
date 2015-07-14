package org.jactr.examples.tracker;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.six.BasicBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.IMutableSlot;

/**
 * the tracker module is only needed as that is the only way to contribute a
 * buffer (since they are supposed to be theoretically motivated)
 * 
 * @author harrison
 */
public class TrackerModule extends AbstractModule
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TrackerModule.class);

  public TrackerModule()
  {
    super("tracker");
  }

  protected Collection<IActivationBuffer> createBuffers()
  {
    return Collections.singleton((IActivationBuffer) new BasicBuffer6(
        "tracker", this));
  }

  @Override
  public void initialize()
  {
    /*
     * let's make sure that we've got a tracker-data chunk in the buffer
     */
    IActivationBuffer buffer = getModel().getActivationBuffer("tracker");
    try
    {
      IDeclarativeModule decM = getModel().getDeclarativeModule();
      IChunkType chunkType = decM.getChunkType("track-data").get();
      IChunk chunk = decM.createChunk(chunkType, null).get();

      buffer.addSourceChunk(chunk);
    }
    catch (Exception e)
    {
      LOGGER.error("Could not find or create track-data chunk : ", e);
    }

  }

  public void reset()
  {

  }

}
