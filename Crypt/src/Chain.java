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
		Block genesis_block = new Block(new Transaction(), "0");
		blockchain.add(genesis_block);
	}

	public static ArrayList<Block> getChain() {
		return blockchain;
	}

	public static void setChain(ArrayList<Block> blockchain) {
		Chain.blockchain = blockchain;
	}

	public void add(Block b) {
		Cryptocoin.mineBlock(b);
		
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
		
		for(i=1; i < blockchain.size(); i++) {
			curr = blockchain.get(i);
			prev = blockchain.get(i-1);
			
			if(!curr.hash.equals(curr.calculateHash())) {
				System.out.println("ERRROR: this block is defective - " + curr.hash);
				return false;
			}
			
			if(!prev.hash.equals(curr.prevHash)) {
				System.out.println("ERROR: this block doesn't match with the rest of the chain - " + curr.hash);
				return false;
			}
			
			if(!curr.hash.substring(0, Cryptocoin.miningDifficulty).equals(targetHash)) {
				System.out.println("ERROR: there's a block that hasn't been mined yet");
			}
		}
		
		return true;
	}
}
