import java.util.*;
import java.math.BigInteger;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLZpSafePrimeElement;
import edu.biu.scapi.primitives.dlog.ZpElement;


class RS{
	String input;
	int t;//Number of parts
	int N;//
	List<String>parts;
	List<GroupElement>partsAsElements;
	List<BigInteger>coefficients;
	OpenSSLDlogZpSafePrime dlog;

	public RS(){
		this.parts = new ArrayList<String>();
		this.partsAsElements = new ArrayList<GroupElement>();
		this.dlog = new OpenSSLDlogZpSafePrime();
		coefficients = new ArrayList<BigInteger>();
	}

	public static void main(String[]args) throws Exception{
		RS rs = new RS();
		rs.input = args[0];
		rs.t=Integer.parseInt(args[1]);
		//This means we have to divide the input string into length/(t+1) groups
		int pad = (rs.t+1)-(rs.input.length() % (rs.t+1));
		rs.input += String.format("%1$-"+(pad)+"s", "" ).replace(" ", "0");
		System.out.println(rs.input);

		int partLength = rs.input.length()/(rs.t+1);
		int i=0;
		while(i<rs.input.length() && i+partLength<rs.input.length()){
			rs.parts.add(rs.input.substring(i, i+partLength));
			i+=partLength;
		}
		if(i<rs.input.length()){
			rs.parts.add(rs.input.substring(i));
		}
		System.out.println(rs.parts.toString());

		//At this point the input string is split into parts
		//Now we need to convert each part into a field element.

		
		
		for(String part : rs.parts){
			//System.out.println(new String(part.getBytes()));
			GroupElement ge = rs.dlog.encodeByteArrayToGroupElement(part.getBytes());
			//System.out.println(ge.toString());
			rs.partsAsElements.add(ge);
		}
		//System.out.println(partsAsElements.toString());
		//Done
		//At this point the split input parts can each be mapped into a group element
		//Now we need to convert each part into a field element.
		//p(x) = m0 + m1.x + m2.x^2 + m3.x^3 + ... + mt.x^t;
		int j=0;
		for(GroupElement ge : rs.partsAsElements){
			//dlog.decodeGroupElementToByteArray((ECF2mPointBc)ge)
			BigInteger coefficient = ((ZpElement)ge).getElementValue();
			System.out.println("m"+j+" : "+coefficient);
			rs.coefficients.add(coefficient);
			j++;
			//System.out.println(new String(dlog.decodeGroupElementToByteArray(ge)));
		}
		//System.out.println(rs.coefficients);
		

		//Defining the polynomial part is over at this point.
		//Next we start computing the code word.
		//We compute p(x) for n different x values.
		rs.evaluatePolynomial(new BigInteger("23423"));

	}
	public BigInteger evaluatePolynomial(BigInteger alpha){
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
