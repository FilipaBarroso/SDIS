import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.security.*;
import java.security.spec.*;

public class Wallet {

	public PrivateKey privKey;
	public PublicKey pubKey;
	public float funds;
	public String signature;
	
	public Wallet() {
		// Public Key, Private Key (used to make a signature), Funds
		generateKeys();
	}
	
	public String getPrivateKeyString() {
		return privKey.getEncoded().toString();
	}
	
	public String getPublicKeyString() {
		return pubKey.getEncoded().toString();
	}
	
	public void generateKeys() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			keyGen.initialize(ecSpec, random);   
	    	KeyPair keyPair = keyGen.generateKeyPair();

	    	privKey = keyPair.getPrivate();
	    	pubKey = keyPair.getPublic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
