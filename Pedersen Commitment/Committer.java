import edu.biu.scapi.primitives.dlog.DlogGroup;
import edu.biu.scapi.primitives.dlog.miracl.MiraclDlogECF2m;
import edu.biu.scapi.primitives.dlog.openSSL.OpenSSLDlogZpSafePrime;
import edu.biu.scapi.comm.Channel;
import java.security.SecureRandom;
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtCommitter;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.CmtCommitValue;
import edu.biu.scapi.interactiveMidProtocols.commitmentScheme.pedersen.CmtPedersenCommitter;
import java.util.*;

public class Committer{
	DlogGroup dlog;
    Channel channel;

    public Committer() throws Exception{
    	dlog = new MiraclDlogECF2m("K-233");
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
    	Committer com = new Committer();
    	CmtCommitter committer = new CmtPedersenCommitter(com.channel, com.dlog, new SecureRandom());
    	CmtCommitValue val = committer.generateCommitValue(new String("commit this string!").getBytes());
		committer.commit(val, 2);
		committer.decommit(2);
}
}
