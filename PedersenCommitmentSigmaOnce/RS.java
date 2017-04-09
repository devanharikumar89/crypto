import java.util.*;
import java.math.BigInteger;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLZpSafePrimeElement;
import edu.biu.scapi.primitives.dlog.cryptopp.CryptoPpDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.ZpElement;


class RS{
	String input;
	int t;//Number of parts-1
	int N = 80;//statistical security parameter
	List<String>parts;
	List<GroupElement>partsAsElements;
	List<BigInteger>coefficients;
	List<BigInteger>images;
	CryptoPpDlogZpSafePrime dlog;

	public RS(){
		this.parts = new ArrayList<String>();
		this.partsAsElements = new ArrayList<GroupElement>();
		this.dlog = new CryptoPpDlogZpSafePrime();
		this.coefficients = new ArrayList<BigInteger>();
		this.images = new ArrayList<BigInteger>();
	}

	public List<BigInteger> process(String input, int t){
		int pad = (t+1)-(input.length() % (t+1));
		input += String.format("%1$-"+(pad)+"s", "" ).replace(" ", "0");
		System.out.println(input);

		int partLength = input.length()/(t+1);
		int i=0;
		while(i<input.length() && i+partLength<input.length()){
			parts.add(input.substring(i, i+partLength));
			i+=partLength;
		}
		if(i<input.length()){
			parts.add(input.substring(i));
		}
		System.out.println(parts.toString());

		//At this point the input string is split into parts
		//Now we need to convert each part into a field element.

		
		
		for(String part : parts){
			//System.out.println(new String(part.getBytes()));
			GroupElement ge = dlog.encodeByteArrayToGroupElement(part.getBytes());
			//System.out.println(ge.toString());
			partsAsElements.add(ge);
		}
		//System.out.println(partsAsElements.toString());
		//Done
		//At this point the split input parts can each be mapped into a group element
		//Now we need to convert each part into a field element.
		//p(x) = m0 + m1.x + m2.x^2 + m3.x^3 + ... + mt.x^t;
		int j=0;
		for(GroupElement ge : partsAsElements){
			//dlog.decodeGroupElementToByteArray((ECF2mPointBc)ge)
			BigInteger coefficient = ((ZpElement)ge).getElementValue();
			System.out.println("m"+j+" : "+coefficient);
			coefficients.add(coefficient);
			j++;
			//System.out.println(new String(dlog.decodeGroupElementToByteArray(ge)));
		}
		//System.out.println(rs.coefficients);
		

		//Defining the polynomial part is over at this point.
		//Next we start computing the code word.
		//We compute p(x) for n different x values.
		for(int index=1; index<=N; index++){	
		images.add(evaluatePolynomial(BigInteger.valueOf(index), t));
		}
	return images;
	}
	//This evaluation function has to be called N times, as of now with sequental alpha values from 1 to 80
	public BigInteger evaluatePolynomial(BigInteger alpha, int t){
		//System.out.println(alpha);
		int power = 0;
		BigInteger sum = BigInteger.ZERO;

		for(int k=0; k<=t; k++){
			BigInteger term = coefficients.get(k).multiply(alpha.pow(k));
			sum=sum.add(term);
		}

		System.out.println(sum);
		return sum;

	}
	
}
