## [Pedersen Commitment SCAPI wiki](http://scapi.readthedocs.io/en/latest/interactive_layer/commitments.html)

* Let p and q be large primes such that q|(p-1)
* g - generator of the order-q sub group of Zp
* Let a be a random secret from Zq and h=g^a mod p
* The values p, q, g and h are public while a is a secret. 
* To commit to a message m element of Zq, the sender chooses a random r element of Zq and sends the commitment c = g^m h^r mod p to the receiver.
* To open the commitment the sender reveals m and r and receiver verifies that c = g^m h^r mod p

## Notes on Implementation

1. Started the implementation by installing SCAPI and using its basic DiscreteLog example class - DlogExample.java. DlogExample from SCAPI initiates a discrete log group using the OpenSSL implementation of the elliptic curve group K-233. This class does not do anything towards implementing the Pedersen commitment, but is used just to play around with the Discrete Log math that is required in implementing Pedersen commitment. 

2. Using the discrete log functionality, DiscreteLogCommitment.java was written next. This class includes an initialization method init() to use q, g and p from the user input to create the discrete log group. In addition to init, it has the commit and decommit methods. Verify is the last method in the class that makes sure the commitment scheme holds. 

3. A problem with the design of DiscreteLogCommitment.java was that the committer and verifier were part of the same class, which means the committer knows the value of h before verifier tells him. This had to be changed. This meant moving the commit and decommit methods to different classes and making them communicate through sockets. Read about [channels](https://github.com/devanharikumar89/crypto/tree/master/channel) 


## How to run

* Install java 1.6 or later
* DlogExample.java and DiscreteLogExample.java are independent java files and hence can be run by java commands. (Scapi commands in this case because we are using scapi and scapi commands include javac in the classpath) 
* To compile scapic filename.java
* To run scapi classname
