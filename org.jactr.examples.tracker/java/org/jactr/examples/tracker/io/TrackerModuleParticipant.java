package org.jactr.examples.tracker.io;

/*
 * default logging
 */
import org.jactr.examples.tracker.TrackerModule;
import org.jactr.io.participant.impl.BasicASTParticipant;

public class TrackerModuleParticipant extends BasicASTParticipant
{
  public TrackerModuleParticipant()
  {
    super("org/jactr/examples/tracker/io/tracker.jactr");
    setInstallableClass(TrackerModule.class);
  }

}
