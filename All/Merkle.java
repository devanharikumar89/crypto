import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

/**
 * @author devan
 *
 */

/* 
 * The class Merkle implements the operations on the Merkle tree.
 * Eack of these operations are explained below in detail.
 * root - root of the Merkle tree
 * sha256 - SHA 256 of the root of the Merkle tree
 * allNodes - list of all nodes in the tree in the order left to right, leaf level to root level
 * dataIndices - the indices of the nodes that have data. 
 * */

public class Merkle {
	private Node root;
	private byte[] sha256;
	private List<Node> allNodes;
	private int dataIndices;
	//this value has to be added to convert index to id
	private int offset;
	//this map holds all the nodes against their ids
	private Map<Integer, Node>nodeMap;

	//getters and setters begin here
	public Map<Integer, Node> getNodeMap(){
		return nodeMap;
	}
	
	public void setNodeMap(Map<Integer, Node>nodeMap) {
		this.nodeMap = nodeMap;
	}

	public int getOffset(){
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getDataIndices() {
		return dataIndices;
	}

	public void setDataIndices(int dataIndices) {
		this.dataIndices = dataIndices;
	}

	public List<Node> getAllNodes() {
		return allNodes;
	}

	public void setAllNodes(List<Node> allNodes) {
		this.allNodes = allNodes;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public byte[] getSha256() {
		return sha256;
	}

	public void setSha256(byte[] sha256) {
		this.sha256 = sha256;
	}
	
	/*
	 * This function takes as input an array of strings. 
	 * The first thing that is being checked is the size
	 * of the array. We pad it to the nearest power of 2
	 * greater than the current size of the array. Each 
	 * element is thereafter added to the allNodes list 
	 * using the constructor meant for leaf nodes. Later, 
	 * this list is iterated and the tree is built up, using
	 * the constructor meant for non leaf nodes.
	 * */
	public void makeMerkleTree(String[] values) {

		if (values == null || values.length <= 0) {
			System.out.println("Invalid number of values");
			System.exit(0);
		}
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(values));
		int len = list.size();
		if ((len & (len - 1)) != 0)// power of 2 check
		{
			pad(list);
		}
		this.offset = list.size()-1;
		this.dataIndices = list.size();
		this.allNodes = new ArrayList<Node>();
		for (String str : list) {
			allNodes.add(new Node(str));
		}
		int limit = allNodes.size();
		int counter = 0;
		while (limit > 1) {
			int count = 0;
			List<Node> temp = new ArrayList<Node>();
			while (count < limit) {
				Node left = allNodes.get(counter++);
				Node right = allNodes.get(counter++);
				Node parent = new Node(left, right);
				left.setParent(parent);
				right.setParent(parent);
				temp.add(parent);
				count += 2;
			}
			allNodes.addAll(temp);
			limit /= 2;
		}
		// System.out.println(allNodes.toString());
		this.root = allNodes.get(allNodes.size() - 1);
		//At this point we assign the ids of all the nodes, 
		//in a top down fashion (level order traversal)
		//so that the reveal operation becomes easier
		//This function also updates the node map
		assignIds();
		System.out.println("\n\nMERKLE TREE CREATION");
		System.out.println("____________________\n\n");
		System.out.println("Number of leaves with data : "+len);
		System.out.println("Number of leaves : "+dataIndices);
		System.out.println("Status : OK\n\n");
			
	}

	private void assignIds(){
		Queue<Node> queue = new LinkedList<Node>();
		nodeMap = new HashMap<Integer, Node>();
        queue.add(this.root);
        int id = 1;
        while(!queue.isEmpty()){
        	Node temp = queue.poll();
        	temp.setId(id);
        	nodeMap.put(id++, temp);

        	if(temp.getLeft()!=null)
        	queue.add(temp.getLeft());
        	if(temp.getRight()!=null)
        	queue.add(temp.getRight());
        }
	}

	//function used to pad the list to a power of 2
	private void pad(ArrayList<String> list) {
		int shift = 1;
		int current = list.size();
		while (shift < current) {
			shift <<= 1;
		}
		int remaining = shift - current;
		while (remaining-- > 0) {
			list.add("TEST");
		}
	}


	//given the index of a node, this one returns its sibling
	private int getSibling(int index) {
		return index % 2 == 0 ? index + 1 : index - 1;
	}

	//Given an index this function reveals the list of nodes sibling
	//to those fall in the path of that node from the leaf to the root
	//Update : this index is the number of the node at the leaf level from
	//left to right. We need to add the proper offset to match the id number
	public List<Node> reveal(int index) {
		if (index > this.dataIndices) {
			System.out.println("INDEX OUT OF BOUNDS");
			System.exit(0);
		}
		int id = index+offset;
		List<Node> siblingsUpstream = new ArrayList<Node>();
		Node node = this.allNodes.get(index);
		while (id!=1) {
			siblingsUpstream.add(this.nodeMap.get(getSibling(id)));
			id/=2;
		}
		
		return siblingsUpstream;
	}

	public String revealEncoded(int index) {
		List<Node> unencodedSiblings = reveal(index);
		StringBuilder sb = new StringBuilder("");
		for(Node node : unencodedSiblings){
			if(node.getId()%2==0){
				sb.append("L,");
			}else{
				sb.append("R,");
			}
			sb.append(getSerialByteArray(node.getSha256()));
			sb.append("|");
		}
		return sb.toString().substring(0, sb.length()-1);
	}

	private String getSerialByteArray(byte[] array){
        StringBuilder sb = new StringBuilder();
        for(byte b : array){
            sb.append(String.valueOf(b));
            sb.append(",");
        }
        return sb.toString().substring(0, sb.length()-1);
    }

	//This function updates a particular leaf node at a given index
	//to a particular value and updates all the hash values up the tree
	public void update(int index, String val) {
		if (index > this.dataIndices) {
			System.out.println("INDEX OUT OF BOUNDS");
			System.exit(0);
		}
		int id = index+offset;
		Node node = this.nodeMap.get(id);
		node.setData(val);
		node.setSha256(MerkleUtils.getHash(val));
		List<Node> siblingsUpstream = reveal(index);
		for (Node sibling : siblingsUpstream) {
			node.getParent().setSha256(MerkleUtils.getHash(node, sibling));
			node = node.getParent();
		}

	}

	//This function verifies the reveal operation for a particular node.
	//Given an index and the output of its reveal operation, this function verifies if
	//it is correct.
	public boolean verify(List<Node> siblingsUpstream, int index) {
		Node leaf = this.nodeMap.get(offset+index);
		byte[] firstHash = MerkleUtils.getHash(leaf.getData());
		for (Node sibling : siblingsUpstream) {
			if (leaf.getId() % 2 == 0) {
				firstHash = MerkleUtils.getHash(firstHash, sibling.getSha256());
			} else {
				firstHash = MerkleUtils.getHash(sibling.getSha256(), firstHash);
			}
			leaf = leaf.getParent();
		}
		return Arrays.equals(firstHash, this.root.getSha256());
	}

	public void visualize() {
		for (Node node : this.allNodes) {
			System.out.println("Node : " + node.getId());
			//System.out.println("DATA : " + node.getData());
			//System.out.println("SHA 256 : " + Arrays.toString(node.getSha256()));
			}
	}

	//public static void main(String[] args) {
	//	String[] data = { "hello", "my", "name", "is", "devan"};
	//	Merkle tree = new Merkle();
	//	tree.makeMerkleTree(data);
	//	tree.update(12, "but");
	//	System.out.println(tree.revealEncoded(3));
	//	tree.visualize();
	//	System.out.println(tree.verify(tree.reveal(19), 19));
	//	// remove the print in reveal() because it is getting called internally
	//	// in update function as well.
	//	System.out.println(tree.verify(tree.reveal(17), 17));
	//}
}