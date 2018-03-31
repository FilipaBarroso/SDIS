package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

import data.Files;
import database.ChunkKey;
import protocol.Protocol;
import service.Chunk;
import service.Peer;
import service.PeerService;

/*
 * Collects STORED messages
 */
public class MC extends MulticastChannel implements Runnable {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress localPeerIP;
	
	private volatile static HashMap<ChunkKey, ArrayList<Peer>> storedConfs;

	public MC(InetAddress ip, int port, InetAddress localPeerIP) throws IOException {
		super(ip, port);

		this.buffer = new byte[Protocol.MAX_BUFFER];
		this.localPeerIP = localPeerIP;
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
		
		storedConfs = new HashMap<ChunkKey, ArrayList<Peer>>();
	}

	public void run() {
		System.out.println("MC: THREAD BOOTED IN " + getIp() + ":" + getPort());
		
		while(true) {
			try {
				socket.receive(packet);
				
				Peer sender = new Peer(packet.getAddress(), packet.getPort());
				
				// debbugging
				//System.out.println("MC: sender\t" + sender.get_ip() + " : " + sender.get_port());
				//System.out.println("MC: local\t" + PeerService.getLocalPeer().get_ip() + " : " + PeerService.getLocalPeer().get_port());
				
				if(sender.equals(PeerService.getLocalPeer())) continue;
				
				decypherMsg(packet, sender);
				
				/* debugging
				String s = new String(buffer, 0, packet.getLength());
				System.out.println("\nMC:"+ s + "\n");
				*/
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void readyStoredConfirms(ChunkKey ck) {
		if(!storedConfs.containsKey(ck))
			storedConfs.put(ck, new ArrayList<Peer>());
		
		else storedConfs.get(ck).clear();
	}
	
	public static void addStoredConfirm(ChunkKey ck, Peer p) {
		if(!storedConfs.containsKey(ck))
			storedConfs.put(ck, new ArrayList<Peer>());
		
		if(!storedConfs.get(ck).contains(p))
			storedConfs.get(ck).add(p);
	}
	
	public static int getNumStoredConfs(ChunkKey ck) {
		if(!storedConfs.containsKey(ck)) return 0;
		
		return storedConfs.get(ck).size();
	}
	
	public static void deleteStoredConfs(ChunkKey ck) {
		storedConfs.remove(ck);
	}	
}
