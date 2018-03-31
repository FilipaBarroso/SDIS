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
	private static boolean expectingChunks;
	
	private volatile static ArrayList<ChunkKey> chunkConfs;
	
	public MDR(InetAddress ip, int port, InetAddress localPeerIP) throws Exception {
		super(ip, port);

		this.buffer = new byte[Protocol.MAX_BUFFER];
		this.localPeerIP = localPeerIP;
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
		
		expectingChunks = false;
		chunkConfs = new ArrayList<ChunkKey>();
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
			
			decypherMsg(packet, sender);
		}
	}
	
	public static boolean isExpectingChunks() {return expectingChunks;}
	
	public static void doneExpectingChunks() {
		expectingChunks = false;
	}
	
	public static void expectChunks() {
		expectingChunks = true;
	}
	
	public static void addChunkConfirm(ChunkKey ck) {
		if(!hasChunkConf(ck))
			chunkConfs.add(ck);
	}
	
	public static boolean hasChunkConf(ChunkKey ck) {
		for(ChunkKey tmp : chunkConfs) {
			if(tmp.equals(ck)) return true;
		}
		
		return false;
	}
	
	public static void deleteChunkConf(ChunkKey ck) {
		chunkConfs.remove(ck);
	}

	public static int numChunkConfirmsForFile(String fileID) {
		int counter=0;
		
		for(ChunkKey tmp : chunkConfs) {
			if(tmp.getFileID().equals(fileID))
				counter++;
		}
		
		return counter;
	}
}
