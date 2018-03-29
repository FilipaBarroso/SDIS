package channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import protocol.Protocol;
import service.Peer;
import service.PeerService;

/*
 * Formats and sends messages
 */
public class Messenger implements Runnable {

	private static byte[] buffer;
	private static BufferedReader cin;
	private static MulticastSocket socket;
	private static Peer localPeer;
	private static InetAddress server;

	// TODO call this with all channel addrs and ports
	public Messenger(MulticastSocket socket, Peer localPeer, InetAddress server) throws Exception {
		buffer = new byte[1024];
		cin = new BufferedReader(new InputStreamReader(System.in));

		this.socket = socket;
		this.server = server;

		System.out.println("MESSENGER: Login from peer " + localPeer.get_ip() + ":" + localPeer.get_port());
		System.out.println("MESSENGER: Valid Operations: backup & restore");
	}

	public void run() {
		while(true) { try {

			String s = (String)cin.readLine();
			byte[] msg_data = s.getBytes();
			
			// read the console and apply the desired protocol
			int c = decypherConsole(s);
			switch(c) {
			case 1:
				handleBackupMsg();
				break;
			case 2:
				handleRestoreMsg();
				break;
			default:
				break;
			}
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public static void handleBackupMsg() {
		try {
			String filename;
			int repD;
		
			System.out.println("MESSENGER: Specify backup <filename> <replicationDegree>");
			String s = (String)cin.readLine();
			String[] tokens = s.split("\\s+");
			
			filename = tokens[0];
			repD = Integer.parseInt(tokens[1]);
			
			Protocol.initiateBackup(filename, repD);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void handleRestoreMsg() {
		try {
			String filename;
			int repD;
		
			System.out.println("MESSENGER: Specify restore <filename>");
			String s = (String)cin.readLine();
			String[] tokens = s.split("\\s+");
			
			filename = tokens[0];
			repD = Integer.parseInt(tokens[1]);
			
			Protocol.initiateBackup(filename, repD);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int decypherConsole(String s) {
		if(s.equals("backup")) return 1;
		if(s.equals("restore")) return 2;
		if(s.equals("delete")) return 3;
		
		
		return 0;
	}
	
	public static void sendToMC(byte[] msg) throws Exception {
		DatagramPacket mc_packet = new DatagramPacket(msg, msg.length, server, PeerService.default_MCport);
		socket.send(mc_packet);
	}
	
	public static void sendToMDB(byte[] msg) throws Exception {
		DatagramPacket mdb_packet = new DatagramPacket(msg, msg.length, server, PeerService.default_MDBport);
		socket.send(mdb_packet);
	}
	
	public static void sendToMDR(byte[] msg) throws Exception {
		DatagramPacket mdr_packet = new DatagramPacket(msg, msg.length, server, PeerService.default_MDRport);
		socket.send(mdr_packet);
	}
}
