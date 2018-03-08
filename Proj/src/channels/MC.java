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

	public MC(InetAddress ip, int port) throws IOException {
		super(ip, port);

		buffer = new byte[1024];
		socket = new MulticastSocket(port);
		packet = new DatagramPacket(buffer, buffer.length);
	}

	public void run() {
		System.out.println("MC THREAD BOOTED IN " + getIp() + ":" + getPort() + "\n");

		while(true) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buffer = packet.getData();
			String s = new String(buffer, 0, packet.getLength());

			System.out.println("Received msg from peer: "+ s);
		}
	}
}
