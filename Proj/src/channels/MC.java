package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import service.Peer;
import service.PeerService;

/*
 * Stores info related to the control channel
 */
public class MC extends MulticastChannel implements Runnable {

	private MulticastSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	private InetAddress localPeerIP;

	public MC(InetAddress ip, int port, InetAddress localPeerIP) throws IOException {
		super(ip, port);

		this.localPeerIP = localPeerIP;
		buffer = new byte[1024];
		socket = new MulticastSocket(port);
		socket.joinGroup(getIp());
		packet = new DatagramPacket(buffer, buffer.length);
	}

	public void run() {
		System.out.println("MC: THREAD BOOTED IN " + getIp() + ":" + getPort());
		
		while(true) {
			try {
				// ignorar packets que nao sejam STORED e GETCHUNK
				socket.receive(packet);
				// Protocol.decypherMsg
				
				Peer sender = new Peer(packet.getAddress(), packet.getPort());
				
				// prints for debugging purposes
				//System.out.println("MC: sender\t" + sender.get_ip() + " : " + sender.get_port());
				//System.out.println("MC: local\t" + PeerService.getLocalPeer().get_ip() + " : " + PeerService.getLocalPeer().get_port());
				
				if(sender.same(PeerService.getLocalPeer())) continue;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			buffer = packet.getData();
			String s = new String(buffer, 0, packet.getLength());

			System.out.println("\nMC:"+ s + "\n");
		}
	}
}
