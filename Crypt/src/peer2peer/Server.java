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
		System.out.println("Server online ...");

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
		boolean recipient_exists = false, has_funds = true;
		String s = new String(msg.getData(), 0, msg.getLength());
		msgTokens = s.split("\\s+");

		System.out.println("\nReceived msg: " + s);

		/* SEND FUNDS */
		if(msgTokens[0].equals("SEND")) {
			String b;
			String recipient_username = msgTokens[1];
			Float value = Float.parseFloat(msgTokens[2]);
			String sender_username = msgTokens[3];
			Wallet sender = null;
			PublicKey recipientPublicKey = null;

			for(Wallet w : Cryptocoin.getDatabase().getWallets()) {
				if(w.owner.username.equals(sender_username)) {
					sender = w;
					break;
				}
			}

			for(Wallet w : Cryptocoin.getDatabase().getWallets()) {
				if(w.owner.username.equals(recipient_username)) {
					recipientPublicKey = w.publicKey;
					recipient_exists = true;
					break;
				}
			}
			try {
				if(!recipient_exists) {
					b = "R";
					System.out.println(recipient_username + " does not exist");
					byte[] buf = b.getBytes();
					DatagramPacket new_packet = new DatagramPacket(buf, buf.length, Cryptocoin.server_address, msg.getPort());
					socket.send(new_packet);
				}
				else {
					if(sender.getBalance() < value) {
						System.out.println("Insufficient funds");
						b = "IF";
					}
					else {
						Transaction t = sender.sendFunds(recipientPublicKey, value);
						Cryptocoin.getBlockchain().currentBlock.addTransaction(t);
						b = Float.toString(sender.getBalance());
					}

					byte[] buf = b.getBytes();
					DatagramPacket new_packet = new DatagramPacket(buf, buf.length, Cryptocoin.server_address, msg.getPort());
					socket.send(new_packet);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* USER LOGIN */
		else if(msgTokens[0].equals("LOGIN")) {
			boolean user_exists = false;
			for(Wallet w : Cryptocoin.getDatabase().getWallets()) {
				if(w.owner.username.equals(msgTokens[1])) {
					user_exists = true;
					System.out.println("Returning user with port " + w.owner.user_port);
					break;
				}
			}

			if(!user_exists) {
				Cryptocoin.getDatabase().updateUserPort(msg.getPort()+1);
				Wallet new_w = new Wallet(msgTokens[1], msg.getPort());
				Cryptocoin.getBlockchain().currentBlock.addTransaction(Cryptocoin.getBlockchain().bank.sendFunds(new_w.publicKey, 100f));
				Cryptocoin.addWallettoDB(new_w);
			}
		}

		/* CHECK BALANCE */
		else if(msgTokens[0].equals("BALANCE")) {
			for(Wallet w : Cryptocoin.getDatabase().getWallets()) {
				if(w.owner.username.equals(msgTokens[1])) {
					String b = Float.toString(w.getBalance());
					byte[] buf = b.getBytes();

					try {
						int user_port = msg.getPort();
						System.out.println("Sending to " + user_port + " balance = " + b);
						DatagramPacket new_packet = new DatagramPacket(buf, buf.length, Cryptocoin.server_address, user_port);
						socket.send(new_packet);

					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		else {
			System.out.println("ERROR: Received a faulty message");
			return;
		}

	}
}
