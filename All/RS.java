import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.ZpElement;
import java.math.BigInteger;
import java.util.*;
import edu.biu.scapi.primitives.dlog.cryptopp.CryptoPpDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.groupParams.ZpGroupParams;

//This code converts a string into a polynomial by splitting the it into equal length subtrings,
//and the polynomial is evaluated to N values in modular arithmetic. The values are called images in
//this implementation. At any time, we need only t of them to rebuild the polynomial that
//corresponds to an input string.

class RS{
	//This is the input string.
	String input;
	//There are t+1 equal(?) parts in a string each of which 
	//becomes a coefficient in the polynomials
	int t;
	//statistical security parameter
	//This simply means the polynomial will be evaluated at N different 
	//points to create N images. Out of these N any t+1 images can be 
	//used to recreate the polynomial. After retrieving the polynomial, the 
	//coefficents can be found out using the Cramer's rule using determinants
	//and they can be mapped back into the string and the string can be regenerated.
	int N = 80;
	//parts holds the substrings
	List<String>parts;
	//parts are mapped into elements in the group.
	List<GroupElement>partsAsElements;
	//These are the coefficients of the polynomial
	List<BigInteger>coefficients;
	//These are the images created by evaluating the polynomial
	//at N different points
	List<BigInteger>images;
	CryptoPpDlogZpSafePrime dlog;
	ZpGroupParams params;

	public RS(ZpGroupParams params){
		this.parts = new ArrayList<String>();
		this.partsAsElements = new ArrayList<GroupElement>();
		//The parameters are passed from the Committer class here because
		//it is very important that the dlog object of the RS class has the same values esp. 
		//the prime of the group, p. 
		this.params = params;
		this.dlog = new CryptoPpDlogZpSafePrime(params);
		this.coefficients = new ArrayList<BigInteger>();
		this.images = new ArrayList<BigInteger>();
	}

	//This function snippet is taken word for word from Stackoverflow.com
	//Given a string text, this function splits it into equal length substrings
	//stores them in a list and returns the list.
	public static List<String> splitEqually(String text, int size) {
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}

	public List<BigInteger> process(String input, int t){
		//The following two lines take the input string and pads it using 0's
		//so that when split all the t+1 parts have equal length.
		int pad = (t+1)-(input.length() % (t+1));
		input += String.format("%1$-"+(pad)+"s", "" ).replace(" ", "0");

		int partLength = input.length()/(t+1);
		int i=0;
		while(i<input.length() && i+partLength<input.length()){
			parts.add(input.substring(i, i+partLength));
			i+=partLength;
		}
		if(i<input.length()){
			parts.add(input.substring(i));
		}
		//System.out.println(parts.toString());

		//At this point the input string is split into parts
		//Now we need to convert each part into a field element.
		for(String part : parts){
			GroupElement ge = dlog.encodeByteArrayToGroupElement(part.getBytes());
			partsAsElements.add(ge);
		}
		//At this point the split input parts can each be mapped into a group element
		//Now we need to convert each part into a field element(BigInteger).
		//p(x) = m0 + m1.x + m2.x^2 + m3.x^3 + ... + mt.x^t;
		int j=0;
		for(GroupElement ge : partsAsElements){
			//dlog.decodeGroupElementToByteArray((ECF2mPointBc)ge)
			BigInteger coefficient = ((ZpElement)ge).getElementValue();
			//System.out.println("m"+j+" : "+coefficient);
			coefficients.add(coefficient);
			j++;
		}
		//Defining the polynomial part is over at this point.
		//Next we start computing the code word.
		//We compute p(x) for n different x values.
		for(int index=1; index<=N; index++){	
		images.add(evaluatePolynomial(BigInteger.valueOf(index), t));
		}
	return images;
	}
	//This evaluation function has to be called N times, as of now with sequental alpha values from 1 to N
	//A question is whether this evaluation has to be done in modular arithmetic
	public BigInteger evaluatePolynomial(BigInteger alpha, int t){
		//System.out.println(alpha);
		int power = 0;
		BigInteger sum = BigInteger.ZERO;

		for(int k=0; k<=t; k++){
			BigInteger term = coefficients.get(k).multiply(alpha.pow(k));
			sum=sum.add(term);
		}

		//This is if we dot need modular arithmetic
		//return sum;
		//This is if we need modular arithmetic
		return sum.mod(params.getP());

	}
	
}