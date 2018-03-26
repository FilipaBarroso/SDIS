package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import channels.MC;
import channels.MDB;
import channels.MDR;
import channels.Messenger;

public class PeerService {

	private static MulticastSocket socket;
	private static Peer localPeer;
	private static Messenger messenger;
	public static Random random = new Random();

	private static volatile MC mcThread;
	private static volatile MDB mdbThread;
	private static volatile MDR mdrThread;

	public static String defaultServer = "225.0.0.0";
	public static int default_MCport = 1, default_MDBport = 2, default_MDRport = 3;

	public static void main(String args[]) throws Exception {
		// check whether args are empty or there's channel configs / a command 
		System.out.println("STARTED PEER SERVICE \n");

		socket = new MulticastSocket();
		localPeer = new Peer(getPeerAddr(), socket.getLocalPort());


		//	Multicast Channels threads
		mcThread = new MC(InetAddress.getByName(defaultServer), default_MCport, localPeer.get_ip());
		new Thread(mcThread).start();
		mdbThread = new MDB(InetAddress.getByName(defaultServer), default_MDBport, localPeer.get_ip());
		new Thread(mdbThread).start();
		//	new Thread(mdrThread).start();

		Messenger messenger = new Messenger(socket, localPeer, InetAddress.getByName(defaultServer));
		new Thread(messenger).start();
	}

	public static Peer getLocalPeer() {
		return localPeer;
	}

	// Function for getting the local peer's address
	public static InetAddress getPeerAddr() throws IOException {
		MulticastSocket socket = new MulticastSocket();
		socket.setTimeToLive(0);

		InetAddress addr = InetAddress.getByName("224.0.0.0");
		socket.joinGroup(addr);

		byte[] bytes = new byte[0];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr,
				socket.getLocalPort());

		socket.send(packet);
		socket.receive(packet);

		socket.close();

		return packet.getAddress();
	}

	public static MulticastSocket getSocket() {
		return socket;
	}

}
