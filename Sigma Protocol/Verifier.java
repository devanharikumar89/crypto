import edu.biu.scapi.primitives.dlog.DlogGroup;
import java.math.BigInteger;
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.comm.Channel;
import java.security.SecureRandom;
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtReceiver;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtCommitValue;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtRCommitPhaseOutput;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.pedersen.CmtPedersenReceiver;
import java.util.*;

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaVerifierComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogVerifierComputation;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.SigmaVerifier;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaCommonInput;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dlog.SigmaDlogCommonInput;
import edu.biu.scapi.primitives.dlog.GroupElement;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;


public class  Verifier{
    DlogGroup dlog;
    Channel channel;

    public Verifier() throws Exception{
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
        //Note that the following line is different for a server and client
        TwoPartyCommunicationSetup commSetup = new SocketCommunicationSetup(listOfParties.get(1), listOfParties.get(0));

        //Call the prepareForCommunication function to establish one connection within 2000000 milliseconds.
        Map<String, Channel> connections = commSetup.prepareForCommunication(1, 2000000);

        //Return the channel to the calling application. There is only one created channel.
        channel = (Channel) connections.values().toArray()[0];
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+channel.toString());
        //System.out.println("Sending data");
        //channel.send("devan");
        System.out.println("Created Channel\n");
        }catch(Exception e){
        System.out.println("Exception");
        }

    }


    public static void main(String[] args) throws Exception {
        Verifier ver = new Verifier();
        //Creates sigma verifier computation.
        //t - soundness parameter in bits
        int t = 10;
        SigmaVerifierComputation verifierComputation = new SigmaDlogVerifierComputation(ver.dlog, t, new SecureRandom());

        //Creates Sigma verifier with the given SigmaVerifierComputation.
        SigmaVerifier verifier = new SigmaVerifier(ver.channel, verifierComputation);

        // Creates input for the verifier.
        //Sets the given h


        GroupElement g = ver.dlog.getGenerator();
        BigInteger w = new BigInteger("1234");//secret
        GroupElement h = ver.dlog.exponentiate(g, w);

        SigmaCommonInput input = new SigmaDlogCommonInput(h);

        //Calls the verify function of the verifier.
        //verifier.verify(input); 
        System.out.println("Verified : "+verifier.verify(input));
        
    }
}


