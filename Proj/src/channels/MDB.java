package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import service.Chunk;
import service.Peer;
import service.PeerService;
import Protocol.MsgHandler;
import Protocol.Protocol;

public class MDB extends MulticastChannel implements Runnable {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress localPeerIP;

	public MDB(InetAddress ip, int port, InetAddress localPeerIP) throws IOException {
		super(ip, port);

		this.localPeerIP = localPeerIP;
		buffer = new byte[Protocol.MAX_BUFFER];
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
	}

	public void run() {
		System.out.println("MDB: THREAD BOOTED IN " + getIp() + ":" + getPort());

		while(true) {
			try {
				socket.receive(packet);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Peer sender = new Peer(packet.getAddress(), packet.getPort());
			if(sender.same(PeerService.getLocalPeer())) continue;
			
			buffer = packet.getData();
			String s = new String(buffer, 0, packet.getLength());

			System.out.println("\nMDB:"+ s + "\n");
		}
	}

	/*
	 * cria o chunk da msg e guarda-o na pasta CHUNKS\FILENAME
	 * verificar se a pasta ja existe
	 * guardar o chunk na Database 
	 */
	public static void handlePUTCHUNK(byte[] msg) {
		// ver se ainda nao existe na DB
		
		try {
			byte[] body = MsgHandler.extractBody(msg);
			String[] msg_tokens = MsgHandler.msgTokens;
			
			Chunk chunk = new Chunk(msg_tokens[3], Integer.parseInt(msg_tokens[4]), Integer.parseInt(msg_tokens[5]), body);
			
			Protocol.storeChunk(chunk);
			
			// Protocol.sendSTORED to MC
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
