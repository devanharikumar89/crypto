import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.util.BigIntegers;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.GroupElementSendableData;
import edu.biu.scapi.primitives.dlog.ZpElement;
import edu.biu.scapi.primitives.dlog.groupParams.ZpGroupParams;
import edu.biu.scapi.primitives.dlog.cryptopp.CryptoPpDlogZpSafePrime;
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.comm.Channel;
import java.util.concurrent.TimeUnit;
import java.util.*;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaVerifierComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogVerifierComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaVerifier;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaCommonInput;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogCommonInput;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

		//System.out.println(sum);
		return sum;

	}
	
}
public class Committer{
    //g generator
    //p prime of the group
    //q is the order of the group
    //h arrives from the other party
    BigInteger g, p, q;
    BigInteger h;

    //The Dlog group of this protocol is as shown below. 
    //This is in keeping with the Pedersen Commitment and Sigma Protocol together.
    CryptoPpDlogZpSafePrime dlog;
    GroupElement g_ge;
    Channel channel;
    SecureRandom random;

    public Committer(){
        init2();
    }

    public void init2() {
        try{
        //Initializing the DLogGroup with the default constructor 
        //makes sure the group values are random every time it is done
        dlog = new CryptoPpDlogZpSafePrime();
        ZpGroupParams params = (ZpGroupParams)(dlog.getGroupParams());
        g_ge = dlog.getGenerator();
        g=params.getXg();
        q=params.getQ();
        p=params.getP();
        establishConnection();
        //The following step is to make sure that both the parties have the same global values for g, q and p.
        //This has to be changed to a secure 3rd party.
        channel.send(g.toString()+"|"+q.toString()+"|"+p.toString());
    }catch(Exception e){
        System.out.println(e.toString());
    }
    }

