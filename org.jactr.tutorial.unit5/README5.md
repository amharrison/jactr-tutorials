# Unit 5 Code Description
## Fan
The fan experiment utilized the same model/task/environment delineation that we've seen in prior tutorials.
In addition to spreading activation this section introduced the concept of markers and how they can be used to annotate
the log viewer with color coded blocks. This is useful rapidly finding relevant code. Markers can be placed via
the experiment interface, as illustrated in DisplayTrial. They can also be placed using API for [MarkerManager](https://github.com/amharrison/jactr-core/blob/master/org.jactr.tools/src/org/jactr/tools/marker/MarkerManager.java). Or via
a production using the custom [PointMarkerAction](https://github.com/amharrison/jactr-core/blob/master/org.jactr.tools/src/org/jactr/tools/marker/PointMarkerAction.java).

## Grouped Recall
The grouped recall model, like the unit 1 tutorials, is completely self-contained within the model itself. It has no
task or environment with which to work. These types of models are relatively rare but the added benefit of not requiring an 
environment means they can be run extremely fast.

## Siegler Task
The modeling of the Siegler task was primarily to acclimate you to the process of fitting a model. It introduced no 
new functionality.

## 1-hit Blackjack
This model represents a radical departure from what we've seen up to this point. Up till now, most of the models have had clear
delineations between model, task, and environment. Here we are squishing the task and environment together (simple twenty one and cards) then
jamming it into the model itself. This limits the reuse of either component (task-environment or model) by tightly coupling them together, as opposed
to doing it through the simulated perceptual interface. While it takes a bit more programming knowledge, this decision to integrate the model and 
the task-environment means that the model will run extremely fast (x6-10). Let's take a moment and go over the design decisions and their implications:

### Integrating with a model
In jACT-R there are three canonical ways of interfacing with the architecture (as opposed to the various ways of interfacing with individual modules, like the ISimilarityHandler used in this model.
The first is by extending the [IInstrument](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/instrument/IInstrument.java) interface. This is intended for code that passively 
inspects or interrogates a model for whatever information is desired. An instrument should not change or influence the execution of the model in any way other than run time. Loggers, tracers, probes, and
serializers fall under this category. The second method is by extending the [IExtension](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/extensions/IExtension.java) interface. 
Extensions are intended to influence the execution of a model in a purely practical, atheoretic manner. Optimizers and task-environment interfaces fall under this category. Finally, we have [IModule](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/module/IModule.java)s, which
provide theoretically motivated execution modifications. Since we are providing just a task-environment interface, an extension is used in this [example](https://github.com/amharrison/jactr-tutorials/blob/master/org.jactr.tutorial.unit5/src/org/jactr/tutorial/unit5/twentyone/jactr/SimpleTwentyOneExtension.java).

### Events and Listeners
We use the extension interface to allow our [extension](https://github.com/amharrison/jactr-tutorials/blob/master/org.jactr.tutorial.unit5/src/org/jactr/tutorial/unit5/twentyone/jactr/SimpleTwentyOneExtension.java) to hook into the model. It immediately attaches
an [IModelListener](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/model/event/IModelListener.java), specifically [PerCycleProcessor](https://github.com/amharrison/jactr-tutorials/blob/master/org.jactr.tutorial.unit5/src/org/jactr/tutorial/unit5/twentyone/jactr/PerCycleProcessor.java). This listener
allows the code to handle significant events of the model, such as its start or the beginning and end of each cycle. jACT-R is an asynchronous, event-based architecture. Everything of theoretic or functional significance fires off an event that can be handled by specialized listeners.
It is through the use of these listeners that all functionality is built. Here we use the modelStarted event to start the game.

### Timed-based control
Because the design of this task is strictly time-locked, we can implement it entirely using [ITimedEvent](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/queue/ITimedEvent.java)s, specifically two
[RunnableTimedEvent](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/queue/timedevents/RunnableTimedEvent.java)s, which simply execute a runnable after a set time has elapsed. The two runnables defined
*_postLearningPhase & _postActionPhase* represent the code to start and finish the hand of twenty one. As can be seen in *_postActionPhase*, we queue up a RunnableTimedEvent for the next phase only if there are more games to play:
```java
IClock clock = ACTRRuntime.getRuntime().getClock(model);
      
      if(_summaryHistory.size()<TOTAL_BINS)
        model.getTimedEventQueue().enqueue(new RunnableTimedEvent(clock.getTime() + 10, _postLearningPhase));
      else
      {
        DataCollection.get().logData(_summaryHistory.toArray(new Double[0]));
        
        throw new ModelTerminatedException("End of the run");
      }
```
If there aren't more hands to play, the code throws a *ModelTerminatedException*, this is a safe way to exit a model from anywhere within a model's thread (which a timedevent always is).

### Manipulating Buffers and Chunks
Because all of this code is run from within timed events, we know that it is on the model thread and we should have relatively free reign to manipulate the contents.
In the following snippet you can see the goal buffer retrieved and its contents fetched. If there was nothing in the goal buffer a new chunk is built and inserted.
```java
...
      IActivationBuffer goalBuffer = model.getActivationBuffer(IActivationBuffer.GOAL);
      IChunk goalChunk = goalBuffer.getSourceChunk();

      _game.startHand();
      try {
        boolean goalWasNull = goalChunk == null;
        goalChunk = buildGoal(goalChunk, model);
        if (goalWasNull)
          goalBuffer.addSourceChunk(goalChunk);
...   
```
The methods *buildGoal* and *updateGoal* both use the [FluentChunk](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/fluent/FluentChunk.java) fluent builder to manipulate slot values. This
is always a good idea as it does so in a thread-safe manner, handling the chunk's write lock itself. 

### ISimilarityHandler
The [similarity handler](https://github.com/amharrison/jactr-tutorials/blob/master/org.jactr.tutorial.unit5/src/org/jactr/tutorial/unit5/twentyone/jactr/NumericSimilarityHandler.java) used to automatically compute similarity scores for numbers is a fairly cut and dry piece of code.
Basically it tests two objects to see if they are both numbers, then it applies the similarity transform. Caching is handled elsewhere.

```java
  @Override
  public boolean handles(Object one, Object two) {
    boolean rtn = one instanceof Number && two instanceof Number;
    return rtn;
  }

  @Override
  public double computeSimilarity(Object one, Object two, double maxDiff, double maxSim) {
    
    double a = ((Number)one).doubleValue();
    double b = ((Number)two).doubleValue();
    
    double sim = -Math.abs(a-b)/Math.max(a, b);
    return sim;
  }
```

### Custom Actions
By bypassing the perceptual motor modules entirely, we need a means to collect the response from the model. That's 
where [TwentyOneResponse](https://github.com/amharrison/jactr-tutorials/blob/master/org.jactr.tutorial.unit5/src/org/jactr/tutorial/unit5/twentyone/jactr/TwentyOneResponse.java) comes in. It is a custom action that can be invoked using the *proxy* jactr directive.

```
  proxy("org.jactr.tutorial.unit5.twentyone.jactr.TwentyOneResponse"){
    response = =action
  }
```
Custom actions must provide both a *bind* and *fire* method. The *fire* method is obvious, it returns the amount of time
the action should take, defaults to 0. The *bind* method takes a little more explaining. The binding of an action is the
last thing that is done when a production is fully instantiated. It is when variables are fully resolved. The binding of an action
means you return a copy of the action with its variables fully resolved. This is usually done with a constructor accepting both 
the [VariableBindings](https://github.com/amharrison/jactr-core/blob/master/org.jactr/java/org/jactr/core/production/VariableBindings.java) and a collection of slots. 

