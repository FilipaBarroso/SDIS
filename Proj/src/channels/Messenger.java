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

	public Messenger(MulticastSocket socket, Peer localPeer, InetAddress server) throws Exception {
		buffer = new byte[1024];
		cin = new BufferedReader(new InputStreamReader(System.in));

		this.socket = socket;
		this.server = server;

		System.out.println("MESSENGER: Login from peer " + localPeer.get_ip() + ":" + localPeer.get_port());
		System.out.println("MESSENGER: You can choose to backup, restore or delete a file");
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
			case 3:
				handleDeleteMsg();
				break;
			default:
				System.out.println("MESSENGER: The protocol '" + s + "' doesn't exist");
				break;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	public static void handleBackupMsg() {
		try {
			String filename;
			int repD;
			String[] tokens;
			
			do {
			System.out.println("MESSENGER: Specify backup protocol <filename> <replicationDegree>");
			String s = (String)cin.readLine();
			tokens = s.split("\\s+");
			}	while(tokens.length != 2);
			
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
			String[] tokens;
			
			do {
			System.out.println("MESSENGER: Specify restore protocol <filename>");
			String s = (String)cin.readLine();
			tokens = s.split("\\s+");
			}	while(tokens.length != 1);			
			
			filename = tokens[0];
			
			Protocol.initiateRestore(filename);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void handleDeleteMsg() {
		try {
			String filename;
			int repD;
			String[] tokens;
			
			do {
			System.out.println("MESSENGER: Specify delete protocol <filename>");
			String s = (String)cin.readLine();
			tokens = s.split("\\s+");
			}	while(tokens.length != 1);			
			
			filename = tokens[0];
			
			Protocol.initiateDelete(filename);
			
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
