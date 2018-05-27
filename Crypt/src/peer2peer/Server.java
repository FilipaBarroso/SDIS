package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Random;

import blockchain.Chain;
import blockchain.Cryptocoin;
import blockchain.Transaction;
import blockchain.Wallet;

/*
 * multicast channel thread
 * handles user messages demanding transactions and sends them to Cryptocoin() to process them
 */
public class Server implements Runnable {

	public static int MAX_BUFFER = 65000;
	private MulticastSocket socket;
	private DatagramPacket packet;
	private String[] msgTokens;
	private byte[] buffer;
	private InetAddress ip;
	private int port;

	public Server(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		this.buffer = new byte[MAX_BUFFER];

		try {
			socket = new MulticastSocket(port);
			socket.joinGroup(ip);
			packet = new DatagramPacket(buffer, buffer.length);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		System.out.println("Server online ..");

		while(true) {
			try {
				socket.receive(packet);
				decypherMsg(packet);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ex: SEND RECIPIENT_PUBLICKEY_STRING VALUE USER_WALLET_PUBLICKEY_STRING
	public void decypherMsg(DatagramPacket msg) {
		String s = new String(msg.getData(), 0, msg.getLength());
		msgTokens = s.split("\\s+");

		System.out.println("Received msg: " + s);

		if(!msgTokens[0].equals("SEND") || msgTokens.length != 4) {
			System.out.println("ERROR: Received a faulty message");
			return;
		}

		String recipient_publicKey = msgTokens[1];
		Float value = Float.parseFloat(msgTokens[2]);
		String sender_publicKey = msgTokens[3];
		Wallet sender = null;
		PublicKey recipient = null;

		for(Wallet w : Cryptocoin.wallets) {
			if(w.getPublicKeyString().equals(sender_publicKey)) {
				sender = w;
				break;
			}
		}

		for(Wallet w : Cryptocoin.wallets) {
			if(w.getPublicKeyString().equals(recipient_publicKey)) {
				recipient = w.publicKey;
				break;
			}
		}

		Transaction t = sender.sendFunds(recipient, value);
		Cryptocoin.getBlockchain().currentBlock.addTransaction(t);
	}
}
