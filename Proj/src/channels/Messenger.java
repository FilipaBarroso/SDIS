package channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import service.Peer;
import service.PeerService;

/*
 * Formats and sends messages
 */
public class Messenger {

	private byte[] buffer;
	private BufferedReader cin;

	private MulticastSocket socket;
	private Peer localPeer;
	private InetAddress server;

	// TODO call this with all channel addrs and ports
	public Messenger(MulticastSocket socket, Peer localPeer, InetAddress server) throws Exception {
		buffer = new byte[1024];
		cin = new BufferedReader(new InputStreamReader(System.in));

		this.socket = socket;
		this.server = server;

		System.out.println("MESSENGER: LOGIN FROM PEER " + localPeer.get_ip() + " : " + localPeer.get_port() + "\n");

		openDialogue();
	}

	public void openDialogue() throws Exception {
		while(true) { try {

			String s = (String)cin.readLine();
			byte[] msg_data = s.getBytes();
			sendMessage(msg_data);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	// por agora assumir q so ha um canal
	public void sendMessage(byte[] msg) throws Exception {
		DatagramPacket mc_packet = new DatagramPacket(msg, msg.length, server, PeerService.default_MCport);
		socket.send(mc_packet);

		openDialogue();
	}
}
