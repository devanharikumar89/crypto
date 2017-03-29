
/**
 * @author devan
 *
 */

/*This class represents a node in a Merkle tree.
 * data - represents the data stored in the tree. Only relevant for the leaf nodes
 * sha256 - SHA 256 hash value for a particular node
 * isLeaf - decides whether a node is a leaf or an inner node
 * left, right, parent - represent the left, right and parent nodes
 * id - A number that represents the id of the node. For a node this is its number counted L to R from leaf level to root level
 * */

public class Node {
	private String data;
	private byte[] sha256;
	private boolean isLeaf;
	private Node left;
	private Node right;
	private Node parent;
	private int id;

	// constructor only to be used for leaves
	public Node(String data, int id) {
		this.data = data;
		this.sha256 = MerkleUtils.getHash(data);
		this.isLeaf = true;
		this.left = null;
		this.right = null;
		this.parent = null;
		this.id = id;
	}

	// constructor only to be used for non-leaf nodes
	public Node(Node left, Node right, int id) {
		this.data = null;
		this.sha256 = MerkleUtils.getHash(left.sha256, right.sha256);
		this.isLeaf = false;
		this.left = left;
		this.right = right;
		this.parent = null;
		this.id = id;
	}
    //public getters and setters for private fields.
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getSha256() {
		return sha256;
	}

	public void setSha256(byte[] sha256) {
		this.sha256 = sha256;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

}
