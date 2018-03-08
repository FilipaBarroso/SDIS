package channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import service.PeerService;

/*
 * Formats and sends messages
 */
public class Messenger {

	private byte[] buffer;
	private BufferedReader cin;

	private InetAddress server_group;
	private MulticastSocket mc_socket;

	// TODO call this with all channel addrs and ports
	public Messenger() throws Exception {
		buffer = new byte[1024];
		cin = new BufferedReader(new InputStreamReader(System.in));

		server_group = InetAddress.getByName(PeerService.defaultServer);

		mc_socket = new MulticastSocket(PeerService.default_MCport);
		mc_socket.joinGroup(server_group);

		System.out.println("MESSENGER CONNECTED INTO " + server_group + ":" + PeerService.default_MCport);
		openDialogue();
	}

	public void openDialogue() throws Exception {
		while(true) { try {
			System.out.println("Send a msg to peers:\n");

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
		DatagramPacket packet = new DatagramPacket(msg, msg.length, server_group, PeerService.default_MCport);
		mc_socket.send(packet);

		openDialogue();
	}
}
