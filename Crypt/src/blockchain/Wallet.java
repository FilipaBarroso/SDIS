package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import peer2peer.User;

import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;
	public User owner;
	
	// contains all unspent outputs from this wallet aka its balance
	public HashMap<String,TransactionOutput> WalletUTXOs = new HashMap<String,TransactionOutput>();
	
	public Wallet() {
		generateKeys();
		
		// debugging
		//System.out.println(this.toString() + " private key: " + getPrivateKeyString());
		//System.out.println(this.toString() + " public key: " + getPublicKeyString());
	}
	
	public boolean belongsTo(String user_name) {
		return owner.user_name.equals(user_name);
	}

	public void mine(Block b) {
		b.mineBlock(this);
	}
	
	public Transaction sendFunds(PublicKey recipient, float value) {
		if(getBalance() < value) {
			System.out.println("WALLET ERROR: insuficient funds");
			return null;
		}
		
		// make the transaction inputs list aka the spent outputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		float total = 0;
		for(Map.Entry<String, TransactionOutput> item : WalletUTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));

			if(total > value) break; // no need to include unnecessary inputs
		}

		Transaction newTrans = new Transaction(publicKey, recipient, value, inputs);
		newTrans.generateSignature(privateKey);
		
		// remove spent outputs from unspent outputs
		for(TransactionInput ti : inputs) WalletUTXOs.remove(ti.transOutputID);
		
		return newTrans;
	}
	
	public float getBalance() {
		float total = 0;
		
		for(Map.Entry<String, TransactionOutput> item : Cryptocoin.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			// add unspent outputs to this wallet
			if(UTXO.belongsTo(publicKey)) {
				WalletUTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		
		return total;
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
