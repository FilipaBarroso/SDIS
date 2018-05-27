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

		public User(String username) {
			this.username = username;
			cin = new BufferedReader(new InputStreamReader(System.in));
			this.buffer = new byte[MAX_BUFFER];

			try {
				socket = new MulticastSocket(8001);
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
			socket.close();
			}
			catch (Exception e) {

			}
		}

		public void handleSendFunds() {
			try {
				System.out.println("\nSend to user:");
				String username;
				PublicKey recipientPublicKey = null;
				username = (String)cin.readLine();

				for(Wallet w : Cryptocoin.wallets) {
					if(w.owner.username.equals(username)) {
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
			socket.close();

			Thread.sleep(2000);
		}

		public void handleCheckBalance() {
			try {
			String s = "BALANCE "
								+ username;

			byte[] msg = s.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length, Cryptocoin.server_address, Cryptocoin.server_port);
			socket.send(packet);

			Thread.sleep(2000);

				socket.receive(packet);
				String balance = new String(packet.getData(), 0, packet.getLength());
				System.out.println("\nThis user has " + balance + " coins");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
