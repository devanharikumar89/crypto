## [Pedersen Commitment](http://crypto.stackexchange.com/questions/9704/why-is-the-pedersen-commitment-computationally-binding)

* Let p and q be large primes such that q|(p-1)
* g - generator of the order-q sub group of Zp
* Let a be a random secret from Zq and h=g^a mod p
* The values p, q, g and h are public while a is a secret. 
* To commit to a message m element of Zq, the sender chooses a random r element of Zq and sends the commitment c = g^m h^r mod p to the receiver.
* To open the commitment the sender reveals m and r and receiver verifies that c = g^m h^r mod p

## Notes on Implementation

1. This module is application of modules 2 and 3 - Commitment and Channel together. Commitment module established the Pedersen commitment in a single class, because there was no communication established. Channel module established that two parties can communicate to make a channel object which can be used by the Commitment module to implement Pedersen commitment. That is what is implemented in this module.

2. The Committer class runs first listening on an established port number as per the properties file. All the values for p, q, g etc are chosen in the same way they were in the commitment module. When the Verifier class runs it establishes a channel between itself and the Committer. They exchange values h, committed message etc. This implementation overcomes the drawback of the previous implementation in that the committer has no knowledge of the h value. 

### TO-DO

3. This module can be made much simpler (we can do away with p, q, g etc ) by implementing Pedersen Commitment using the inbuilt classes for SCAPI. For this all we need is the Channel object that is created by establishing connection between the Committer and Verifier. 

## How to run

1. Install java, scapi
2. Run Committer.java
3. Run Verifier.java
