## Notes on Implementation

1. Go through classes SigmaVerifierComputation, SigmaDlogVerifierComputation, SigmaVerifier, SigmaDlogCommonInput, SigmaProverComputation, SigmaDlogProverComputation, SigmaDlogProverInput.

2. In SigmaProtocolDlog, the prover gets a GroupElement h and a BigInteger w such that g^w = h. The h - should be a groupelement and w should be a BigInteger.

3. Started using OpenSSLDlogZpSafePrime class for the DlogGroup however it gave the error saying that the requested value for ZpElement is not a quadratic resideue. Hence changed the DlogGroup to MiraclDlogECF2m.

4. The t value was initialized at 1000 and it threw and error saying that it was too high. Changed it to 10. 




## How to Run

* Install java 1.6 or later.
* Update the properties file with the IP addresses. 
* Run Prover.java and Verifier.java
