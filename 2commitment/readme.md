## [Commitment Scheme wiki](https://en.wikipedia.org/wiki/Commitment_scheme)
In cryptography, a commitment scheme allows one to commit to a chosen value (or chosen statement) while keeping it hidden to others, with the ability to reveal the committed value later.[1] Commitment schemes are designed so that a party cannot change the value or statement after they have committed to it: that is, commitment schemes are binding. Commitment schemes have important applications in a number of cryptographic protocols including secure coin flipping, zero-knowledge proofs, and secure computation.
A way to visualize a commitment scheme is to think of a sender as putting a message in a locked box, and giving the box to a receiver. The message in the box is hidden from the receiver, who cannot open the lock themselves. Since the receiver has the box, the message inside cannot be changed—merely revealed if the sender chooses to give them the key at some later time.
Interactions in a commitment scheme take place in two phases:
the commit phase during which a value is chosen and specified
the reveal phase during which the value is revealed and checked

## Notes on Implementation

1. Started the implementation by installing SCAPI and using its basic DiscreteLog example class - DlogExample.java. DlogExample from SCAPI initiates a discrete log group using the OpenSSL implementation of the elliptic curve group K-233. This class does not do anything towards implementing the Pedersen commitment, but is used just to play around with the Discrete Log math that is required in implementing Pedersen commitment. 

2. Using the discrete log functionality, DiscreteLogCommitment.java was written next. This class includes an initialization method init() to use q, g and p from the user input to create the discrete log group. In addition to init, it has the commit and decommit methods. Verify is the last method in the class that makes sure the commitment scheme holds. 

3. A problem with the design of DiscreteLogCommitment.java was that the committer and verifier were part of the same class, which means the committer knows the value of h before verifier tells him. This had to be changed. This meant moving the commit and decommit methods to different classes and making them communicate through sockets. Read about [channels](https://github.com/devanharikumar89/crypto/tree/master/3channel) 


## How to run

* Install java 1.6 or later
* DlogExample.java and DiscreteLogExample.java are independent java files and hence can be run by java commands. (Scapi commands in this case because we are using scapi and scapi commands include javac in the classpath) 
* To compile scapic filename.java
* To run scapi classname
