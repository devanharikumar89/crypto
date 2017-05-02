import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.bouncycastle.util.BigIntegers;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.GroupElementSendableData;
import edu.biu.scapi.primitives.dlog.ZpElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.groupParams.ZpGroupParams;
import edu.biu.scapi.primitives.dlog.cryptopp.CryptoPpDlogZpSafePrime;

//Added imports from the channel/Server.java
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.exceptions.DuplicatePartyException;
import edu.biu.scapi.comm.twoPartyComm.SocketPartyData;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.comm.Channel;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaProverComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogProverComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaProver;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProverInput;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogProverInput;


public class  Receiver{
	BigInteger g, p, q, h;
    BigInteger secret;
	CryptoPpDlogZpSafePrime dlog;
	SecureRandom random;
	GroupElement g_ge;
    Channel channel;
	public Receiver(){
		init();
	}

	public void init(){
        try{
        establishConnection();
        String gqp = (String)(channel.receive());
        //TimeUnit.SECONDS.sleep(3);
        String[]publics = gqp.split("\\|");

        g=new BigInteger(publics[0]);
        q=new BigInteger(publics[1]);
        p=new BigInteger(publics[2]);

		ZpGroupParams params = new ZpGroupParams(q, g, p);
		dlog = new CryptoPpDlogZpSafePrime(params);
		g_ge = dlog.getGenerator();
		random = new SecureRandom();

		BigInteger q_minus_one = q.subtract(BigInteger.ONE);

        // create a random exponent a
        secret = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, random);
        //System.out.println("a : "+a.toString());

        // exponentiate g in r to receive a new group element
        GroupElement g_power_secret_ge = dlog.exponentiate(g_ge, secret);
        BigInteger g_power_secret = ((ZpElement) g_power_secret_ge).getElementValue();
        //System.out.println("g_power_a : "+g_power_a.toString());
        
        BigInteger g_power_secret_mod_p = g_power_secret.mod(p);
        h = g_power_secret_mod_p;//receiver should choose this, and use sigma protocol to show that.
        //System.out.println("h: "+h.toString());
    }catch(Exception e){
        System.out.println(e.toString());
    }

	}

    public void establishConnection(){
        LoadSocketParties loadParties = new LoadSocketParties("socket.properties");
        List<PartyData> listOfParties = loadParties.getPartiesList();
        //for(PartyData data : listOfParties){
        //   System.out.println(data.toString());
        //}
        try{
        //Note that the following line is different for a server and client
        TwoPartyCommunicationSetup commSetup = new SocketCommunicationSetup(listOfParties.get(1), listOfParties.get(0));

        //Call the prepareForCommunication function to establish one connection within 2000000 milliseconds.
        Map<String, Channel> connections = commSetup.prepareForCommunication(1, 2000000);

        //Return the channel to the calling application. There is only one created channel.
        channel = (Channel) connections.values().toArray()[0];
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+channel.toString());
        //System.out.println("Sending data");
        //channel.send("devan");
        System.out.println("\n*****Connection Established*****\n\n");
        System.out.println("\n*****RECEIVER*****\n\n");
        }catch(Exception e){
        System.out.println("*****Failed to establish a connection*****");
        }

    }
	public BigInteger deCommit(BigInteger m, BigInteger r) throws Exception{
		 //DeCommitment : Reveal m and r, and let the receiver verify comm = g^m * h^r mod p.


       // TimeUnit.SECONDS.sleep(2);
        //System.out.println("g : "+g.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("p : "+p.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("q : "+q.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("h : "+h.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("m : "+m.toString()+"\n\n");
        //TimeUnit.SECONDS.sleep(2);
        //System.out.println("r : "+r.toString()+"\n\n");
       // TimeUnit.SECONDS.sleep(2);

        GroupElement g_power_m_ge_rev = dlog.exponentiate(g_ge, m);
        BigInteger g_power_m_rev = ((ZpElement) g_power_m_ge_rev).getElementValue();
        //System.out.println("g_power_m_rev : "+g_power_m_rev.toString());        


        GroupElement h_ge_rev = dlog.generateElement(false, h);
        GroupElement h_power_r_ge_rev = dlog.exponentiate(h_ge_rev, r); 
        BigInteger h_power_r_rev = ((ZpElement) h_power_r_ge_rev).getElementValue();
        //System.out.println("h_power_r_rev : "+h_power_r_rev.toString());    
        

        GroupElement g_power_m_times_h_power_r_ge_rev = dlog.multiplyGroupElements(g_power_m_ge_rev, h_power_r_ge_rev);
        BigInteger g_power_m_times_h_power_r_rev = ((ZpElement) g_power_m_times_h_power_r_ge_rev).getElementValue();
        //System.out.println("g_power_m_times_h_power_r_rev : "+g_power_m_times_h_power_r_rev.toString()); 
        //OpenSSLZpSafePrimeElement betterg = new OpenSSLZpSafePrimeElement();

        BigInteger comm_rev = g_power_m_times_h_power_r_rev.mod(p);
        //System.out.println("comm_rev : "+comm_rev);
        return comm_rev;
	}

	public boolean verify(BigInteger comm, BigInteger deComm){
		if(comm==null && deComm == null){
			return true;
		}else if(comm==null || deComm == null){
			return false;
		}
		return comm.equals(deComm);

	}

    public byte[] formatByteArray(String input){
        String[]parts = input.split(",");
        byte[]array = new byte[parts.length];
        for(int i=0; i<array.length; i++){
            array[i]=Byte.parseByte(parts[i]);
        }
        return array;
    }

    public boolean verifyHash(String commitString, String[]siblings, byte[]sha){
        byte[]tester = MerkleUtils.getHash(commitString);
        for(String str : siblings){
            byte[]sibling = formatByteArray(str.substring(2));
            if(str.charAt(0)=='L'){
                tester = MerkleUtils.getHash(sibling, tester);
            }else{
                tester = MerkleUtils.getHash(tester, sibling);
            }
        }
        return Arrays.equals(tester, sha);
    }


    public static void main(String[] args) throws Exception {
        // initiate a discrete log group
        /*
        Let p and q be large primes such that q∣(p−1), let g be a generator of the order-q subgroup of Z⋆p. Let a be a random secret from Zq
        and h=g^a mod p. The values p, q, g and h are public, while a is secret.
        To commit to a message m∈Zq, the sender chooses a random r∈Zq and sends the commitment c=g^m * h^r mod p to the receiver; while in
        order to open the commitment, the sender reveals m and r, and the receiver verifies that c=g^m * h^r mod p.
        */

        Receiver dlc = new Receiver();
        System.out.println("COMMIT PHASE : \n");
        System.out.println("\nSending h\n\n");
        dlc.channel.send(dlc.h.toString());



        System.out.println("Sigma Protocol Starts\n\n");
        System.out.println("PROVER\n\n\n");
       // TimeUnit.SECONDS.sleep(5);
        System.out.println("SCAPI Internal Proof");
        //dlc.h is the secret known only to this party. 
        //t - soundness parameter in bits
        int t = 64;

        //GroupElement g = pro.dlog.getGenerator();
        //BigInteger w = new BigInteger("1234");//secret
        //GroupElement y = dlc.dlog.exponentiate(dlc.g_ge, dlc.h);

        //dlc.channel.send(y.generateSendableData());

        SigmaProverComputation proverComputation = new SigmaDlogProverComputation(dlc.dlog, t, new SecureRandom());

        //Create Sigma Prover with the given SigmaProverComputation.
        SigmaProver prover = new SigmaProver(dlc.channel, proverComputation);

        //Creates input for the prover.
        //In SigmaProtocolDlog, the prover gets a GroupElement y and a BigInteger w such that g_ge^h = y. 
        //y - should be a groupelement
        //h should be a BigInteger

       // TimeUnit.SECONDS.sleep(2);
        GroupElement h_val = dlc.dlog.generateElement(false, dlc.h);
        SigmaProverInput input = new SigmaDlogProverInput(h_val, dlc.secret);
       // TimeUnit.SECONDS.sleep(2);
        //Calls the prove function of the prover.
        prover.prove(input);
        System.out.println("\n\nProver rests");



        System.out.println("\n\nCOMMIT PHASE : \n");

       // int gh = Integer.parseInt((String)dlc.channel.receive());
       // dlc.channel.send("ack");
       // Map<Integer, Boolean>pedersenSummary = new HashMap<Integer, Boolean>();
       // int mapIndex = 0;
       //while(gh-->0){

        String deCommString = (String)dlc.channel.receive();
        //System.out.println(deCommString);
        byte[] sha = dlc.formatByteArray(deCommString);
        System.out.println(new String(sha));
        //String[] deCommitments = deCommString.split("\\|");                                                                             
        //System.out.println("Received Commitment. \nSending ack\n\n");
        //dlc.channel.send("ack");
        System.out.println("DECOMMIT PHASE : \n");

        


        System.out.println("Verifying...\n\n");
		boolean result = true;

       
        //System.out.println("\n\nVerifier rests\n\n\n");


        //Merkle tree queries start here.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true){
        System.out.println("Enter the node to be revealed, return for exit\nThis can range from 1 to the number of leaf nodes in Merkle tree with data\n");
        String in = br.readLine();
        if(in.length()==0){
            break;
        }
        dlc.channel.send(in);


        String commitString = (String)dlc.channel.receive();
        String[] commitStrings = commitString.split("\\|");                                                                               
        System.out.println("Received commits. \nSending ack\n\n");
        dlc.channel.send("ack");

        String rString = (String)dlc.channel.receive();
        String[] rStrings = rString.split("\\|");                                                                                 
        System.out.println("Received randoms. \nSending ack\n\n");
        dlc.channel.send("ack");

        String mString = (String)dlc.channel.receive();
        String[] mStrings = mString.split("\\|");                                                                               
        System.out.println("Received messages. \nSending ack\n\n");
        dlc.channel.send("ack");

        String encodedSiblings = (String)dlc.channel.receive();
        String[] siblings = encodedSiblings.split("\\|");                                                                               
        System.out.println("Received Merkle tree data for verification. \nSending ack\n\n");
        dlc.channel.send("ack");


        System.out.println("Merkle Tree holds : "+dlc.verifyHash(commitString, siblings, sha));

        for(int i=0; i<commitStrings.length-1; i++){

        BigInteger deComm = new BigInteger(commitStrings[i+1]);
        BigInteger m = new BigInteger(mStrings[i+1]);
        BigInteger r = new BigInteger(rStrings[i+1]);

        BigInteger comm = dlc.deCommit(m, r);
        result = result && dlc.verify(comm, deComm);
        }
        System.out.println("Commitment holds : "+result);

    }
        dlc.channel.send("done");
    }

}
