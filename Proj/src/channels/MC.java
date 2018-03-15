package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/*
 * Stores info related to the control channel
 * Calls a MsgHandler thread 
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
		System.out.println("MC THREAD BOOTED IN " + getIp() + ":" + getPort() + "\n");

		while(true) {
			try {
				System.out.println("MC: Reading packets\n");
				socket.receive(packet);
				System.out.println("MESSAGED BY: " + packet.getAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(packet.getAddress() == localPeerIP) {
				System.out.println("Testing: Rejected message coming from same IP");
				continue;
			}
			buffer = packet.getData();
			String s = new String(buffer, 0, packet.getLength());

			System.out.println("Received msg from peer: "+ s);
		}
	}
}
