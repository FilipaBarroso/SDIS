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

		public String username;
		private static BufferedReader cin;
		private byte[] buffer;
		public static int MAX_BUFFER = 65000;
		private MulticastSocket socket;
		private DatagramPacket server_packet;
		private int user_port;

		public User(String username, int user_port) {
			this.user_port = user_port;
			System.out.println("User port " + user_port);
			this.username = username;
			cin = new BufferedReader(new InputStreamReader(System.in));
			this.buffer = new byte[MAX_BUFFER];

			try {
				socket = new MulticastSocket(user_port);
				socket.joinGroup(Cryptocoin.server_address);
				server_packet = new DatagramPacket(buffer, buffer.length);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			handleLogin();
			System.out.println("\n" + username + " logged in");

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

		public void handleLogin() {
			try {
			String s = "LOGIN "
					+ username;

			byte[] msg = s.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
			socket.send(packet);
			}
			catch (Exception e) {

			}
		}

		public void handleSendFunds() {
			try {
				System.out.println("\nSend to user:");
				String recipient_username;
				PublicKey recipientPublicKey = null;
				recipient_username = (String)cin.readLine();

				System.out.println("How much:");
				float value;
				value = Float.parseFloat((String)cin.readLine());

				sendMsg(recipient_username, value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// TODO should send SEND recipient_username value username
		// the server should do all the checking and reply accordingly
		public void sendMsg(String recipient_username, Float value) throws Exception {
			String s = "SEND "
					+ recipient_username + " "
					+ value + " "
					+ username;

			byte[] msg = s.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
			socket.send(packet);

			Thread.sleep(2000);

			// TODO interpret error messages

			socket.receive(server_packet);
			String balance = new String(server_packet.getData(), 0, server_packet.getLength());
			System.out.println("\n" + username + " has " + balance + " coins");
		}

		public void handleCheckBalance() {
			try {
			String s = "BALANCE "
								+ username;

			byte[] msg = s.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
			socket.send(packet);

			Thread.sleep(2000);

				socket.receive(server_packet);
				String balance = new String(server_packet.getData(), 0, server_packet.getLength());
				System.out.println("\n" + username + " has " + balance + " coins");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
