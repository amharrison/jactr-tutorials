# Unit 2: Perception and Motor Actions in ACT-R

## ACT-R Interacting with the World

This unit will introduce some of the mechanisms which allow ACT-R to interact with the world, which for the purposes of the tutorial will be experiments presented via the computer.  This is made possible with the addition of perceptual and motor modules which are now an integrated part of the system.  It is a set of modules for ACT-R which provides a model with visual, motor, auditory, and vocal capabilities as well as the mechanisms for interfacing those modules to the world.  The default mechanisms which we will use allow the model to interact with the computer i.e. process the visual items presented, press keys and move and click the mouse.  Other more advanced interfaces can be developed, but that is beyond the scope of the tutorial.

## The First Experiment
The *org.jactr.tutorial.unit2* project contains code to present a very simple experiment and a model that can perform the task.  The experiment consists of a window in which a single letter is presented.  The participant’s task is to press that key. When a key is pressed, the display is cleared and the experiment ends.  

If you run the *Unit 2 - experiment* run configuration, you will be presented with the human version of the experiment.

The *Unit 2 - model* run configuration contains the model version. Both versions use the same experiment configuration (*src/org.jactr.tutorial.unit2.experiment/experiment.xml*) and run the same experiment. The only difference is in the run configuration.

In the following sections we will look at how the model perceives the letter being presented, how it issues a response, and briefly discuss some parameters in ACT-R.  

### Control and Representation
Before looking at the details of the new buffers and modules, however, there is something different about this model relative to the models that were used in unit 1 which needs to be addressed.  There are two chunk types created for this model:

```
 chunktype read-letters {state = null}
 chunktype array {letter = null}
```


The chunk type read-letters has one slot which is called state and will be used to track the current task state for the model.  The other chunk type, array, also has only one slot, which is called letter, and will hold a representation of the letter which is seen by the model.  

In unit 1, we saw that the chunk placed into the goal buffer had slots which held all of the information relevant to the task – one buffer held all of the information.  That represents how things have typically been done with ACT-R models in the past, but with ACT-R 6.0, a more distributed representation of the model’s “state” is the preferred means of modeling.  Now, we will use two buffers to hold the information.  The *goal* buffer will be used to hold control state information – the internal representation of what the model is doing and where it is in the task. In this model the *goal* buffer will hold chunks of type read-letters.  A different buffer, the *imaginal* buffer, will hold the chunk which contains the problem state information, and in this model that will be a chunk of type array.

### The State Slot
In this model, the state slot of the chunk in the goal buffer will maintain information about what the model is doing.  It can then be used to explicitly indicate which productions are appropriate at any time. This is often done when writing ACT-R models because it is easy to specify and makes them easier to follow.  It is however not always necessary to do so, and there are other means by which the same control flow can be accomplished.  In fact, as we will see in a later unit there are consequences to keeping extra information in the goal chunk. However, because it does make the production sequencing in a model clearer you will see state slots in many of the models in the tutorial even if they are not always necessary.  As an additional challenge for this unit, you can try to modify the demo2 model so that it works without needing to use the goal buffer or explicit state markers at all.

## The Imaginal Module

The first new module we will describe in this unit is the imaginal module.  This module has a buffer called imaginal which is used to create new chunks.  These chunks will be the model’s internal representation of information – its internal image (thus the name imaginal module).  Like any buffer, the chunk in the imaginal module can be modified by the productions to build that representation using RHS modification actions as shown in unit 1.  

The important thing about the imaginal buffer is how the chunk first gets into the buffer.  Unlike the goal buffer’s chunk which we have been creating and placing there in advance of the model starting, the imaginal module will create the chunk for the imaginal buffer in response to a request from a production.

All requests to the imaginal module through the imaginal buffer are requests to create a new chunk.  The imaginal module will create a new chunk using the chunk-type and any initial slot values provided in the request and place that chunk into the imaginal buffer.  An example of this is shown in the encode-letter production:

```
production encode-letter{
   goal{
     isa read-letters
     state = attend
   }
   visual{
     isa text
     value = =letter
   }
 }{
   goal{
     state = respond
   }
   +imaginal{
     isa array
     letter = =letter
   }
 }
```

We will explain the details of how the text chunk gets into the visual buffer in the next section.  For now, we are interested in this request on the RHS:

```
+imaginal{
     isa array
     letter = =letter
   }
```

