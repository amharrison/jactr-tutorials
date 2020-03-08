# Unit 1: Understanding Production Systems

## The ACT-R Production System

ACT-R is a production system theory that tries to explain human cognition by developing a model of the knowledge structures that underlie cognition.  There are two types of knowledge representation in ACT-R -- *declarative* knowledge and *procedural* knowledge. Declarative knowledge corresponds to things we are aware we know and can usually describe to others.  Examples of declarative knowledge include “George Washington was the first president of the United States” and “An atom is like the solar system”. Procedural knowledge is knowledge which we display in our behavior but which we are not conscious of.  For instance, no one can describe the rules by which we speak a language and yet we do.  In ACT-R declarative knowledge is represented in structures called chunks whereas procedural knowledge is represented as rules called productions.  Thus chunks and productions are the basic building blocks of an ACT-R model.

The function of Unit 1 is to present the formal notation for specifying chunks and productions and to describe how the two types of knowledge interact to produce cognition.  You will get some directed practice in interpreting and writing production systems. This tutorial will cover three models:

- [Count](#count-model)
- [Addition](#addition-model)
- [Semantic](#semantic-model)
- [Tutor](#tutor-model)

### Chunks in ACT-R 

In ACT-R, elements of declarative knowledge are called chunks.  Chunks represent knowledge that a person might be expected to have when they solve a problem.  A chunk is defined by its type and its slots.   One can think of chunk-types as categories (e.g., birds) and slots as category attributes (e.g., color or size).   A chunk also has a name which can be used to reference it, but the name is not considered to be a part of the chunk itself.  Below are some representations of chunks that encode the facts that the dog chased the cat and that 4+3=7.  The chunks are displayed as a name and then slot and value pairs.  The type of the first chunk is chase and its slots are agent and object. The isa slot is special and specifies the type of the chunk.  The type of the second chunk is addition-fact and its slots are addend1, addend2, and sum.

```
Action023
    isa chase
    agent dog
    object cat


Fact3+4
    isa addition-fact
    addend1 three
    addend2 four
    sum seven
```

### Productions in ACT-R 
A production rule is a statement of a particular contingency that controls behavior.  Examples might be

```
IF the goal is to classify a person
    and he is unmarried
THEN classify him as a bachelor

IF the goal is to add two digits d1 and d2 in a column
    and d1 + d2 = d3
THEN set as a subgoal to write d3 in the column
```

The condition of a production rule (the IF part) consists of a specification of the chunks in various buffers.  The action of a production rule (the THEN part) consists of modifications of the chunks in the buffers, requests for other chunks to be placed into the buffers, or requests for other actions to be taken.  The above are informal English specifications of production rules.  They give an overview of what the production does in the context of the chunks in the buffers, but do not necessarily detail everything that needs to happen within the production. You will learn the syntax for precise production specification within the ACT-R system.

## Creating Knowledge Elements
To create chunks, chunk types, and productions one must issue the necessary jACT-R commands. 

### Creating New Chunk Types
To create a new type of chunk like “bird” or “addition fact”, you need to specify a frame for the chunk using the chunktype command.  This requires that you specify the name of the chunk type and the names of the slots that it will have.  The general chunk type specification looks like this:

```
chunktype add {
  arg1  = null
  arg2  = null
  count = null
  sum   = null
}
```
Here we define a chunktype *add* with four slots, each with a default value of *null*. Any chunk reference can be a default value for a chunktype.

### Creating Chunks
Once a chuntype is defined, you can specify instances and slot values for the knowledge they represent.

```
count-order aa (first=0.0, second=1.0), bb (first=1.0, second=2.0), cc (first=2.0, second=3.0),
  dd (first=3.0, second=4.0), ee (first=4.0, second=5.0), ff (first=5.0, second=6.0), gg (first=6.0, second=7.0),
  hh (first=7.0, second=8.0), ii (first=8.0, second=9.0), jj (first=9.0, second=10.0)

```
Here we define ten chunks aa..jj (jACT-R doesn't permit single letter identifiers). Each one defines a fact linking two numbers in sequence. Every chunk defined in this way is automatically added to declarative memory (at time 0), assuming the chunk is not the contents of a buffer.

### Production Rules
A production rule is a condition-action pair. The condition (also known as the left-hand side or LHS) specifies a pattern of chunks that must be present in the buffers for the production rule to apply. The action (right-hand side or RHS) specifies some actions to be taken when the production fires. 

### Buffers
Before continuing with productions we need to describe what these buffers are.  The buffers are the interface between the procedural memory system and the other components (called modules) of the ACT-R architecture. For instance, the goal buffer is the interface to the goal module.  Each buffer can hold one chunk at a time, and the actions of a production affect the contents of the buffers. Essentially, buffers operate like scratch-pads for creating and modifying chunks. 

In this chapter we will only be concerned with two buffers -- one for holding the current goal and one for holding information retrieved from the declarative memory module.  Later chapters will introduce other buffers and modules as well as further clarify the operations of the *goal* and *retrieval* buffers used here.

### Production Rules Continued
The general form of a production rule is:

```
production name {
  //lhs tests
}
{
  //rhs requests
}
```

Each production must have a unique name. The buffer tests consist of a set of patterns to match against the current buffers’ contents.  If all of the patterns correctly match, then the production is said to match and it can be selected. It is possible for more than one production to match the current buffer contents at any time. Among all the matching productions only one will be selected to fire and that production’s actions will be performed.  The process of choosing a production from those that match is call conflict resolution, and it will be discussed in detail in later units.  For now, what is important is that only one production may fire at a time.  After a production fires, matching and conflict resolution will again be performed and that will continue until the model has finished.  

## Production Rule Specification
In separate subsections to follow we will describe the syntax involved in specifying the condition and the action of a production.  In doing so we will use the following production that counts from one number to the next:

```
production increment {
  goal {
    isa count-from
    count =  =num1
    end   != =num1
  }
  retrieval {
    isa count-order
    first  =  =num1
    second =  =num2
  }
}{
  goal {
    count = =num2
  }
  +retrieval {
    isa count-order
    first =  =num2
  }
}
```
or rather:

IF the goal chunk is of type count, count is assigned to *=num1* so we can ensure that *end* and *count* are not equal, AND we have retrieved a count-order fact where *first* and *count* are the same (via the variable *=num1*), *=num2* is assigned to the value of second. THEN update our internal representation of, effectively incrementing *count*, and try to retrieve the next fact in sequence.

Variables (anything starting with *=*) are an important concept in the ACT-R production system. Variables are used in productions to test for general conditions.  They can be used for two basic purposes. In the condition they can be used to compare the values in different slots, for instance that they have the same value or different values, without needing to know all the possible values those slots could have.  They can also be used to copy values from one slot to another slot in the actions of the production.  The name of the variable can be any symbol and should be chosen to help make the purpose of the production clear.  A variable is only meaningful within a specific production.   The same variable name used in different productions does not have any relation between the two uses.  For a variable test to match there must be a value in the slot.  An empty slot, indicated with the jACT-R symbol *null*, will not match to a variable in a buffer specification.

One final thing to note is that *=goal* and *=retrieval*, as used to specify the buffers, are also variables. They will be bound to the chunk that is in the goal buffer and the chunk that is in the retrieval buffer respectively.  These variables for the chunks in the buffers can be used just like any other variable to test a value in a slot or to place that chunk into a slot as an action. 

### Buffer Requests
If the buffer name is prefixed with "+", then the action is a request to that buffer’s module.  Typically this results in the module replacing the chunk in the buffer with a different one, but could also be a request for the module to make some change to the chunk that is already in the buffer.  Each module has its own purpose and handles different types of requests.  In later units of the tutorial we will describe modules that can handle visual attention and manual control requests along with a few others.
In this unit, we are dealing only with the declarative memory and goal modules.  Requests to the declarative memory module (the module for which the retrieval buffer is the interface) are always a request to retrieve a chunk from the model’s declarative memory that matches the specification provided and to place that chunk into the retrieval buffer if one is found.

Thus this request:

```
  +retrieval {
    isa count-order
    first =  =num2
  }
```

is asking the declarative memory module to retrieve a chunk which is of type count-order and with a first slot that has the same value as =num2.  If such a chunk exists in the declarative memory of the model, then it will be placed into the retrieval buffer.  

### Buffer Clearing
The third type of action that can be performed on a buffer is to explicitly clear the chunk from the buffer.  This is done by placing "-" before the buffer name in the action. 
Thus, this action on the RHS of a production would clear the chunk from the retrieval buffer:

```
 -retrieval{}
```

### Implicit Clearing

In addition to the explicit clearing action one can make, there is also an implicit clearing that will occur for buffers.  A request of a module with a “+” action will automatically cause that buffer to be cleared.  So, this request from the example production:

```
  +retrieval {
    isa count-order
    first =  =num2
  }
```

results in the retrieval buffer being automatically cleared

## jACT-R Models
We will be going through a series of examples to illustrate how a production system works and to introduce you to the ACT-R environment.  All of your work in the ACT-R tutorial will probably involve using some variant of the jACT-R environment.  The environment is a GUI for running, inspecting, and debugging jACT-R models.  It runs within the [Eclipse IDE](http://eclipse.org/) and is available for all platforms.  

- [Count](#count-model) model
- [Addition](#addition-model) model

## Count Model
The first model is a simple production system that counts up from one number to another - for example it will count up from 2 to 4 -- 2,3,4.  It is included with the tutorial project for unit 1.  It is contained in the file called *models/org.jactr.tutorial.unit1/count.jactr*. You should now start ACT-R and the ACT-R environment if you have not done so already and open the count model.

You should now open the count model in a text editor (if you have not already) to begin looking at how the model is specified.  We will be focusing on the specification of the chunks and productions.  The other commands that are used in this unit’s models are described in the unit 1 model description document.

### Chunktypes for the Count Model
In the model file you will find the following two specifications for new chunk types used by this model:

```
chunktype count-order {
  first  = null
  second = null
}

chunktype count-from {
  start = null
  end   = null
  count = null
}
```
The count-order chunk type is used for chunks that encode the ordering of numbers.  The count-from chunk type will be used as the type for the goal chunk of the model and has slots to hold the starting number, the ending number, and the current count so far.  

### Declarative Memory for Count Model
In the model file you will find the initial chunks placed into the declarative memory of the model:

```
count-order bb (first=1.0, second=2.0), cc (first=2.0, second=3.0), dd (first=3.0, second=4.0), ee (first=4.0, second=5.0),
  ff (first=5.0, second=6.0)

count-from first-goal (start=2.0, end=4.0)
```
The first five define the counting facts named bb, cc, dd, ee, and ff (jACT-R doesn't permit one letter identifiers).  They are of the type count-order and each counting fact connects the number lower in the counting order (in slot first) to the number next in the counting order (in slot second).  This is the knowledge that enables the model to count.

The last chunk, first-goal, is of the type count-from and it encodes the goal of counting from 2 (In slot start) to 4 (in slot end).  Note that the chunk-type count-from has another slot called count which is not used when creating the chunk first-goal.  Because the count slot is not stated, it will be empty in the chunk first-goal which means that it will have the default value null. 

### Setting the Initial Goal
The chunk first-goal is declared to be the model’s current goal (placed into the goal buffer) by the buffer specification:

```
buffers {
  goal {first-goal} [
    "Activation"              : "1"
    "StrictHarvestingEnabled" : "false" ]
  ...
}
```

Now that we have seen the chunks the model has we will look at the productions that use those chunks to count.

### The Start Production
The count model has three productions: start, increment, and stop.  The first production that gets selected and fired by the model is the production start. Here is the defintion from the model:

```
production start {
  goal {
    isa count-from
    start =  =num1
    count =  null
  }
}{
  goal {
    count = =num1
  }
  +retrieval {
    isa count-order
    first =  =num1
  } 
}
```
On its LHS it tests the goal buffer.  It tests that there is a value in the start slot which it now references with the variable *=num1*.  This is often referred to as binding the variable, as in *=num1* is bound to the value that is in the start slot.  It also checks that the count slot is currently empty i.e. that it has the value *null*.

On the RHS it performs two actions.  The first is to change the value of the count slot of the chunk in the goal buffer to be the value bound to *=num1*.  The other action is to request that the declarative memory system retrieve a chunk of type count-order that has the value which is bound to *=num1* in its first slot.

### The Increment Production
Here is the specification of the increment production:

```
production increment {
  goal {
    isa count-from
    count =  =num1
    end   != =num1
  }
  retrieval {
    isa count-order
    first  =  =num1
    second =  =num2
  }
}{
  goal {
    count = =num2
  }
  +retrieval {
    isa count-order
    first =  =num2
  }
}
```
On the LHS of this production we see that it tests both the goal and retrieval buffers. In the test of the goal buffer it uses a modifier in the testing of the end slot. The “!=” between the slot and value is the negative test modifier.  It means that this production will only match if the end slot of the chunk in the goal buffer does not have the same value as the start slot. 

The retrieval buffer test checks that it has retrieved the count-order chunk with a value of its first slot that matches the current count slot from the goal buffer chunk and binds the variable =num2 to the value of its second slot.

The first two actions are very similar to the start production.  It updates the count slot of the goal chunk with the next number as found from the count-order chunk in the retrieval buffer and then requests that a count-order chunk be retrieved to get the next number.  

### The Stop Production
The final production in the model is stop:

```
production stop {
  goal {
    isa count-from
    end   =  =num
    count =  =num
  }
}{
  -goal {}
  output ( "Answer =num" ) 
  } 
}
```
The stop production matches when the values of the count and end slots of the chunk in the goal buffer are the same.  The actions it takes are to again print out the current number and now to also clear the chunk from the goal buffer. 

*output* can be used on the RHS of a production to display information in the run trace. 

###  Running Model

In the Eclipse IDE, go to Menu -> **Window** -> **Perspective** -> **Open Perspecitve** -> **jACT-R Run**. This will change the layout of the IDE so it is better suited for viewing model runs. You can change the perspective at anytime by clicking the buttons in the upper-right corner of the IDE.  

Now go to Menu -> **Run ** -> **Run Configurations...** and select *count* under **jACT-R Run**.

<img src="images/runcfg.png" width="66%"/>

This is a run configuration. It is the centralized configuration for running a given model. Everything that is necessary beyond just the model is defined here. You can attach various instruments to poke and prod and model. You can define other simulation entities to participate. You can even define what information is returned to the IDE. For now, just press **Run**.

It may take a few seconds to get started (jACT-R uses aggressive caching so subsequent runs should start much faster) but eventually you'll have a populated log view.

<img src="images/logview.png" width="66%"/>

The log view keeps track of all messages for a set amount of time (configurable in the preferences). Each timestep represents a row of messages. Selecting a row will display the full contents of the log in the lower half of the view. Which rows are displayed is configured in the log view filter menu.

<img src="images/extraview.png" width="50%"/>

To the right of the log view is the conflict resolution and buffer state views. For each timestep (row) you select in the log view, the conflict resolution and buffer contents at that time will be displayed. You can use these views to follow the flow of information in the model. 

You should take the time to walk through the model log time step by time step to familiarize yourself with how the productions were selected and what they changed in the system.

***


# Addition Model
The second example model uses a slightly larger set of count facts to do a somewhat more complicated task.  It will do addition by counting up.  Thus, given the goal to add 2 to 5 it will count 5, 6, 7, and return the answer 7.  You should load the addition model in the same way as you loaded the count model.

The initial count facts are the same as those used for the count model with the inclusion of a fact that encodes 1 follows 0 and those that encode all the numbers up to 10.  The chunk type for the goal now encodes the starting number (arg1) and the number to be added (arg2):

```
chunktype add {
  arg1  = null
  arg2  = null
  count = null
  sum   = null
}
```
There are two other slots in the goal called count and sum which will be used to hold the results of the counting and the total so far as the model progresses.  Here is the initial goal chunk created for the model:

```
add second-goal (arg1=1.0, arg2=9.0)
```

Since the count and sum slots are not specified, they are empty, which is indicated with the default value of *null*.

In this sequence we see that the model alternates between incrementing the count from 0 to 2 and incrementing the sum from 5 to 7. The production Initialize–Addition starts things going and requests a retrieval of an increment to the sum.  Increment-Sum processes that retrieval and requests a retrieval of an increment to the count.  That production fires alternately with Increment-Count, which processes the retrieval of the counter increment and requests a retrieval of an increment to the sum.  Terminate-Addition recognizes when the counter equals the second argument of the addition and modifies the goal to make the model stop.

### The Initialize-Addition and Terminate-Addition Productions
The production Initialize-Addition initializes an addition process whereby the system tries to count up from the first digit a number of times that equals the second digit and the production Terminate-Addition recognizes when this has been completed.

```
production initialize-addition {
  goal {
    isa add
    arg1 =  =num1
    arg2 =  =num2
    sum  =  null
  }
  ?retrieval {
    state =  free
  }
}{
  goal {
    count = 0.0
    sum   = =num1
  }
  +retrieval {
    isa count-order
    first =  =num1
  }
}
```
This production initializes the sum slot to be the first digit and the count slot to be zero.  It requests a retrieval of the number that follows =num1. 


Pairs of productions will apply after this to keep incrementing the sum and the count slots until the count slot equals the arg2 slot, at which time Terminate-Addition applies:

```
production terminate-addition {
  goal {
    isa add
    arg1  =  =num1
    arg2  =  =num2
    count =  =num2
    sum   =  =answer
  }
}{
  goal {
    count = null
  }
  output ( "=num1 + =num2 is =answer" ) 
}

```
This production clears the count slot of the goal by setting it to *null* (remember *null* is the value of an empty slot).  This causes the model to stop because other than Initialize-Addition, (which requires that the sum slot be empty) all the other productions require there to be a chunk in the count slot. So, after this production fires none of the productions will match the chunk in the goal buffer. 

### The Increment-Sum and Increment-Count Productions
The two productions that apply repeatedly between the previous two are Increment-Sum, that harvests the retrieval of the sum increment and requests a retrieval of the count increment, and Increment-Count, that harvests the retrieval of the count increment and requests a retrieval of the sum increment.

```
production increment-sum {
  goal {
    isa add
    sum   =  =sum
    count =  =count
    arg2  != =count
  }
  retrieval {
    isa count-order
    first  =  =sum
    second =  =newSum
  }
}{
  goal {
    sum = =newSum
  }
  +retrieval {
    isa count-order
    first =  =count
  }
} 

production increment-count {
  goal {
    isa add
    sum   =  =sum
    count =  =count
  }
  retrieval {
    isa count-order
    first  =  =count
    second =  =newCount
  }
}{
  goal {
    count = =newCount
  }
  +retrieval {
    isa count-order
    first =  =sum
  }
}
```

### Running
As before, we will be using a run configuration to run the addition model. However, this time we are going to use the debugging mode. Let's switch to the debug perspective first. Click on the box+ icon in the upper right hand corner and select **Debug**

![](images/perspective.png)

Let's dive in. Menu -> **Run** -> **Debug Configurations...**. Select *addition* under **jACT-R Run** and hit **Debug**. After a second, the **Debug** view (on the left hand side of the screen) should look like this:

![](images/debug.png)

What you are seeing is two debug targets, one for the jACTR runtime, the other for the underlying Java code. It is possible to debug both at the same time from the same interface, but for now select the runtime target (**Debug ACTRRuntime**). It is currently in a paused state. Whenever you start a model in debug mode it will always start in a paused state. It is up to you to resume it.

Click the step over (F6) and the model will run for one cycle. You should now see the first production that was fired at time 0.

![](images/stepover.png)
 

If you open **Menu** -> **Window** -> **Show View** -> **Other...**, you can select the jACT-R views to add to debug view. Select the **jACT-R Log** view from under the **jACT-R Modeling** group. Now you can see the log file advance as you step through the model. If you also add the **Buffer** and **Conflict Resolution** views, you can see all the information necessary to debug the model.

Back in the debug view, you can select any production to jump to it in the editor. If you select the latest production (bottom), you can also see the current instantiation in the **Variables** view on the right.

Keep stepping through the model until the end.



# Semantic Model
The last example for this unit is the semantic model. It contains chunks which encode the following network of categories and properties.  It is capable of searching this network to make decisions about whether one category is a member of another category.

![](images/graph.png)

### Encoding of the Semantic Network
All of the links in this network are encoded by chunks of type property with the slots object, attribute, and value.  For instance, the following three chunks encode the links involving shark:

```
property p1 (object=shark, attribute=dangerous, value=true), p2 (object=shark, attribute=locomotion, value=swimming), p3 (object=shark, attribute=category, value=fish)
```

p1 encodes that a shark is dangerous by encoding a true value on the dangerous attribute.  p2 encodes that a shark can swim by encoding the value swimming on the locomotion attribute.  p3 encodes that a shark is a fish by encoding fish as the value on the category attribute.

You can inspect the chunks of property in the model to see how the rest of the semantic network is encoded.

### Queries about Category Membership
Queries about category membership are encoded by goals of the is-member type.  There are 3 goals provided in the initial chunks for the model.  The one initially placed in the goal buffer is g1:

```
is-member g1 (object=canary, category=bird)
```

which represents the query to decide if a canary is a bird.  The judgment slot is null reflecting the fact that the decision has yet to be made about whether it is true.  If you run the **semantic** run configuration, you should see the result.


This is among the simplest cases possible and involves only the retrieval of this property

```
property p14(object=canary, attribute=category, value=bird)
```

for verification of the query. There are two productions involved.  The first, Initial-Retrieve, requests the retrieval of categorical information and the second, Direct-Verify, harvests that information and sets the judgment slot to yes:

```
production initial-retrieve {
  goal {
    isa is-member
    object    =  =obj
    category  =  =cat
    judgement =  null
  }
  ?retrieval {
    state =  free
  }
}{
  goal {
    judgement = pending
  }
  +retrieval {
    isa property
    object    =  =obj
    attribute =  category
  }
  output ( "trying to remember something about =obj " ) 
 }
 
production direct-verify {
  goal {
    isa is-member
    object    =  =obj
    category  =  =cat
    judgement =  pending
  }
  retrieval {
    isa property
    object    =  =obj
    attribute =  category
    value     =  =cat
  }
}{
  goal {
    judgement = yes
  }
  output ( "Yes, a =obj is a =cat" )
  -retrieval {} 
}
```

### Chaining Through Category Links

A slightly more complex case occurs when the category is not an immediate super ordinate of the queried object and it is necessary to chain through an intermediate category.  An example where this is necessary is in the verification of whether a canary is an animal and such a query is defined in chunk g2:

```
g2 (object=canary, category=animal)
```

Change the goal of the **goal** buffer in the **buffers** section near the top of the model to *g2*. And then rerun the model. This involves an extra production, Chain-Category, which retrieves the category in the case that an attribute has been retrieved which does not allow a decision to be made.  Here is that production:

```
production chain-category {
  goal {
    isa is-member
    object    =  =obj
    category  =  =cat
    judgement =  pending
  }
  retrieval {
    isa property
    object    =  =obj
    attribute =  category
    value     =  =val
    value     != =cat
  }
  ?retrieval {
    state =  free
  }
}{
  goal {
    object = =val
  }
  +retrieval {
    isa property
    object    =  =val
    attribute =  category
  }
  output ( "=obj is a =val I wonder if =val is a =cat" )
  output ( "Im trying to remember something about =val" ) 
 }
```

To see this execute, caret to the *chain-category* production and hit Ctrl-B (Command-B). This will set a breakpoint on the production (assuming we are still in **Debug** perspective). If you *debug* the model, after you resume (F8) the first time, the model will run until *chain-category* then suspend. You should play around with breakpoint productions at your leisure.

###  The Failure Case

No change the goal to *g3*:

```
g3 (object=canary, category=fish)
```

If you *run* the model, you will see what happens when the it reaches a deadend.

The production Fail applies and fires when a retrieval attempt fails.  This production uses a condition on the LHS that we have not yet seen.

### Query Conditions
In addition to testing the chunks in the buffers as has been done in all the productions to this point, it is also possible to query the state of the buffer or the module which controls it.  This is done using the “?” operator before the name of the buffer.  A module may have a number of different queries to which it will respond, but there are some to which all buffers and modules will always respond.  The queries that are always valid are whether there is a chunk in the buffer or not, whether or not that chunk was requested by the procedural module, and for one of three possible states of the module: free, busy, or error.  The queries are either true or false.  If any query is false, then the production does not match.  Here are examples of the possible queries with respect to the retrieval buffer. 

This query will be true if there is any chunk in the retrieval buffer:

``` 
  ?retrieval{
      buffer = full
      }
```
This query will be true if there is not a chunk in the retrieval buffer:

```
   ?retrieval{
       buffer = empty
       }
```


This query will be true if the retrieval buffer’s module (the declarative memory module) is not currently working to retrieve a chunk:


```
   ?retrieval{
      state   = free
      }
```

This query will be true if the declarative memory module is working on retrieving a chunk:

```
   ?retrieval{
      state    =  busy
      }
```

This query will be true if there was an error in the last request made to the retrieval buffer:

```
   ?retrieval{
      state   =   error
      }
```

For retrieval requests, that means that no chunk could be found that matched the request.

It is also possible to make multiple queries at the same time.  For instance this query would check if the module was not currently handling a request and that there was currently a chunk in the buffer:

```
   ?retrieval{
      state   =   free
      buffer  =   full
      }
```

One can also use the optional negation modifier “-” before a query to test that such a condition is not true.  Thus, either of these tests would be true if the declarative module was not currently retrieving a chunk:

```
   ?retrieval{
      state   =    free
      }
or
   ?retrieval{
     state  !=   busy
     }
```


### The Fail production 
Now, here is the production that fires in response to a category request not being found

```
production fail {
  goal {
    isa is-member
    object    =  =obj
    category  =  =cat
    judgement =  pending
  }
  ?retrieval {
    state =  error
  }
}{
  goal {
    judgement = no
  }
  output ( "No, a =obj is not a =cat" )
}
```

Note the testing for a retrieval failure in the condition of this production.  When a retrieval request does not succeed, in this case because there is no chunk in declarative memory that matches the specification requested, the buffer’s state indicates an error. In this model, this will happen when one gets to the top of a category hierarchy and there are no super ordinate categories.


# Tutor Model

We would like you to now construct the pieces of an jACT-R model on your own. The *tutorModel* file included with the tutorial models contains the basic code necessary for a model, but does not have any of the declarative or procedural elements defined. The instructions that follow will guide you through the creation of those components.  You will be constructing a model that can perform addition of two two-digit numbers.  Once all of the pieces have been added as described, you should be able to load and run the model.

You should now open the *tutorModel* file in a text editor if you have not already.  The following sections will describe the components that you should add to the file in the places indicated by comments in the model.


## Chunktypes

The first thing we need to do is define the chunk types that will be used by the model.  There are two chunk-types which we will define for doing this task.  One to represent addition facts and one to represent the goal chunk which holds the components of the task for the model.  These chunk types will be created with the chunktype command as described in earlier.

### Addition Facts

The first chunk type you will need is one to represent the addition facts.  It should be named addition-fact and have slots named addend1, addend2, and sum.

### The Goal Chunk Type

The other chunk type you will need is one to represent the goal of adding two two-digit numbers.  It should be named add-pair. It must have slots to encode all of the necessary components of the task.  It should have two slots to represent the ones digit and the tens digit of the first number called one1 and ten1 respectively.  It will have two more slots to hold the ones digit and the tens digit of the second number called one2 and ten2, and two slots to hold the answer, called one-ans and ten-ans. It will also need a slot to hold any information necessary to process a carry from the addition in the ones column to the tens column which should be called carry.

After you have created those three chunk-types you have completed the chunk type portion of this model. Now it is time to create the chunks that allow the model to perform addition along with an initial goal to do such an addition.

## Chunks
We are now going to define the chunks that will allow the model to solve the problem 36 + 47.

### The Addition Facts

You need to add the addition facts to encode the following math facts:

3+4=7
6+7=13
10+3=13
1+7=8

They will be of type addition-fact and should be named based on their addends.  For example, the fact that 3+4=7 should be named fact34.  The addends and sums for these facts will be the appropriate numbers

### The Initial Goal

You should now create a chunk called goal which encodes that the goal to add 36+47.  This should be done by specifying the values for the ones and tens digits of the two numbers and leaving all of the other slots empty.
 
 
## Productions
 
So far, we have been looking mainly at the individual production rules in the models.  However, production systems get their power through the interaction of the production rules.  Essentially, one production will set the condition for another production rule to fire, and it is the sequence of productions firing that lead to performing the task.

Your task is to write the ACT-R equivalents of the production rules described below in English, which can perform multi-column addition.

Here are the English descriptions of the six productions needed for this task.

```
START-PAIR
IF the goal is to add a pair of numbers 
   and the ones digits of the pair are available
   but the ones digit of the answer is nil
THEN note in the one-ans slot that you are busy computing 
      the answer for the ones digit
   and request a retrieval of the sum of the ones digits. 

ADD-ONES
IF the goal is to add a pair of numbers 
   and you are busy waiting for the answer for the ones digit
   and the sum of the ones digits has been retrieved
THEN store the sum as the ones answer
   and note that you are busy checking the answer for the carry
   and request a retrieval to determine if the sum equals 10
     plus a remainder.

PROCESS-CARRY
IF the goal is to add a pair of numbers
   and the tens digits are available
   and you are busy working on the carry
   and the one-ans equals a value which a retrieval
    finds is the sum of 10 plus a remainder
THEN make the ones answer the remainder 
   and note that the carry is 1
   and note you are busy computing the sum of the tens digits
   and request a retrieval of the sum of the tens digits.


NO-CARRY
IF the goal is to add a pair of numbers
   and the tens digits are available
   and you are busy working on the carry
   and the one-ans equals a sum
   and there has been a retrieval failure
THEN note that the carry is nil
   and note you are busy computing the sum of the tens digits
   and request a retrieval of the sum of the tens digits.

ADD-TENS-DONE
IF the goal is to add a pair of numbers
   and you are busy computing the sum of the tens digits
   and the carry is nil
   and the sum of the tens digits has been retrieved
THEN note the sum of the tens digits.


ADD-TENS-CARRY
IF the goal is to add a pair of numbers
   and the tens digits are available
   and you are busy computing the sum of the tens digits
   and the carry is 1
   and the sum of the tens digits has been retrieved
THEN set the carry to nil
   and request a retrieval of one plus that sum.

```

When you are finished entering the productions, save your model and we can create a run configuration for it.

Open **Menu** -> **Run** --> **Run Configurations...** and select any of the prior run configurations. Press the **Duplicate** button (to the left of the red-X delete button). Change the name of the configuration to "TwoColumnAddition", then unselect the existing model and select *tutorialModel* instead. **Apply** and **Run** to see how you did.

Now it's time to put the skills you've learned into practice. Use the **Debug** view in conjunction with the **Log**, **Buffer**, and **Conflict Resolution** views to figure out why your model didn't quite work.  If you find yourself stumped, the model *solution.jactr* contains a fully functioning solution.




***
Based on the original CC licensed [ACT-R tutorials](http://act-r.psy.cmu.edu/software/), 2.25.20.
<br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>.

