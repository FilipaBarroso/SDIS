import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.*;

public class Chain {
	
	private static ArrayList<Block> blockchain;
	
	public Chain() {
		Chain.setChain(new ArrayList<Block>());
		
		// adds an empty block so the chain isn't empty
		Block genesis_block = new Block(new Data(), "0");
		blockchain.add(genesis_block);
	}

	public static ArrayList<Block> getChain() {
		return blockchain;
	}

	public static void setChain(ArrayList<Block> blockchain) {
		Chain.blockchain = blockchain;
	}

	public void add(Block b) {
		Chain.blockchain.add(b);
	}

	public void printChain() {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);		
		System.out.println(blockchainJson);
	}

	public String getLastHash() { 
		return blockchain.get(blockchain.size()).getHash();
	}
	
	// TODO
	public boolean verifyChain() { return true;}
}
