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

public class Cryptocoin {

	private static Chain blockchain;
	public static int miningDifficulty = 4;
	public static int minimunTransAmount = 1;

	//list of all unspent transactions outputs
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	public static ArrayList<Wallet> wallets = new ArrayList<Wallet>();

	public static void main(String args[]) throws Exception {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 

		blockchain = new Chain();

		/* debugging */
		Wallet walletA = new Wallet();
		wallets.add(walletA);
		Wallet walletB = new Wallet();
		wallets.add(walletB);

		Block block1 = new Block(blockchain.genesis_block.hash);
		block1.addTransaction(blockchain.bank.sendFunds(walletA.publicKey, 100f));
		System.out.println("WalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		blockchain.add(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA is attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		blockchain.add(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		blockchain.add(block3);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
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
}