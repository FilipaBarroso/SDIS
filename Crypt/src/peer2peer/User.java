package peer2peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * connect to multicast channel thread
 * ask for user input regarding transactions, checking wallet balance etc..
 */
public class User implements Runnable{

	public Wallet user_wallet;
	public String user_name;

	private static BufferedReader cin;

	public User(String user_name) {
		this.user_name = user_name;
		cin = new BufferedReader(new InputStreamReader(System.in));
	}

	public void addWallet(Wallet userwallet) {
		this.user_wallet = userwallet;
	}

	@Override
	public void run() {
		handleLogin();
		System.out.println("\n" + user_name + " logged in");

		while(true) {
			try {
				System.out.println("\nSend funds(1)\nCheck balance(2)");

				String s;
				s = (String)cin.readLine();

				if(s.equals("1")) handleSendFunds();
				else if(s.equals("2")) handleCheckBalance();
				else System.out.println("\nERROR: Wrong input");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void handleLogin() throws Exception {
		String s = "SEND "
				+ key + " "
				+ value + " "
				+ user_wallet.getPublicKeyString();

		byte[] msg = s.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
		MulticastSocket socket = new MulticastSocket(8001);
		socket.send(packet);
		socket.close();
	}

	public void handleSendFunds() {
		try {
			System.out.println("\nSend to user:");
			String username;
			PublicKey recipientPublicKey = null;
			username = (String)cin.readLine();

			for(Wallet w : Cryptocoin.wallets) {
				if(w.owner.user_name.equals(username)) {
					recipientPublicKey = w.publicKey;
					continue;
				}
			}
			if(recipientPublicKey == null) {
				System.out.println("ERROR: User doesn't exist");
				return;
			}

			System.out.println("How much:");
			float value;
			value = Float.parseFloat((String)cin.readLine());

			// TODO send packet to the server with the message SEND PUBLIC_KEY VALUE USER_WALLET
			sendMsg(Cryptocoin.getKeyAsString(recipientPublicKey), value);

			//Transaction t = user_wallet.sendFunds(recipient, value);
			//Chain.currentBlock.addTransaction(t);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(String key, Float value) throws Exception {
		String s = "SEND "
				+ key + " "
				+ value + " "
				+ user_wallet.getPublicKeyString();

		byte[] msg = s.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
		MulticastSocket socket = new MulticastSocket(8001);
		socket.send(packet);
		socket.close();

		Thread.sleep(2000);
	}

	public void handleCheckBalance() {
		float balance = user_wallet.getBalance();
		System.out.println("\nThis user has " + balance + " coins");
	}
}
