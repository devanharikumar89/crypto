//import java.math.BigInteger;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.util.BigIntegers;
import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.GroupElementSendableData;
import edu.biu.scapi.primitives.dlog.ZpElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.primitives.dlog.groupParams.ZpGroupParams;

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

public class Committer{
	BigInteger g, p, q, h;
	OpenSSLDlogZpSafePrime dlog;
	GroupElement g_ge;
    Channel channel;
    SecureRandom random;
	public Committer(BigInteger q, BigInteger g, BigInteger p){
		this.q=q;
		this.g=g;
		this.p=p;
		init(q, g, p);
        //h has to come from receiver
	}

	public void init(BigInteger q, BigInteger g, BigInteger p) {

		ZpGroupParams params = new ZpGroupParams(q, g, p);
		dlog = new OpenSSLDlogZpSafePrime(params);
		g_ge = dlog.getGenerator();

        establishConnection();

	}

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

        }catch(Exception e){
        System.out.println("Exception");
        }

    }
	public BigInteger commit(BigInteger m, BigInteger r){
		//Commitment : Compute c=g^m * h^r - if you know power of g that makes h, then it is easy to cheat
		//for the committer. So we let the receiver choose that value 'a' and use it for sigma protocol.
       
        //System.out.println("m : "+m.toString());
        System.out.println("Commit Phase : \n\ng : "+g.toString()+"\np : "+p.toString()+"\nq : "+q.toString()+"\nh : "+h.toString()+"\n\n");
        System.out.println("m : "+m.toString()+"\nr : "+r.toString()+"\n");

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
        // initiate a discrete log group
        // (in this case the OpenSSL implementation of the elliptic curve group K-233)
        //DlogGroup dlog = new OpenSSLDlogZpSafePrime(64);
        

        /*
        Let p and q be large primes such that q∣(p−1), let g be a generator of the order-q subgroup of Z⋆p. Let a be a random secret from Zq
        and h=g^a mod p. The values p, q, g and h are public, while a is secret.

        To commit to a message m∈Zq, the sender chooses a random r∈Zq and sends the commitment c=g^m * h^r mod p to the receiver; while in
        order to open the commitment, the sender reveals m and r, and the receiver verifies that c=g^m * h^r mod p.
        */
        //q, g and p in that order
        BigInteger q = BigInteger.valueOf(11);
        BigInteger g = BigInteger.valueOf(3);
        BigInteger p = BigInteger.valueOf(23);

        Committer dlc = new Committer(q, g, p);

        // get the group generator and order
        //GroupElement g_ge = dlog.getGenerator();
        //BigInteger g = ((ZpElement)g_ge).getElementValue();
        //System.out.println("g : "+g.toString());

        //BigInteger q = dlog.getOrder();//or params.getQ();
        //System.out.println("q : "+q.toString());
        

        /*At the third level we have:

        DlogZpSafePrime: The order q is not only a prime but also is such that prime p = 2*q + 1.
        DlogEcFp: Any elliptic curve over F_p.
        DlogEcF2m: Any elliptic curve over F_2[m].*/

        //BigInteger p = params.getP(); 
        //System.out.println("p : "+p.toString());

        //At this point we have p, q and g.

        
        // create a random group element

        dlc.random = new SecureRandom();
        
        BigInteger q_minus_one = q.subtract(BigInteger.ONE);
        BigInteger m = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, dlc.random);  
        BigInteger r = BigIntegers.createRandomInRange(BigInteger.ZERO, q_minus_one, dlc.random);
        //System.out.println("r : "+r.toString());
        String h_val = (String)dlc.channel.receive();
        TimeUnit.SECONDS.sleep(3);
        dlc.h = new BigInteger(h_val);
        System.out.println("Received h");
        BigInteger comm = dlc.commit(m, r);
        System.out.println("m, r Committed. Sending commitment");
        dlc.channel.send(comm.toString());
        dlc.channel.receive();
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Received ack, Sending m");
        dlc.channel.send(m.toString());
        dlc.channel.receive();
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Received ack, Sending r");
        dlc.channel.send(r.toString());
        dlc.channel.receive();
        //TimeUnit.SECONDS.sleep(3);
        System.out.println("Received ack, Committer rests");

        
        //BigInteger deComm = dlc.deCommit(m, r);

       

        //System.out.println("Commitment holds : "+dlc.verify(comm, deComm));

        //GroupElement h = dlog.createRandomElement();
        // multiply elements
       // GroupElement gMult = dlog.multiplyGroupElements(g1, h);
    }
}
