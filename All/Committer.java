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
import java.lang.StringBuilder;

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

        return g_power_m_times_h_power_r;
    }

    public boolean verify(BigInteger comm, BigInteger deComm){
        if(comm==null && deComm == null){
            return true;
        }else if(comm==null || deComm == null){
            return false;
        }
        return comm.equals(deComm);

    }
    String getSerialByteArray(byte[] array){
        StringBuilder sb = new StringBuilder();
        for(byte b : array){
            sb.append(String.valueOf(b));
            sb.append(",");
        }
        return sb.toString().substring(0, sb.length()-1);
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

        String h_val = (String)dlc.channel.receive();
        //TimeUnit.SECONDS.sleep(5);
        dlc.h = new BigInteger(h_val);
        System.out.println("Received h\n\n");
        //This is where this party receives h value from the other party.



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
        System.out.println("\n\nVerified : "+result);
        System.out.println("\n___________________________________________________________________");



        System.out.println("\n\nCOMMIT PHASE : \n");
       // TimeUnit.SECONDS.sleep(2);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("Enter the message");
        String in = br.readLine();
        System.out.println("Enter the length of each piece the message has to be broken into. \nThe number of leaves in the Merkle tree is dependent on this value");
        int leaf_len = Integer.parseInt(br.readLine());
        System.out.println("Enter the number of pieces the each message piece has to be broken into. \nThis is the t value used in RS codes");
        int pieces = Integer.parseInt(br.readLine());

        RS rs = new RS((ZpGroupParams)(dlc.dlog.getGroupParams()));

        List<String> leaves = rs.splitEqually(in, leaf_len);
        String[] commitsArray = new String[leaves.size()];
        String[] messagesArray = new String[leaves.size()];
        String[] randomsArray = new String[leaves.size()];
        //The following process of finding the RS codes has to be done for all the pieces of the message.
        for(int index=0; index<commitsArray.length; index++){
        List<BigInteger> images = rs.process(in, pieces);

        //Warning : All the commits, messages and randoms created this way will be of the form : |2456654654|2456621451|...|2456954654|
        //Strip the start and the end before splitting them using the pipe delimiter

        StringBuilder allCommits = new StringBuilder("|");
        StringBuilder allMessages = new StringBuilder("|");
        StringBuilder allRandoms = new StringBuilder("|");

        for(BigInteger m : images){
        System.out.println("Message in the field : "+m);
        BigInteger r = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, dlc.random);
        BigInteger comm = dlc.commit(m, r);

        allCommits.append(comm.toString()+"|");
        allMessages.append(m.toString()+"|");
        allRandoms.append(r.toString()+"|");

    	}
        commitsArray[index]=allCommits.toString();
        messagesArray[index]=allMessages.toString();
        randomsArray[index]=allRandoms.toString();

        //System.out.println("Commit : "+commitsArray[index]);
        //TimeUnit.SECONDS.sleep(7);
        //System.out.println("Message : "+messagesArray[index]);
        //TimeUnit.SECONDS.sleep(7);
        //System.out.println("Random : "+randomsArray[index]);
        //TimeUnit.SECONDS.sleep(7);

        }
    	//System.out.println(allCommits.toString());
    	//System.out.println(allMessages.toString());
    	//System.out.println(allRandoms.toString());

        //At this point the array commitsArray has roughly len(in)/leaf_len number of entries each corresponding to 80(security parameter)
        //field elements (big integers) that are appended together using '|'
        //We send this array into the Merkle tree.

        Merkle tree = new Merkle();
        tree.makeMerkleTree(commitsArray);
        byte[] sha = tree.getRoot().getSha256();
        //byte array cannot be reconstructed as is at the other end no matter what encoding is used
        //So each character in the array is separately appended to a string and that string is sent 
        //to the Receiver. At the other end the string is parsed and tbe sha array is reconstructed
        String sendableByteArray = dlc.getSerialByteArray(sha);
        System.out.println(sendableByteArray);
        System.out.println("Root Hash String: "+new String(sha));

        System.out.println("\nMessage Committed and Merkle Tree Created. \nSending Root Hash\n\n");
        //dlc.channel.send(new String(tree.getRoot().getSha256()));
        //String a = "devan harikumar";
        dlc.channel.send(sendableByteArray);
        //for(byte x : a.getBytes()){
        //System.out.println(x);}
        System.out.println("DECOMMIT PHASE : \n");
        while(true){
        String rev = (String)dlc.channel.receive();
        if(rev.equals("done")){
            break;
        }
        Integer loc = Integer.parseInt(rev);
        List<Node> siblingsUpstream = tree.reveal(loc);
        for(Node node : siblingsUpstream){
            System.out.println(node.getId()+" : "+new String(node.getSha256()));
        }
        //At this point we are sending all the commit values for the message. These commit values are created using the Reed Solomon
        //Implementation, which means we may not need all of them to reconstruct the message. In future this has to be changed to a random
        //n number of values selected from all the values.
        System.out.println("Received Node Id. \nSending commits\n");
        dlc.channel.send(commitsArray[loc].toString());

        dlc.channel.receive();
        System.out.println("Received Ack. \nSending r\n");
        dlc.channel.send(randomsArray[loc].toString());

        dlc.channel.receive();
        System.out.println("Received Ack. \nSending m\n");
        dlc.channel.send(messagesArray[loc].toString());

        dlc.channel.receive();
        System.out.println("Received Ack. \nCommitter rests\n\n\n");
        }
        }

        //The following Merkle tree operations are to be done on the Receiver side. But as long as we do not have a 3rd party exposing the 
        //Merkle tree APIs we can do it here to check if it is correct.
        //We use the byte array that was sent to the receiver as our starting point.
        //The reveal operation should be such that given a leaf number the tree should reveal all the nodes of siblings up the tree.

        


}

