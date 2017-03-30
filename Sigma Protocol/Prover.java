import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.comm.Channel;
import java.math.BigInteger;
import java.security.SecureRandom;
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtCommitter;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtCommitValue;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.pedersen.CmtPedersenCommitter;
import java.util.*;

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaProverComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogProverComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaProver;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProverInput;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogProverInput;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;


public class Prover{
	DlogGroup dlog;
    Channel channel;

    public Prover() throws Exception{
    	dlog = new MiraclDlogECF2m();
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


    public static void main(String[] args) throws Exception {
    	Prover pro = new Prover();

        //Creates sigma prover computation.
        //t - soundness parameter in bits
        int t = 10;
        GroupElement g = pro.dlog.getGenerator();
        BigInteger w = new BigInteger("1234");//secret
        GroupElement h = pro.dlog.exponentiate(g, w);
        SigmaProverComputation proverComputation = new SigmaDlogProverComputation(pro.dlog, t, new SecureRandom());

        //Create Sigma Prover with the given SigmaProverComputation.
        SigmaProver prover = new SigmaProver(pro.channel, proverComputation);

        //Creates input for the prover.
        //In SigmaProtocolDlog, the prover gets a GroupElement h and a BigInteger w such that g^w = h. 
        //h - should be a groupelement
        //w should be a BigInteger

        SigmaProverInput input = new SigmaDlogProverInput(h, w);

        //Calls the prove function of the prover.
        prover.prove(input);
        System.out.println("Prover done");
        }
    }

