# Unit 3: Attention

This unit is concerned with developing a better understanding of how perceptual attention 
works in ACT-R, particularly as it is concerned with visual attent

## Visual Locations
When a visual display such as 

![display](images/display.png)

is presented to ACT-R a representation of all the visual information is immediately 
accessible in a visual icon.

```
 fill in image
```

### Visual Location Requests

When requesting the visual location of an object there are many slots that can be 
specified in the request.  In the last unit we only used the request parameter :attended. 
We will expand on the use of :attended in this unit.  In addition, we will also provide 
more information about requests on all of the slots of the visual-location chunk-type 
and show another request parameter which can be specified - :neares

### The Attended Test in More Detail

The :attended request parameter was introduced in unit 2.  It tests whether or not 
the model has attended the object at that location, and the possible values are new, 
false (or null), and true.  Very often we use the fact that attention tags elements 
in the visual display as attended or not to enable us to draw attention to the previously 
unattended elements.  Consider the following product

```
production find-random-letter{
  goal{
   isa   read-letters
   state = find
  }
}{
 +visual-location{
   isa visual-location
   :attended = false
 }
 goal{
   state = attending
 }
}

```
In its action, this production requests the location of an object that has not yet 
been attended (:attended false).  Otherwise, it places no preference on the location 
to be selected. When there is more than one item in the visicon that matches the 
request, the newest (most recent) one will be chosen. If multiple items also match 
on their recency, then one will be picked randomly. If there are no objects which 
meet the constraints, then the error state will be set for the visual-location buffer. 
After a feature is attended (with a +visual request), it will be tagged as attended 
true and this production’s request for a visual-location will not return the location 
of such an obj

#### Finsts

There is a limit to the number of objects which can be tagged as attended, and there 
is also a time limit on how long an item will remain marked as attended true.  These 
attentional markers are called finsts (INSTantiation FINgers) and are based on the 
work of Zenon Pylyshyn.  The number of finsts and the length of time that they persist 
can be set with the parameters NumberOfFINSTs and FINSTDurationTime respectively 
in the visual modul

The default number of finsts is four, and the default decay time is three seconds. 
Thus, with these default settings, at any time there can be no more than four objects 
marked as attended true, and after three seconds the attended state of an item will 
revert from true to false.  Also, when attention is shifted to an item that would 
require more finsts than there are available the oldest one is reused for the new 
item i.e. if there are four items marked with finsts as attended true and you move 
attention to a fifth item the first item that had been marked as attended true will 
now be marked as attended false and the fifth item will be marked as attended true. 
Because the default value is small, productions like the one above are not very useful 
for modeling tasks with a large number of items on the screen because the model will 
end up revisiting items very quickly.  One solution is to always set NumberOfFINSTS 
to a value that works for your task, but one of the goals of ACT-R modeling is to 
produce parameter free models, so a different approach is generally desired.  After 
discussing some of the other specifications one can use in a request we will come 
back to how one could do such thin

### Visual-location slots 

