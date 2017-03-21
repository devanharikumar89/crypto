## [Channel in SCAPI](http://scapi.readthedocs.io/en/latest/communication.html#setting-up-communication)

The communication layer provides communication services for any interactive cryptographic protocol. The basic communication channel is plain (unauthenticated and unencrypted). Secure channels can be obtained using TLS or by using a pre-shared symmetric key. This layer is heavily used by the interactive protocols in SCAPI’ third layer and by MPC protocols. It can also be used by any other cryptographic protocol that requires communication. The communication layer is comprised of two basic communication types: a two-party communication channel and a multiparty layer that arranges communication between multiple parties.

The two party communication layer provides a way to setup a two-party channel and communicate between them. This implementation improves on the previous SCAPI two-party channel in the following aspects:

The user can choose between three different channel types: a socket-based channel, a queue-based channel, and a TLS channel (for each of the socket and queue options). Each option has it own advantages and disadvantages, and the user should analyze which channel is most appropriate. In general, a queue is a more robust channel, but is less efficient than a socket channel.
The queue channels avoid, or more accurately automatically recover from, communication failures. Thus, an application that needs to be robust and recover from such failures should use the queue channel. We have chosen a socket based channel here.

Any number of channels can be established between a single pair of parties. Each party provides the number of channels that are desired, and the communication setup function returns a map containing this number of channels. This is very important for multithreading (e.g., SCAPI oblivious transfer protocols receive a channel in their constructor; in order to run many OTs in parallel, it is necessary to generate a different channel for each thread). We stress that only one port is needed, even if many channels are created. The different channels have unique internal port numbers, but use only a single external port number.
All the classes in the two-party communication are in the edu.biu.scapi.twoPartyComm package.


## Notes on Implementation

1. There are three files in this module - The client, the server and the properties file. The properties file is so that the client and server files get to know the number of parties taking part in the communication as well as their IP addresses and port numbers they are listening on. 

2. The server file creates a class that listens on a particlular port number that is pre determined. The client when started pings the server on the port it is listening on and thus a connection is set up. Channel is an abstration of the communication thus established. This part of the module is just to successfully test the creation of channels in SCAPI which can be used to implement [Pedersen Commitment](https://github.com/devanharikumar89/crypto/tree/master/4pedersen). 

## How to run

1. Install java, scapi
2. Update the properties file with the correct IP addresses of the client and server (Make sure they are in the same network)
3. Run Server.java
4. Run Client.java
