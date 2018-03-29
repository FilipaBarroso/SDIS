package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

import database.ChunkKey;
import protocol.Protocol;
import service.Peer;
import service.PeerService;

/*
 * Deals with CHUNK restore messages
 */
public class MDR extends MulticastChannel implements Runnable {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress localPeerIP;
	
	private volatile static HashMap<String, ChunkKey> chunkConfs;
	
	public MDR(InetAddress ip, int port, InetAddress localPeerIP) throws Exception {
		super(ip, port);
		

		this.localPeerIP = localPeerIP;
		buffer = new byte[Protocol.MAX_BUFFER];
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
		
		chunkConfs = new HashMap<String, ChunkKey>();
	}

	public void run() {
		System.out.println("MDR: THREAD BOOTED IN " + getIp() + ":" + getPort());

		while(true) {
			try {
				socket.receive(packet);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Peer sender = new Peer(packet.getAddress(), packet.getPort());
			if(sender.equals(PeerService.getLocalPeer())) continue;
			
			buffer = packet.getData();
			decypherMsg(buffer, sender);
			
			/*
			String s = new String(buffer, 0, packet.getLength());
			System.out.println("\nMDB:"+ s + "\n");
			*/
		}
	}
	
	public static void addChunkConfirm(String fileID, ChunkKey ck) {
		if(!chunkConfs.containsKey(fileID))
			chunkConfs.put(fileID, ck);
	}
	
	public static boolean hasChunkConf(ChunkKey ck) {
		if(!chunkConfs.containsKey(ck)) return false;
		
		return true;
	}
	
	public static void deleteChunkConfs(ChunkKey ck) {
		chunkConfs.remove(ck);
	}
}
