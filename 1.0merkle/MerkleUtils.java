import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author devan
 *
 */

//This class involves utilities that are used in implementing the Merkle class.
public class MerkleUtils {
	//This function returns the SHA 256 value for a particular string
	public static byte[] getHash(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			return hash;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Given two hash values this function returns the hash of the given hashes appended together
	public static byte[] getHash(byte[] left, byte[] right) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] doubleByte = new byte[left.length + right.length];
			int index = 0;
			for (byte l : left) {
				doubleByte[index++] = l;
			}
			for (byte r : right) {
				doubleByte[index++] = r;
			}
			byte[] hash = digest.digest(doubleByte);
			return hash;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Given two Merkel tree nodes, this function rearranges them in increasing order
	//and returns the hash of appended hashes by calling the overloaded version of this function
	public static byte[] getHash(Node left, Node right) {
		if (left.getId() < right.getId()) {
			return getHash(left.getSha256(), right.getSha256());
		}
		return getHash(right.getSha256(), left.getSha256());
	}
}
