#[Merkle Tree Wiki](https://en.wikipedia.org/wiki/Merkle_tree)

In cryptography and computer science, a hash tree or Merkle tree is a tree in which every non-leaf node is labelled with the hash of the labels or values (in case of leaves) of its child nodes. Hash trees allow efficient and secure verification of the contents of large data structures. Hash trees are a generalization of hash lists and hash chains.
Demonstrating that a leaf node is a part of the given hash tree requires processing an amount of data proportional to the logarithm of the number of nodes of the tree.

#Notes on Implementation

1. Merkle.java is the file that has the basic class that holds the implementations of each of the operations in the Merkle tree.
2. 
