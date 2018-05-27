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

			if(msgTokens[0].equals("SEND")) {
				// TODO implement error messages
			String recipient_username = msgTokens[1];
			Float value = Float.parseFloat(msgTokens[2]);
			String sender_username = msgTokens[3];
			Wallet sender = null;
			PublicKey recipientPublicKey = null;

			for(Wallet w : Cryptocoin.wallets) {
				if(w.owner.username.equals(sender_username)) {
					sender = w;
					break;
				}
			}

			for(Wallet w : Cryptocoin.wallets) {
				if(w.owner.username.equals(recipient_username)) {
					recipientPublicKey = w.publicKey;
					break;
				}
			}

			Transaction t = sender.sendFunds(recipientPublicKey, value);
			Cryptocoin.getBlockchain().currentBlock.addTransaction(t);

			try {
				String b = Float.toString(sender.getBalance());
			byte[] buf = b.getBytes();
			int user_port = msg.getPort();
			DatagramPacket new_packet = new DatagramPacket(buf, buf.length, Cryptocoin.server_address, user_port);
			socket.send(new_packet);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		}

			else if(msgTokens[0].equals("LOGIN")) {
					Wallet new_wallet = new Wallet(msgTokens[1]);
					System.out.println(msgTokens[1] + " has logged in");
			}

			else if(msgTokens[0].equals("BALANCE")) {
				for(Wallet w : Cryptocoin.wallets) {
					if(w.owner.username.equals(msgTokens[1])) {
						String b = Float.toString(w.getBalance());
						byte[] buf = b.getBytes();

						try {
						int user_port = msg.getPort();
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
