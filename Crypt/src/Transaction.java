import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Random;

public class Transaction {

	public String transID;
	public PublicKey sender, recipient;
	public float value;
	public byte[] signature;

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int transaction_counter = 0;

	public Transaction() {
		//empty transaction
	}

	public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs) {
		this.sender = sender;
		this.recipient = recipient;
		this.value = value;
		this.inputs = inputs;

		generateTransID();
	}

	public void generateTransID() {
		// avoiding equal transactions from having the same ID
		transaction_counter++;

		transID = Cryptocoin.sha256(sender.getEncoded().toString()
				+ recipient.getEncoded().toString()
				+ Float.toString(value)
				+ transaction_counter);
	}

	public void generateSignature(PrivateKey privKey) throws Exception {
		Signature dsa;
		String data = sender.getEncoded().toString() + recipient.getEncoded().toString() + Float.toString(value);

		dsa = Signature.getInstance("ECDSA", "BC");
		dsa.initSign(privKey);

		byte[] strBytes = data.getBytes();
		dsa.update(strBytes);

		byte[] tmp_sig = dsa.sign();
		signature = tmp_sig;
	}
	
	public boolean verifySignature() {
		String data = sender.getEncoded().toString() + recipient.getEncoded().toString() + Float.toString(value);
		
		return Cryptocoin.verifyECDSA(sender, data, signature);		
	}
}
