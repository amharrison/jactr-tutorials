## Partial Matching
Up to now models have either always retrieved a chunk which matched the retrieval request or resulted in a failure to retrieve anything. Now we will look at modeling errors in recall in more detail. There are two kinds of errors that can occur. One is an error of commission when the wrong item is recalled. This will occur when the activation of the wrong chunk is greater than the activation of the correct chunk. The second is an error of omission when nothing is recalled. This will occur when no chunk has activation above the retrieval threshold.

We will continue to look at productions from the fan model for now. In particular, this production requests the retrieval of a chunk:
```
production retrieve-from-person{
  imaginal{
    isa comprehend-sentence
    arg1 = =person
    arg2 = =location
  }
  ?retrieval{
    state = free
    buffer = empty
  }
}{
  imaginal{}
  +retrieval{
    isa comprehend-sentence
    arg1 = =person
    relation != null
  }
}
```
In this case an attempt is being made to retrieve a chunk with a particular person (the value bound to =person) that had been studied. If =person were the chunk giant, this retrieval request wouldbelookingforachunkwithgiantinthearg1slot. As was shown above, there were three
chunks in the model from the study set which matched that request and one of those was retrieved.

However, let us consider the case where there had been no study sentences with the person giant but there had been a sentence with the person titan in the location being probed with giant i.e. there was a study sentence “The titan is in the bank” and the test sentence is now “The giant is in the bank”. In this situation one might expect that some human participants might incorrectly classify the probe sentence as one that was studied because of the similarity between the words giant and titan. The current model however could not make such an error.

Producing errors like that requires the use of the partial matching mechanism. When partial matching is enabled (by setting the EnablePartialMatching parameter to true) the similarity between the chunks in the retrieval request and the chunks in the slots of the chunks in declarative memory are taken into consideration. The chunk with the highest activation is still the one retrieved, but with partial matching enabled that chunk might not have the exact slot values as specified in the retrieval request.

Adding the partial matching component into the activation equation, we now have the activation Ai of a chunk i defined fully as:

![full activation](images/fullActivation.png)

Bi, Wkj, Sji, and E have been discussed previously. The new term is the partial matching component.
* Specification elements l: The matching summation is computed over the slot values of the retrieval specification.
* Match Scale, **P**: This reflects the amount of weighting given to the similarity in slot l. This is a constant across all slots and is set with the MismatchPenalty parameter.
* Match Similarities, **Mli**: The similarity between the value l in the retrieval specification and the value in the corresponding slot of chunk i.

The similarity value, the Mli, can be set by the modeler along with the scale on which they are defined. The scale range is set with a maximum similarity (set using the :ms parameter) and a maximum difference (set using the :md parameter). By default, MaximumSimilarity is 0 and MaximumDifference is -1.0. The similarity between anything and itself is automatically set to the maximum similarity and by default the similarity between any other pair of values is the maximum difference.
Note that maximum similarity defaults to 0 and similarity values are actually negative. If a slot value matches the request then it does not penalize the activation, but if it mismatches then the activation is decreased. To demonstrate partial matching in use we will look at two example models.

## Grouped Recall