This request of the imaginal buffer is asking the imaginal module to create a chunk of type array and which has the value of the variable =letter in its letter slot.  

An important detail of the request to the imaginal module is that the chunk is not immediately placed into the buffer as a result of the request.  It took .2 seconds before the chunk was made available.  This is an important aspect of the imaginal module – it takes time to build a representation.  The amount of time that it takes the imaginal module to create a chunk is a fixed cost, and the default time is .2 seconds (though that can be changed with a parameter).  In addition to the time cost, the imaginal module is only able to create one new chunk at a time.  That does not impact this model because it is only creating the one new chunk in the imaginal buffer, but there are times where that can matter.  In such situations one may want to verify that the module is available to create a new chunk and how one does that is described later in the unit.

Thus in this model, the imaginal buffer will hold a chunk which contains a representation of the letter which the model reads from the screen.  For this simple task, that representation is not strictly necessary because the model could use the information directly from the vision module to do the task, but for most tasks there will be more information which must be maintained thus requiring such a chunk to be created.  In particular, for this unit’s assignment the model will need to read multiple letters which must be considered before responding.

## The Vision Module
Many tasks involve interacting with visible stimuli and the vision module provides a model with a means for acquiring visual information.  It is designed as a system for modeling visual attention.  It assumes that there are lower-level perceptual processes that generate the representations with which it operates, but it does not model those perceptual processes in detail.  It includes some default mechanisms for parsing text and other simple visual features from a window and provides an interface that one can use to extend it when necessary.

The vision module has two buffers.  There is a visual buffer that can hold a chunk that represents an object in the visual scene and a visual-location buffer that holds a chunk which represents the location of an object in the visual scene.  As with all modules, it also responds to queries of the buffers about the state of the module.  It also responds to more detailed queries which will not be covered in this unit.   All of this is shown in the demo2 model in the two productions find-unattended-letter and attend-letter. 

### Visual-location Buffer

The find-unattended-letter production applies whenever the goal buffer’s chunk has a state of start (which is how the chunk is initially created):

```
 production find-unattended-letter {
   goal{
     isa read-letters
     state = start
   }
 }{
   +visual-location{
     isa visual-location
     :attended = null
   }
   goal{
     state = find-location
   }
 }
```

It makes a request of the visual-location buffer and it changes the goal state to find-location. The visual-location request asks the vision module to find the location of an object in its visual scene (which is the experiment window for this model) that meets the specified requirements, build a chunk to represent the location of that object if one exists, and place that chunk in the visual-location buffer. The visual-location requests always finish immediately which reflects the idea that there is a perceptual system operating in parallel that makes these visual features immediately available. 

```
chunktype visual-location {
  screen-x = null
  screen-y = null
  distance = null
  color    = null
  size     = null
  kind     = null
  nearest  = null
  value    = null
}
```

There are a lot of slots in a visual-location, but most are not important for now, and can be ignored.   The first two, screen-x and screen-y, are the only ones we are concerned with right now.  They encode the exact coordinates of the object in the visual scene. **Note:** The coordinate system in jACT-R is retinotopic in visual angles, with 0,0 being the center of field of view, increasing up and to the right. This is in contrast to canonical ACT-R where the upper-left corner of the window is screen-x 0 and screen-y 0 with values increasing to the right and down.
In general, the specific values are not that important for the model, and do not need to be specified when making a request for a location.  There is a set of descriptive specifiers that can be used for requests on those slots, like lowest or highest, but again those details will not be discussed until unit 3.  

#### The attended request parameter

If we look at the request which was made of the visual-location buffer in the find-unattended-letter production:

```
+visual-location{
     isa visual-location
     :attended = null
   }
```

we see that in addition to specifying “isa visual-location” it includes “:attended = null” in the request.
However, looking at the chunktype for a visual-location we find that it does not have a slot called attended or :attended.

This :attended specification is called a request parameter.  It acts like a slot in the request, but does not correspond to a slot in the chunk-type specified.  A request parameter is valid for any request on the buffer regardless of the chunk-type specified.  Request parameters are used to supply general information to the module about a request which may not be desirable to have in the resulting chunk that is placed into the buffer. A request parameter is specific to the particular buffer and will always start with a “:” which distinguishes it from an actual slot of the chunk-type.  We will discuss a couple different request parameters in this unit and later units as we introduce more buffers.

