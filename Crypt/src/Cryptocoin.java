import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Random;

public class Cryptocoin {
	
	private static Chain blockchain;
	public static int miningDifficulty = 4;

	public static ArrayList<Wallet> wallets;
	
	public static void main(String args[]) throws Exception {
		blockchain = new Chain();

		// create some wallets
		Wallet walletA = new Wallet();
		wallets.add(walletA);
		Wallet walletB = new Wallet();
		wallets.add(walletB);
		
		//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(walletA.getPrivateKeyString());
		System.out.println(walletA.getPublicKeyString());
		
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.pubKey, walletB.pubKey, 5, null);
		transaction.generateSignature(walletA.privKey);
		
		//Verify the signature works and verify it from the public key
		System.out.print("Is signature verified: ");
		System.out.println(transaction.verifySignature());
	}
	
	public static void mineBlock(Block b) {
		String target = new String(new char[miningDifficulty]).replace('\0', '0');
		
		while(!b.hash.substring(0, miningDifficulty).equals(target)) {
			b.nonce++;
			b.hash = b.calculateHash();
			
			// temp
			if(b.hasBeenMined()) break;
		}
		
		System.out.println("Block mined - " + b.hash);
		b.setAsMined();
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
}