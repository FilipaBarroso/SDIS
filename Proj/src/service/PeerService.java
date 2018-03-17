package service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import channels.MC;
import channels.MDB;
import channels.MDR;
import channels.Messenger;
import utils.Utils;

public class PeerService {

	private static MulticastSocket socket;
	private static Peer localPeer;
	private static Messenger messenger;
	
	private static volatile MC mcThread;
	private static volatile MDB mdbThread;
	private static volatile MDR mdrThread;
	
	public static String defaultServer = "225.0.0.0";
	public static int default_MCport = 1, default_MDBport = 2, default_MDRport = 3;

	public static void main(String args[]) throws Exception {
		// check whether args are empty or there's channel configs / a command 
		System.out.println("STARTED PEER SERVICE \n");
		
		socket = new MulticastSocket();
		localPeer = new Peer(Utils.getPeerAddr(), socket.getLocalPort());
		
		
		//	Multicast Channels threads
		mcThread = new MC(InetAddress.getByName(defaultServer), default_MCport, localPeer.get_ip());
		new Thread(mcThread).start();
		//  new Thread(mdbThread).start();
		//	new Thread(mdrThread).start();

		Messenger messenger = new Messenger(socket, localPeer, InetAddress.getByName(defaultServer));
	}
	
	public static Peer getLocalPeer() {
		return localPeer;
	}
	
	public static MulticastSocket getSocket() {
		return socket;
	}
	
}
