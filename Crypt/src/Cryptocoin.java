import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

public class Cryptocoin {
	
	private static Chain blockchain;
	public static int miningDifficulty = 4;
	
	public static void main(String args[]) throws Exception {
		blockchain = new Chain();
		
		// remember to block.mineblock() after adding one to the chain
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
}