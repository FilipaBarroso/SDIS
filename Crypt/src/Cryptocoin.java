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

		// create some wallets
		Wallet walletA = new Wallet();
		wallets.add(walletA);
		System.out.println();
		Wallet walletB = new Wallet();
		wallets.add(walletB);

		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);

		//Verify the signature works and verify it from the public key
		System.out.print("\nIs signature verified: ");
		System.out.println(transaction.verifySignature());
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