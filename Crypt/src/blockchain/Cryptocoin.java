package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Random;
import java.security.Security;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.HashMap;
import org.bouncycastle.*;
import com.google.gson.GsonBuilder;

import peer2peer.*;

public class Cryptocoin {

	private static Chain blockchain;
	public static int miningDifficulty = 4;
	public static int minimunTransactionAmount = 1;

	//list of all unspent transactions outputs TODO save this in a database
	//public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	//public static ArrayList<Wallet> wallets = new ArrayList<Wallet>();

	private static Database database = new Database();

	// server related variables
	public static InetAddress server_address;
	public static String server_name = "225.0.0.0";
	public static int server_port = 8000;
	private static Server server;

	public static void main(String args[]) throws Exception {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// load database

		server_address = InetAddress.getByName(server_name);

		/* ------------------------------------------------*/

		if(args.length < 1) {
			blockchain = new Chain();
			server = new Server(server_address, server_port);

			Wallet test_wallet = new Wallet("test");
			new Thread(server).start();
		}

		else if(args.length == 1) {
			User u = new User(args[0]);

			new Thread(u).start();
		}


		/* debugging, without the if conditions */
		/*
		Wallet walletA = new Wallet("userA");
		Wallet walletB = new Wallet("userB");

		blockchain = new Chain();

		blockchain.currentBlock.addTransaction(Chain.bank.sendFunds(walletA.publicKey, 100f));
		System.out.println("userA's balance is: " + walletA.getBalance());
		System.out.println("userB's balance is: " + walletB.getBalance());
		System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
		blockchain.currentBlock.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		System.out.println("\nuserA's balance is: " + walletA.getBalance());
		System.out.println("userB's balance is: " + walletB.getBalance());

		System.out.println("\nuserA is attempting to send more funds (1000) than it has...");
		blockchain.currentBlock.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		System.out.println("\nuserA's balance is: " + walletA.getBalance());
		System.out.println("userB's balance is: " + walletB.getBalance());

		System.out.println("\nuserB is attempting to send funds (20) to WalletA...");
		blockchain.currentBlock.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nuserA's balance is: " + walletA.getBalance());
		System.out.println("userB's balance is: " + walletB.getBalance());
		*/
	}

	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();

		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.id);
		}

		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(sha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";

		return merkleRoot;
	}

	public static String sha256(String base) {
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	public static boolean verifyECDSA(PublicKey senderKey, String data, byte[] sig) {
		boolean ret = false;

		try {
			Signature ecdsa = Signature.getInstance("ECDSA", "BC");
			ecdsa.initVerify(senderKey);
			ecdsa.update(data.getBytes());

			ret = ecdsa.verify(sig);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static Chain getBlockchain() {
		return blockchain;
	}

	public static String getKeyAsString(Object obj) {
		PublicKey tmpPubKey;
		PrivateKey tmpPrivKey;

		if(obj instanceof PublicKey) {
			tmpPubKey = (PublicKey) obj;
			return Base64.getEncoder().encodeToString(tmpPubKey.getEncoded());
		}
		else if(obj instanceof PrivateKey) {
			tmpPrivKey =  (PrivateKey) obj;
			return Base64.getEncoder().encodeToString(tmpPrivKey.getEncoded());
		}

		return null;
	}

	public static Database getDatabase() {
		return database;
	}

	public static void addUTXOtoDB(String s, TransactionOutput t){
		database.addUTXOs(s, t);
	}

	public static void removeUTXOfromDB(String s){
		database.removeUTXO(s);
	}

	public static void addWallettoDB(Wallet w){
		database.addWallet(w);
	}
}
