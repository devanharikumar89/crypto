# [Merkle Tree Wiki](https://en.wikipedia.org/wiki/Merkle_tree)

In cryptography and computer science, a hash tree or Merkle tree is a tree in which every non-leaf node is labelled with the hash of the labels or values (in case of leaves) of its child nodes. Hash trees allow efficient and secure verification of the contents of large data structures. Hash trees are a generalization of hash lists and hash chains.
Demonstrating that a leaf node is a part of the given hash tree requires processing an amount of data proportional to the logarithm of the number of nodes of the tree.

# Notes on Implementation

1. Merkle.java is the file that has the basic class that holds the implementations of each of the operations in the Merkle tree. Node.java is the object that is in every node of the tree. MerkleUtils provides additional functionality in the implementation of Merkle trees. For a detailed information on the implementaion, inline comments are provided in the source code.

2. The leaf nodes of the Merkle tree hold data. In case the number of nodes are not a power of 2, we pad it to the nearest power of 2. After that, each pair of nodes at any level is hashed to make a node at the higher level. This continues until we get just one node which is the root node. 

3. An update operation is when one of the leaf nodes changes in value. We recalcualte the hash value of that node and propagate that change up the tree all the way till the root. 

4. A reveal operation reveals the list of hash values of siblings of all ancestors in the path from a particular leaf to the root of the Merkle tree. This information is vital in verifying the Merkle tree.

5. A verify operation takes a list of nodes (nodes of hash values of siblings of all ancestors in the path from a leaf to the root) and the corresponding index of the leaf node that supposedly revealed the list of nodes. Using the index of the lead node, a reveal operation is independetly called and the resulting list is compared with the input list with respect to the SHA values. 
