import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.*;

public class Chain {
	
	private static ArrayList<Block> blockchain;
	public Transaction genesisTransaction;
	public Block genesis_block;
	public Wallet bank;
	
	public Chain() {
		Chain.setChain(new ArrayList<Block>());
		
		// adds an empty block so the chain isn't empty
		genesis_block = new Block("0");

		bank = new Wallet();
		Wallet genesisWallet = new Wallet();
		
		// create genesis transaction, which fills the bank up 
		genesisTransaction = new Transaction(genesisWallet.publicKey, bank.publicKey, 100000000f, null);
		genesisTransaction.generateSignature(genesisWallet.privateKey);	 //manually sign the genesis transaction	
		genesisTransaction.id = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.id)); //manually add the Transactions Output
		Cryptocoin.UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

		genesis_block.addTransaction(genesisTransaction);
		blockchain.add(genesis_block);
	}

	public static ArrayList<Block> getChain() {
		return blockchain;
	}

	public static void setChain(ArrayList<Block> blockchain) {
		Chain.blockchain = blockchain;
	}

	public void add(Block b) {
		b.mineBlock();
		
		Chain.blockchain.add(b);
		if(!isChainValid()) {
			System.out.println("ERROR: chain invalid\n");
			blockchain.remove(b);
		}
	}

	public void printChain() {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
	}

	public String getLastHash() { 
		return blockchain.get(blockchain.size()).hash;
	}
	
	public boolean isChainValid() { 
		Block curr, prev;
		int i;
		String targetHash = new String(new char[Cryptocoin.miningDifficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		for(i=1; i < blockchain.size(); i++) {
			curr = blockchain.get(i);
			prev = blockchain.get(i-1);
			
			// validate hashes
			if(!curr.hash.equals(curr.calculateHash())) {
				System.out.println("CHAIN ERROR: this block is defective - " + curr.hash);
				return false;
			}
			
			if(!prev.hash.equals(curr.previousHash)) {
				System.out.println("CHAIN ERROR: this block doesn't match with the rest of the chain - " + curr.hash);
				return false;
			}
			
			if(!curr.hash.substring(0, Cryptocoin.miningDifficulty).equals(targetHash)) {
				System.out.println("CHAIN ERROR: there's a block that hasn't been mined yet");
			}
			
			// validate transactions
			TransactionOutput tempOutput;
			for(int t=0; t < curr.transaction_list.size(); t++) {
				Transaction currTransaction = curr.transaction_list.get(t);
				
				if(!currTransaction.verifySignature()) {
					System.out.println("CHAIN ERROR: couldn't verify a signature for a transaction in this block");
					return false;
				}
				
				if(currTransaction.getTotalInputs() != currTransaction.getTotalOutputs()) {
					System.out.println("CHAIN ERROR: inputs don't equal outputs for a transaction in this block");
					return false;
				}
				
				for(TransactionInput ti : currTransaction.inputs) {
					tempOutput = tempUTXOs.get(ti.transOutputID);
					
					if(tempOutput == null) {
						System.out.println("CHAIN ERROR: input is missing on a transaction");
						return false;
					}
					
					if(ti.UTXO.value != tempOutput.value) {
						System.out.println("CHAIN ERROR: value is invalid on a transaction");
						return false;
					}
					
					tempUTXOs.remove(ti.transOutputID);
				}
				
				for(TransactionOutput to : currTransaction.outputs) {
					tempUTXOs.put(to.id, to);
				}
				
				if(currTransaction.outputs.get(0).recipient != currTransaction.recipient) {
					System.out.println("CHAIN ERROR: recipient mismatch on a transaction");
					return false;
				}
				
				if(currTransaction.outputs.get(1).recipient != currTransaction.sender) {
					System.out.println("CHAIN ERROR: sender mismatch on a transaction");
					return false;
				}
			}
		}
		
		System.out.println("Chain is valid");
		
		return true;
	}
}