package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot;
	public ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();
	private long timestamp;
	public int nonce = 0;

	public Block(String prevHash) {
		this.previousHash = prevHash;
		this.setTimestamp(new Date().getTime());
		this.hash = calculateHash();
	}

	public String mineBlock() {
		merkleRoot = Cryptocoin.getMerkleRoot(transaction_list);
		String target = new String(new char[Cryptocoin.miningDifficulty]).replace('\0', '0');

		while(!hash.substring(0, Cryptocoin.miningDifficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}

		System.out.println("Block mined with the hash: " + hash + "\n");

		return hash;
	}

	public boolean addTransaction(Transaction trans) {
		if(trans == null) return false;

		//if(hash != "0")
			if(!trans.processTransaction())
				return false;

		transaction_list.add(trans);

		System.out.println("\nTransaction stored .. current block has " + transaction_list.size() + " transactions");

		// each block gets added to the chain to be mined after 3 transactions
		if(transaction_list.size() >= 3) {
			System.out.println("Adding current block to chain");
			Cryptocoin.getBlockchain().addCurrentBlock();
		}
		return true;
	}

	public String calculateHash() {
		return Cryptocoin.sha256(
				previousHash +
				Long.toString(timestamp) +
				Integer.toString(nonce) +
				merkleRoot
				);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
