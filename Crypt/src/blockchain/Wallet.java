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

	// this constructor should never be used
	/*
	public Wallet() {
		// generate hashes
		generateKeys();
		// add this wallet to the list of existing wallets
		Cryptocoin.wallets.add(this);
	}
	*/

	public Wallet(String username) {
		// generate hashes
		generateKeys();

		owner = new User(username);

		// add this wallet to the list of existing wallets
		Cryptocoin.addWallettoDB(this, owner);

		// add a base 100 coins to the wallet
		if(owner.username == "bank" || owner.username == "genesis") return;
		Cryptocoin.getBlockchain().currentBlock.addTransaction(Chain.bank.sendFunds(publicKey, 100f));
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return owner.username;
	}

	public boolean belongsTo(String user_name) {
		return owner.username.equals(user_name);
	}

	public void mine(Block b) {
		if(b.mineBlock(this))
			System.out.println(this.toString() + " got the mining reward of "+ Cryptocoin.getBlockchain().miningReward);
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

		for(Map.Entry<String, TransactionOutput> item : Cryptocoin.getDatabase().getUTXOs().entrySet()) {
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