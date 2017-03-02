import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.exceptions.DuplicatePartyException;
import edu.biu.scapi.comm.twoPartyComm.SocketPartyData;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.comm.twoPartyComm.SocketCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.TwoPartyCommunicationSetup;
import edu.biu.scapi.comm.Channel;
import java.util.*;

class SetupPartiesCopy{
public static void main(String[]args){
LoadSocketParties loadParties = new LoadSocketParties("socket.properties");
List<PartyData> listOfParties = loadParties.getPartiesList();
for(PartyData data : listOfParties){
	System.out.println(data.toString());
}
try{
TwoPartyCommunicationSetup commSetup = new SocketCommunicationSetup(listOfParties.get(1), listOfParties.get(0));

//Call the prepareForCommunication function to establish one connection within 2000000 milliseconds.
Map<String, Channel> connections = commSetup.prepareForCommunication(1, 2000000);
Channel channel = (Channel) connections.values().toArray()[0];
String data = (String)channel.receive();
System.out.println("Received : "+data);

}catch(Exception e){
System.out.println("Exception");
}
}
}
