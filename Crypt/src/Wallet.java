import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;
	public float funds;
	public String signature;
	
	public Wallet() {
		// Public Key, Private Key (used to make a signature), Funds
		generateKeys();
		
		System.out.println(this.toString() + " private key: " + getPrivateKeyString());
		System.out.println(this.toString() + " public key: " + getPublicKeyString());
	}
	
	public String getPrivateKeyString() {
		return Cryptocoin.getKeyAsString(privateKey);
	}
	
	public String getPublicKeyString() {
		return Cryptocoin.getKeyAsString(publicKey);
	}
	
	public void generateKeys() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			keyGen.initialize(ecSpec, random);   
	    	KeyPair keyPair = keyGen.generateKeyPair();

	    	privateKey = keyPair.getPrivate();
	    	publicKey = keyPair.getPublic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