For a visual-location request one can use the :attended request parameter to specify whether the vision module returns the location of an object which the model has previously looked at (attended to) or not.  If it is specified as null, then the request is for a location which the model has not attended, and if it is specified as t, then the request is for a location which has been attended previously.  There is also a third option, new.  This means that not only has the model not attended to the location, but also that the object has recently appeared in the visual scene.  

The attend-letter production applies when the goal state is find-location, there is a visual-location in the visual-location buffer, and the vision module is not currently active:

```
production attend-letter{
   goal{
     isa read-letters
     state = find-location
   }
   visual-location{
     isa visual-location
   }
   ?visual{
     state = free
   }
 }{
   +visual{
     isa attend-to
     where = =visual-location
   }
   goal{
     state = attend
   }
 }
```


On the LHS of this production are two conditions that have not been seen before.  The first is a test of the visual-location buffer. Notice that the only test on the buffer is the isa slot.  All that is necessary is to make sure that there is a chunk of type visual-location in the buffer.  The details of its slot values do not matter.  Then, a query is made of the visual buffer.  

### Checking  a module’s state

On the LHS of attend-letter a query is made of the visual buffer to test that the state of the vision module is free. All buffers will respond to a query for the module’s state and the possible values for that query are busy, free, or error as was shown in unit 1. The test of state free is a check to make sure the buffer being queried is available for a new request.  If the state is free, then it is safe to issue a new request, but if it is busy then it is not and the results of doing so depend on how the module handles such a situation.  

Typically, a module is only able to handle one request to a buffer at a time.  This is the case for both the imaginal and visual buffers which require some time to produce a result. Since all modules operate in parallel it might be possible for the procedural module to select a new production which makes a new request to a module that is still working on a previous request.  If a production were to fire at such a point and issue another request to a module which is busy and only able to handle one request at a time, that is referred to as “jamming” the module.  When a module is jammed, it will output a warning message in the trace to let you know what has happened.  Thus, when there is the possibility of jamming a module one should query its state to prevent such situations. 

Note that we did not test the state of the visual-location buffer in the find-unattended-letter production before issuing the visual-location request because we know that those requests always complete immediately and thus the visual-location state is always free.  We also did not test the state of the imaginal module before making the request to the imaginal buffer in the encode-letter production.  In that case, a carefully written model would check such a situation, but because this model only makes one such request we omitted the check because we knew that the state would be free at that time. However, it is always a good idea to query the state in every production that makes a request that could potentially jam a module even if you know that it will not happen because of the structure of the other productions.  That makes it clear to anyone else who may read the model and you may decide to later apply that model to a different task where that assumption no longer holds.

In addition to the state, there are also other queries that one can make of a buffer.  Unit 1 presented the general queries that are available to all buffers.  Some buffers also provide queries that are specific to the details of the module and those will be described as needed in the tutorial.  One can also find all the queries to which a module responds in the reference manual.

### Visual buffer

On the RHS of attend-letter it makes a request of the visual buffer which isa move-attention and it specifies the screen-pos[ition] as the visual location in the visual-location buffer. A request of the visual buffer for a move-attention is a request for the vision module to move its attention to the specified location, create a chunk which encodes the object that is there, and place that chunk into the visual buffer.  

If a request to move-attention is made at time 0.100 seconds that encoding does not complete and result in a chunk being placed into the visual buffer until 0.185 seconds.  Those 85 ms represent the time to shift attention and create the visual object.  Altogether, counting two production firings (one to request the location and one to request the attention shift) and the 85 ms to execute the attention shift and object encoding, it takes 185 ms to create the chunk that encodes the letter on the screen. 

As you step through the model you will find a chunk in the visual buffer after those actions have occurred:

```
TEXT0-0
  ISA TEXT
   SCREEN-POS  VISUAL-LOCATION0-0-0
   VALUE  "v"
   COLOR  BLACK
   HEIGHT  10
   WIDTH  7
```

The chunk is of type text because it is a letter that was encoded from the screen.  The screen-pos slot holds the location chunk for that object.  The value slot holds a string that contains the text encoded from the screen, in this case a single letter.  The status slot is empty, and is essentially a free slot which can be used by the model to encode additional information in that chunk.  The color, height, and width slots hold information about the visual features of the item attended.

After a visual object has been placed in the visual buffer, it can be harvested by a production like this one:

