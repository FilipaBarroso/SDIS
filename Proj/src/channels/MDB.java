package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Protocol.Protocol;
import service.Peer;
import service.PeerService;

public class MDB extends MulticastChannel implements Runnable {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress localPeerIP;

	public MDB(InetAddress ip, int port, InetAddress localPeerIP) throws IOException {
		super(ip, port);

		this.localPeerIP = localPeerIP;
		buffer = new byte[1024];
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
	}

	public void run() {
		System.out.println("MDB: THREAD BOOTED IN " + getIp() + ":" + getPort());

		while(true) {
			try {
				// ignorar packets que nao sejam PUTCHUNK
				socket.receive(packet);
				// Protocol.decypherMsg(packet.getData());
				
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


}
