package org.jactr.examples.timer;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * simple static singleton to collect data from the ProductionIntervalTimer.
 * Since it [interval timer] is instantiated for each model run, we need this to
 * hold the values across model runs, but still be able to clear them out for
 * each different block of runs.
 * 
 * @author harrison
 */
public class Aggregator
{

  static private final Collection<Number> _durations = Collections
                                                         .synchronizedList(new ArrayList<Number>());

  static public void addDuration(double duration)
  {
    _durations.add(duration);
  }

  static public Collection<Number> getDurations(Collection<Number> container)
  {
    if (container == null) container = new ArrayList<Number>();

    synchronized (_durations)
    {
      container.addAll(_durations);
    }

    return container;
  }

  static public void clear()
  {
    _durations.clear();
  }
}