Because the vision module was designed around interacting with a 2-D screen the primary 
slots for visual-locations are screen-x and screen-y.  Named to be consistent with 
canonical Lisp implementation, they DO NOT represent the location based on its x 
and y position on the screen, but rather visual angles measured in degrees from the 
center of the screen.  We will only be working with models that are interacting with 
a 2-D screen. The center is (0,0), increasing positively up and to the right (e.g. 
lower left corner is (-x, -y

There is also a distance slot in the visual-location chunk-type.  This represents 
the distance from the model to the location in cm. Though this value is generally 
ignored by most mode

The height and width slots hold the dimensions of the item measured in visual degrees. 
The size slot holds the approximate area covered by the item measured in degrees 
of visual angle squared.  These values provide the general shape and size of the 
item on the displa

The color slot holds a representation of the color of the item.  Typically, these 
will be symbolic descriptors like black or red which are chunk

The kind and value slots provide a general description of the item, but typically 
not the specific information needed to fully describe the item.  To get specific 
information the model will require shifting attention to the item.  The kind slot 
usually specifies the chunk-type of the object that will be found when the visual-location 
is attended.  The value slot holds some description which is assumed to be available 
without attending.


### Visual-location request specification

One can specify constraints for a visual-location request based on the values of 
the slots in the visual-location chunk-type.  Any of the slots may be specified using 
any of the modifiers (=, !=, <, >, <=, or >=) in much the same way one specifies 
a retrieval request.  Each of the slots may be specified any number of times. In 
addition, there are some special tests which one can use that will be described below. 
All of the constraints specified will be used to find a visual-location in the visicon 
to be placed into the visual-location buffer.  If there is no visual-location in 
the visicon which satisfies all of the constraints then the visual-location buffer 
will indicate an error stat

#### Exact values

If you know the exact values for the slots you are interested in then you can specify 
those values directl

```
+visual-location{
   isa      visual-location
   screen-x = 1.5
   screen-y = 2.7
   color    = black
 }
```

You can also use the negation test, !=, with the values to indicate that you want 
a location which does not have that val
```
+visual-location{
   isa      visual-location
   color  =  black
   kind  !=  text
}

```

Often however, one does not know the specific information about the location in the 
model and needs to specify things more generally. In fact, it is preferred that you 
not use exact values as that makes very specific assumptions about the environment, 
assumptions that are almost certainly wron

#### General values

When the slot being tested holds a number it is also possible to use the slot modifiers 
!=, <, <=, >, and >= along with specifying the value.  Thus to request a location 
that is to the right of screen-x 1.5 and at or above screen-y 2.7 one could use the 
reque

```
+visual-location{
    isa      visual-location
    screen-x > 1.5
    screen-y >= 2.7
}

```

In fact, one could use two modifiers for each of the slots to restrict a request 
to a specific range of values.  For instance to request an object which was located 
somewhere within a box bounded by the corners (-1,1) and (1,2) one could speci
```
+visual-location{
   isa      visual-location
   screen-x > -1
   screen-x < 1
   screen-y > 1
   screen-y < 2
}
```

#### Production variables 

It is also possible to use variables from the production in the requests instead 
of specific values. Consider this production which uses a value from a slot in the 
goal to test the col

```
production find-by-color{
   goal{
    isa    find-color
    target = =color
   }
}{
  +visual-location{
    isa    visual-location
    color  = =color
  }
}

```
Variables from the production can be used just like specific values including with 
the use of the modifiers.  Assuming that the LHS of the production binds =x, =y, 
and =kind this would be a valid reque
```
  +visual-location{
    isa    visual-location
    kind     = =kind 
    screen-x < =x
    screen-x != 0
    screen-y >= =y
    screen-y < 400
}
```

#### Relative values

If you are not concerned with any specific values, but care more about relative settings 
then there are also ways to specify tha

You can use the values lowest and highest in the specification of any slot which 
has a numeric value.  Of the chunks which match the other constraints the one with 
the numerically lowest or highest value for that slot will then be the one foun

In terms of screen-x and screen-y, remember that x coordinates increase from left 
to right, so lowest corresponds to leftmost and highest rightmost, while y coordinates 
increase from top to bottom, so lowest means topmost and highest means bottommos

If this is used in combination with :attended it can allow the model to find things 
on the screen in an ordered manner.  For instance, to read the screen from left to 
right you could u

```
+visual-location{
   isa      visual-location
   screen-x  = lowest
   :attended = false
}

```
assuming that you also move attention to the items so that they become attended and 
that the model has sufficient finsts to tag everythin

There is one note about using lowest and highest when more than one slot is specified 
in that way for examp

```
+visual-location{
   isa      visual-location
   width    = highest
   screen-x = lowest
   color    = red
}

```

Visual location slots are tested in the order that they are specified. In this example, 
we'd select all the visual locations with the highest width, then reduce that set 
by screen-x, then finally select based on color. That may not produce the same result 
as this request for the same set of visicon chun

```
+visual-location{
   isa      visual-location
   screen-x = lowest
   width    = highest
   color    = red
}
```
This request will start with the screen-x candidates, filtered by width, then color

#### The current value

It is also possible to use the special value current in a request.  That means the 
value of the slot must be the same as the value for the location of the currently 
attended object (the one attention was last shifted to with a attend-to request). 
This request would find a location which had the same screen-x value as the current 
on

```
+visual-location{
   isa      visual-location
   screen-x = current
}
```
You can also use the value current with the modifiers. The following test will find 
a location which is up and to the right of the currently attended object in a different 
colo
```
+visual-location{
   isa      visual-location
   screen-x > current
   screen-y < current
   color   != current
}
```
If the model does not have a currently attended object (it has not yet attended to 
anything) then the tests for current are assumed to use center of gaze (0,0)

### The :nearest request parameter

Like :attended, there is another request parameter available in visual-location requests. 
The :nearest request parameter can be used to find the items closest to the currently 
attended location, or some other location.  To find the location of the object nearest 
to the currently attended location we can again use the value curren
```
+visual-location{
   isa     visual-location
   :nearest = current
}
```
It is also possible to specify any location chunk for the nearest test, and the location 
of the object nearest to that location will be retur
```
+visual-location{
   isa      visual-location
   :nearest  = =some-location
}
```
If there are constraints other than nearest specified then they are all tested first. 
The nearest of the locations that matches all of the other constraints is the one 
that will be placed into the buffer. Specifically, the nearest is determined by the 
straight line distance using only the screen-x and screen-y coordinate


### Ordered Search

Above it was noted that a production using this visual-location request (in conjunction 
with appropriate attention shifts) could be used to read words on the screen from 
left to righ

```
production read-next-word{
   goal{
      isa      read-word
      state  = find
   }
}{
   +visual-location{
      isa      visual-location
      :attended = false
      screen-x  = lowest
   }
   goal{
      state   = attend
   }
}
```
However, if there are fewer finsts available than words to be read that production 
will result in a loop that reads only one more word than there are finsts.  For instance, 
if there are six words on the line and the model only has four finsts (the default) 
then when it attends the fifth word the finst on the first word will be removed to 
use because it is the oldest.  Then the sixth request will result in finding the 
location of the first word again because it is no longer marked as attended.  If 
it is attended it will get the finst from the second word, and so 

By using the special tests for current and lowest one could have the model perform 
the search from left to right without using the :attended true.

```
production read-next-word{
   =goal>
      isa       read-word
      state   = find
}{
   +visual-location{
      isa      visual-location
      screen-x > current
      screen-x = lowest
   }
   goal{
      state   = attend
   }
}
```
That will always be able to find the next word to the right of the currently attended 
one.  Similarly, one could add tests for the screen-y coordinate to produce a top-to-bottom 
and left-to-right search pattern or combine that with the :nearest request parameter 
to perform other ordered search strategies.

## The Sperling Task

 

![Display](images/display.png)
