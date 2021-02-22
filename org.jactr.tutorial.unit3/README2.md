## The Subitizing Task
Your assignment for this unit is to write a model for the subitizing task.  This 
is an experiment where you are presented with a set of marks on the screen (in 
this case X's) and you have to count how many there are.  

If you run the run configuration **Unit 3 - Subitizing Experiment** you will be
run through ten trials in which you will see from 1 to 10 objects on the screen.
  The trials will be in a random order.  You should press the number key that 
corresponds to the number of items on the screen unless there are 10 objects in 
which case you should type 0. 

### The Vocal System
We have already seen that the default ACT-R mechanism for pressing the keys takes 
a considerable amount of time, which would have a serious affect on the results 
of this model.  One solution would be to more explicitly control the hand movements 
to provide faster responses, but that is beyond the scope of this unit.  For this 
task the model will provide a vocal response i.e. it is going to say the number 
of items on the screen.  This is done by making a request of the speech module 
in a manner exactly like the requests to the motor module.  

Here is the production in the Sperling model that presses a key:
```
production do-report {
  goal{
    isa report-row
    row =  =tone
  }
  retrieval{
    isa visual-object
    status =  =tone
    value  =  =val
  }
  ?motor{
    state =  free
  }
}{
  +motor{
    isa press-key
    key =  =val
  }
  +retrieval{
    isa text
    status              =  =tone
    :recently-retrieved != true
  }
}
```
With the following changes it would speak the response instead (note however that
 the sperling experiment is not written to accept a vocal response so it will not
properly score those responses if you attempt to run the model with these 
modifications):
```
production do-report {
  goal{
    isa report-row
    row =  =tone
  }
  retrieval{
    isa visual-object
    status =  =tone
    value  =  =val
  }
  ?vocal{
    state =  free
  }
}{
  +vocal{
    isa speak
    string =  =val
  }
  +retrieval{
    isa text
    status              =  =tone
    :recently-retrieved != true
  }
}
```
The primary change is that instead of the manual buffer we use the vocal buffer.
On the LHS we query the vocal buffer to make sure that the speech module is not 
currently in use:
```
?vocal{
    state =  free
  }
```
The default timing for speech acts is .200 seconds per assumed syllable based on
 the length of the string to speak.  That value works well for this assignment so
we will not go into the details of adjusting it.
  
### Exhaustively Searching the Visual Icon
When the model is doing this task it will need to exhaustively search the display.  
It can use the ability of the visual system to tag those elements that have been 
attended and not go back to them -- just as in the Sperling task.  To make the 
assignment easier, the number of finsts has been set to 10 in the starting model.  
Thus, your model only needs to use the :attended specification in the visual-location 
requests.  However, the model also has to be able to detect when there are no 
more unattended visual locations.  This will be signaled by an error when a request 
is made of the visual-location buffer that cannot be satisfied. This is the same 
as when the retrieval buffer reports an error when no chunk that matches the 
request can be retrieved.  The way for a production to test for that would be to 
have the following test on the left-hand side:
```
production respond {
 ...
 ?visual-location{
  state = error
 } 
}{
 ...
}

```
When no location can be found to satisfy a request of the visual-location buffer 
it will report a state of error.

### The Assignment
Your task is to write a model for the subitizing task that always responds correctly,
 and does an approximate job of reproducing the human data.  You are provided with 
chunks that encode numbers and their ordering from 0 to 10. They also contain a slot 
called value that holds the string of the number to be spoken. The chunk-type provided 
for the goal chunk has a slot to maintain the current count and a slot to hold the 
current model state, and an initial goal chunk which has a state slot value of 
start is also set initially.  As with the demonstration model for this unit, you 
may use only the goal buffer for holding the task information instead of splitting 
the representation between the goal and imaginal buffers.  As always however, the 
provided chunk-types and chunks are only a recommended starting point and one is 
free to use other representations and control mechanisms.


 