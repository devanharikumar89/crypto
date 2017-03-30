import java.util.*;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLZpSafePrimeElement;
import edu.biu.scapi.primitives.dlog.ZpElement;


class RS{
	public static void main(String[]args) throws Exception{
		String input = args[0];
		int t=Integer.parseInt(args[1]);
		//This means we have to divide the input string into length/(t+1) groups
		List<String>parts = new ArrayList<String>();
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

		List<GroupElement>partsAsElements = new ArrayList<GroupElement>();
		OpenSSLDlogZpSafePrime dlog = new OpenSSLDlogZpSafePrime();
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
			System.out.println("m"+j+" : "+((ZpElement)ge).getElementValue());
			j++;
			//System.out.println(new String(dlog.decodeGroupElementToByteArray(ge)));
		}
		
		



	}
}