    //The following function reads the properties file and establishes a connection (called as channel)
    //between the two IP addresses. The channel object is used by the Pedersen commitment and the Sigma protocol.
    public void establishConnection(){
        LoadSocketParties loadParties = new LoadSocketParties("socket.properties");
        List<PartyData> listOfParties = loadParties.getPartiesList();
        for(PartyData data : listOfParties){
            System.out.println(data.toString());
        }
        try{
        TwoPartyCommunicationSetup commSetup = new SocketCommunicationSetup(listOfParties.get(0), listOfParties.get(1));

        //Call the prepareForCommunication function to establish one connection within 2000000 milliseconds.
        Map<String, Channel> connections = commSetup.prepareForCommunication(1, 2000000);

        //Return the channel to the calling application. There is only one created channel.
        channel = (Channel) connections.values().toArray()[0];
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+channel.toString());
        //System.out.println("Sending data");
        //channel.send("devan");
        System.out.println("\n*****Connection Established*****\n\n");
        System.out.println("\n*****COMMITTER*****\n\n");
        
        }catch(Exception e){
        System.out.println("*****Failed to establish a connection*****");
        }

    }
    public BigInteger commit(BigInteger m, BigInteger r) throws Exception{
        //Commitment : Compute c=g^m * h^r - if you know power of g that makes h, then it is easy to cheat
        //for the committer. So we let the receiver choose that value 'a' and use it for sigma protocol.
       
        //System.out.println("m : "+m.toString());
       // System.out.println("Commit Phase : \n");
        //TimeUnit.SECONDS.sleep(2);
        System.out.println("g : "+g.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        System.out.println("p : "+p.toString()+"\n\n");
       // TimeUnit.SECONDS.sleep(2);
        System.out.println("q : "+q.toString()+"\n\n");
       //TimeUnit.SECONDS.sleep(2);
        System.out.println("h : "+h.toString()+"\n\n");
       // TimeUnit.SECONDS.sleep(2);
        System.out.println("m : "+m.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        System.out.println("r : "+r.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("m : "+m.toString()+"\nr : "+r.toString()+"\n");

        GroupElement g_power_m_ge = dlog.exponentiate(g_ge, m);
        BigInteger g_power_m = ((ZpElement) g_power_m_ge).getElementValue();
        //System.out.println("g_power_m : "+g_power_m.toString());        


        GroupElement h_ge = dlog.generateElement(false, h);
        GroupElement h_power_r_ge = dlog.exponentiate(h_ge, r); 
        BigInteger h_power_r = ((ZpElement) h_power_r_ge).getElementValue();
        //System.out.println("h_power_r : "+h_power_r.toString());    
        

        GroupElement g_power_m_times_h_power_r_ge = dlog.multiplyGroupElements(g_power_m_ge, h_power_r_ge);
        BigInteger g_power_m_times_h_power_r = ((ZpElement) g_power_m_times_h_power_r_ge).getElementValue();
        //System.out.println("g_power_m_times_h_power_r : "+g_power_m_times_h_power_r.toString()); 
        //OpenSSLZpSafePrimeElement betterg = new OpenSSLZpSafePrimeElement();

        BigInteger comm = g_power_m_times_h_power_r.mod(p);
        //System.out.println("comm : "+comm);
        return comm;
    }

    public boolean verify(BigInteger comm, BigInteger deComm){
        if(comm==null && deComm == null){
            return true;
        }else if(comm==null || deComm == null){
            return false;
        }
        return comm.equals(deComm);

    }


    public static void main(String[] args) throws Exception {
        
        /*
        Let p and q be large primes such that q∣(p−1), let g be a generator of the order-q subgroup of Z⋆p. Let a be a random secret from Zq
        and h=g^a mod p. The values p, q, g and h are public, while a is secret.

        To commit to a message m∈Zq, the sender chooses a random r∈Zq and sends the commitment c=g^m * h^r mod p to the receiver; while in
        order to open the commitment, the sender reveals m and r, and the receiver verifies that c=g^m * h^r mod p.
        */

        Committer dlc = new Committer();
        dlc.random = new SecureRandom();        
        BigInteger q_minus_one = dlc.q.subtract(BigInteger.ONE);

        //This is where this party receives h value from the other party.
        System.out.println("COMMIT PHASE : \n");
        String h_val = (String)dlc.channel.receive();
        //TimeUnit.SECONDS.sleep(5);
        dlc.h = new BigInteger(h_val);
        System.out.println("Received h\n\n");
       // TimeUnit.SECONDS.sleep(2);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the message");
        String in = br.readLine();

        System.out.println("Enter the number of pieces the message has to be broken into");
        int pieces = Integer.parseInt(br.readLine());

        RS rs = new RS();
        List<BigInteger> images = rs.process(in, pieces);
        //BigInteger m = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, dlc.random);
        dlc.channel.send(String.valueOf(images.size()));//sending the size of the loop
        dlc.channel.receive();//placeholder for ack
        
        Map<Integer, Boolean>sigmaSummary = new HashMap<Integer, Boolean>();
        int mapIndex = 0;
        for(BigInteger m : images){
        System.out.println("Message in the field : "+m);  
        BigInteger r = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, dlc.random);

        //TimeUnit.SECONDS.sleep(5);
        BigInteger comm = dlc.commit(m, r);
        System.out.println("\nMessage Committed. \nSending Commitment\n\n");
        dlc.channel.send(comm.toString());
        //TimeUnit.SECONDS.sleep(5);
        System.out.println("DECOMMIT PHASE : \n");
        dlc.channel.receive();
       // TimeUnit.SECONDS.sleep(5);
        System.out.println("Received Ack. \nSending m\n");
        dlc.channel.send(m.toString());
        dlc.channel.receive();
       //TimeUnit.SECONDS.sleep(5);
        System.out.println("Received Ack. \nSending r\n");
        dlc.channel.send(r.toString());
        dlc.channel.receive();
       // TimeUnit.SECONDS.sleep(5);
        System.out.println("Received Ack. \nCommitter rests\n\n\n");

       // TimeUnit.SECONDS.sleep(10);
        System.out.println("Sigma Protocol Starts\n\n");
        System.out.println("VERIFIER\n\n\n");
       // TimeUnit.SECONDS.sleep(5);
        System.out.println("SCAPI Internal Verification");
        //GroupElement y  = dlc.dlog.reconstructElement(false, (GroupElementSendableData)dlc.channel.receive());
       // TimeUnit.SECONDS.sleep(5);
        //t - soundness parameter in bits
        int t = 64;
        SigmaVerifierComputation verifierComputation = new SigmaDlogVerifierComputation(dlc.dlog, t, new SecureRandom());
        //Creates Sigma verifier with the given SigmaVerifierComputation.
        SigmaVerifier verifier = new SigmaVerifier(dlc.channel, verifierComputation);
        // Creates input for the verifier.
        //Sets the given h
        GroupElement h_ge = dlc.dlog.generateElement(false, dlc.h);
        SigmaCommonInput input = new SigmaDlogCommonInput(h_ge);
        //Calls the verify function of the verifier.
        //verifier.verify(input); 
        Boolean result = verifier.verify(input);
        sigmaSummary.put(++mapIndex, result);
        System.out.println("\n\nVerified : "+result);
    }

    System.out.println("Sigma Protocol Verification Summary : ");
    for(int a : sigmaSummary.keySet()){
    	System.out.println(a+" : "+sigmaSummary.get(a));
    }

}}

