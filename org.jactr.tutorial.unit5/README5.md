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
