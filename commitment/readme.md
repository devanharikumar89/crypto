## [Pedersen Commitment SCAPI wiki](http://scapi.readthedocs.io/en/latest/interactive_layer/commitments.html)

* Let p and q be large primes such that q|(p-1)
* g - generator of the order-q sub group of Zp
* Let a be a random secret from Zq and h=g^a mod p
* The values p, q, g and h are public while a is a secret. 
* To commit to a message m element of Zq, the sender chooses a random r element of Zq and sends the commitment c = g^m h^r mod p to the receiver.
* To open the commitment the sender reveals m and r and receiver verifies that c = g^m h^r mod p
