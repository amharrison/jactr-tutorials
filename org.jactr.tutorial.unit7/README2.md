## Past-tense Learning
Your assignment is to make a model that learns the past tense of verbs in English. The learning process of the English past tense is characterized by the so-called U-shaped learning with irregular verbs. That is, at a certain age children inflect irregular verbs like “to break” correctly, so they say “broke” if they want to use the past tense. But at a later age, they overgeneralize, and start saying “breaked”, and then at an even later stage they again inflect irregular verbs correctly. Some people, such as Pinker and Marcus, interpret this as evidence that a rule is learned to create a regular past tense (add “ed” to the stem). According to Pinker and Marcus, after this rule has been learned, it is overgeneralized so that it will also produce regularized versions of irregular verbs.

The start of a model to learn the past tense of verbs is included with the unit, [past.jactr]() under the **org.jactr.tutorial.unit7.past** package. The assignment is to make the model learn a production which represents the regular rule for making the past tense and also specific productions for producing the past tense of each verb. So eventually it should learn productions which act essentially like this:

```
IF the goal is to make the past tense of a verb 
THEN copy that verb and add –ed

IF the goal is to make the past tense of the verb have 
THEN the past tense is had
```

The task is controlled by the [PastTenseExtension](), which is installed using the ```extension``` definition:
```
extension "org.jactr.tutorial.unit7.past.PastTenseExtension"[
  "MaximumTrials"  : "7500"
  "ReportInterval" : "100"]
```

Installed into the model like this, the extension is able to access to model's internals. It does two things. It adds correct past tenses to declarative memory, reflecting the fact that a child hears and then encodes correct past tenses from others. It also creates goals which indicate to the model that it should generate the past tense of a verb found in the imaginal buffer and then runs the model to do so. The model will be given two correct past tenses for every one that it must generate.

The past tense of verbs are encoded in chunks using the slots of this chunk-type:
```
chunktype past-tense {
  verb   = null
  stem   = null
  suffix = null
}
```
Where the verb slot holds the base form of the verb and the combination of the stem and suffix slots forms the past tense, with the value blank in the suffix slot meaning that there is no suffix to add (which is used instead of not specifying the slot so that one can distinguish a complete past tense from one that is malformed). Here are examples of correctly formed past tenses for the irregular verb have and the regular verb use:
```
PAST-TENSE1
     verb have
     stem had
     suffix blank
PAST-TENSE234
     verb use
     stem use
     suffix ed
```

To indicate to the model that it should create a past tense the chunk in the goal buffer will have a state slot with a value of start and the chunk in the imaginal buffer will contain a chunk which has a verb slot with the base form of a verb. Here is what those buffers’ contents would look like at the start of a run to form the past tense of the verb work:
```
GOAL: STARTING-GOAL-0 [STARTING-GOAL]
STARTING-GOAL-0
  STATE START
IMAGINAL: CHUNK2-0 [CHUNK2]
CHUNK2-0
  VERB WORK
```
The model then has to fill in the stem and suffix slots of the chunk in the imaginal buffer to indicate the past tense form of the verb and set the state slot of the chunk in the goal buffer to done to indicate that it is finished. Once the state slot is set to done, one of the three productions provided with the model should fire to simulate the final encoding and “use” of the word, each of which has a different reward. There are three possible cases:

* An irregular inflection, this is when there is a value in the stem slot and the suffix is marked explicitly as blank. This use has the highest reward, because irregular verbs tend to be short.
* A regular inflection, in which the stem slot is the same as the verb slot and the suffix slot has a value which is not blank. This has a slightly lower reward.
* Non-inflected when neither the stem nor suffix slots are set in the chunk. The non- inflection case applies when the model cannot come up with a past tense at all, either because it has no example to retrieve, no production to create it, or no strategy to come up with anything based on a retrieved past tense. The non- inflection situation receives the lowest reward because the past tense would have to be indicated by some other method, for example by adding “yesterday” or some other explicit reference to time.

An important thing to notice is that all three of those situations receive a reward. The model receives no feedback as to whether the past tenses it produces are correct – any validly formed result is considered a success and rewarded. The only feedback it receives with respect to the correct form of past tenses is the correctly constructed verbs that it hears between the attempts to generate its own.

You can run the model using the **Unit 7 - Past Tense** run configuration. It is already has probes configured to track the performance of the model.

![u-shape](images/u-shape.png) 






***
Based on the original CC licensed [ACT-R tutorials](http://act-r.psy.cmu.edu/software/), 2.25.20.

<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>.