```
 production encode-letter{
   goal{
     isa read-letters
     state = attend
   }
   visual{
     isa text
     value = =letter
   }
 }{
   goal{
     state = respond
   }
   +imaginal{
     isa array
     letter = =letter
   }
 }
```

which makes a request to the imaginal buffer to create a new chunk which holds a representation of the letter as was described in the section on the imaginal module.


## Learning New Chunks
This process of seeking the location of an object in one production, switching attention to the object in a second production, and harvesting the object in a third production is a common style in ACT-R models.  One important thing to appreciate is that this is one way in which ACT-R can acquire new declarative chunks. Initially the chunks will be in the perceptual buffers, but they will be stored in declarative memory as a permanent chunk encoding what has been perceived in that location once they leave the buffers.  That process occurs for all buffers – whenever a chunk is cleared from a buffer it becomes part of the model’s declarative memory.  Thus this is also happening for the imaginal buffer’s chunks. When the module creates a new internal representation using the imaginal buffer any chunk which was previously in the buffer will also become part of the models declarative memory.

## The Motor Module
When we speak of motor actions in ACT-R we are mostly concerned with hand movements.  It is possible to extend the motor module to other modes of action, but the default mechanism is built around controlling a pair of hands.  In this unit we will only be concerned with finger presses at a keyboard, but the fingers can also be used to press other devices and the hands can also be used to move a mouse or other device.  

The buffer for interacting with the motor module is called the motor buffer.  Unlike other buffers however, the motor buffer never holds a chunk.  It is used only to issue commands and to query the state of the motor module.  The motor buffer is used to request actions be performed by the hands.  As with the vision module, you should always check to make sure that the motor module is free before making any requests to avoid jamming it.  The motor buffer query to test the state of the module works the same as the one described for the vision module:

```
   ?motor{
     state = free
   }
```

That query will be true when the module is available.

The motor module actually has a more complicated state than just free or busy because there are multiple stages in the motor module, and it is possible to make a new request before the previous one has completed by testing the individual stages.  However we will not be discussing that in the tutorial, and will only test on the overall state i.e. whether the entire module is free or busy.  The respond production from the demo2 model shows the motor buffer in use:

```
 production respond{
   goal{
     isa read-letters
     state = respond
   }
   imaginal{
     isa array
     letter = =letter
   }
   ?motor{
     state = free
   }
 }{
   goal{
    state = done
   }
   +motor{
     isa press-key
     key = =letter
   }
 }
```

This production fires when a letter has been encoded, the goal state is respond and the motor buffer indicates that the motor module is available.  Then a request is made to press the key corresponding to the letter from the letter slot of the chunk in the imaginal buffer and the state is changed to done.  The type of action requested of the hands is specified in the isa slot of the motor buffer request. The press-key request assumes that the hands are located over the home row and the fingers will be returned there after the key has been pressed.  There are many other requests that can be made of the hands, but for now, key presses are all we need and you can find more details in the documentation of the motor module. 

When the production is fired a request is made to press the key, at time 0.485 seconds. However, it takes 250 ms to prepare the features of the movement (preparation-complete), 50 ms to initiate the action, another 100 ms for the key to be struck, and finally it takes another 150 ms for the finger to return to the home row. Thus the time of the key press is at .885 seconds, however the motor module is still busy until time 1.035 seconds.  The press-key request obviously does not model the typing skills of an expert typist, but is a sufficient mechanism for many tasks. 

## Strict Harvesting
It states that if the chunk in a buffer is tested on the LHS of a production (also referred to as harvesting the chunk) and that buffer is not modified on the RHS of the production, then that buffer is automatically cleared. By default, this happens for all buffers except the goal buffer. 

If one wants to keep a chunk in a buffer after a production fires without modifying the chunk then it is valid to specify an empty modification to do so.  For example, if one wanted to keep the chunk in the visual buffer after encode-letter fired we would only need to add an visual{} action to the RHS:

```
 production encode-letter{
   goal{
     isa read-letters
     state = attend
   }
   visual{
     isa text
     value = =letter
   }
 }{
   goal{
     state = respond
   }
   +imaginal{
     isa array
     letter = =letter
   }
   visual{}
 }
```




## Unit 2 Assignment 

Your assignment is to extend the abilities of the model in demo2 to do a more complex experiment.  The new experiment presents three letters.  Two of those letters will be the same.  The participant's task is to press the key that corresponds to the letter that is different from the other two. 

