### [Pedersen Commitment using SCAPI inbuilt classes](http://scapi.readthedocs.io/en/latest/interactive_layer/commitments.html?highlight=pedersen%20commitment)

This implementation is more or less the direct application of the inbuilt Pedersen Commitment of the SCAPi library. Using this, one can commit any data. Everything other than the choice of the discrete log group and the channel object is abstracted away by the SCAPI implementation making the implementation concise.

### Notes on Implementation
1 There are two classes - Committer and Receiver much like the previous iterations of Pedersen Implementation in this project. A channel object is created by making them communicate with each other. 

2 In addition to the channel object this commitment scheme also requires a discrete log group implementation of our choice.

3 Any string can be committed with this scheme.

### How to run
* Install scapi, java 1.6 or later
* Uupdate the socket.properties with the ip address of the committer and receiver and pre determined port numbers.
* Run committer.java followed by receiver.java
* Verify that the committed value is correct.
