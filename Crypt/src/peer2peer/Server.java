package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

/*
 * multicast channel thread
 * handles user messages demanding transactions and sends them to Cryptocoin() to process them
 */
public class Server implements Runnable {

	public static int MAX_BUFFER = 65000;
	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress ip;
	private int port;

	public Server(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		this.buffer = new byte[MAX_BUFFER];

		try {
			socket = new MulticastSocket(port);
			socket.joinGroup(ip);
			packet = new DatagramPacket(buffer, buffer.length);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		System.out.println("Server online ..");

		while(true) {
			try {
				socket.receive(packet);
				decypherMsg(packet);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// message must include user's public wallet key, for identification
	// ex: SEND RECIPIENT_WALLET VALUE SENDER_WALLET
	public static void decypherMsg(DatagramPacket packet) {		
		
	}
}
