import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

public class Block {
	
	private Transaction data;
	public String hash;
	public String prevHash;
	private long timestamp;
	public int nonce = 0;
	private boolean hasBeenMined = false;
	
	public Block(Transaction data, String prevHash) {
		this.setData(data);
		this.prevHash = prevHash;
		this.setTimestamp(new Date().getTime());
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		return Cryptocoin.sha256(
				prevHash +
				Long.toString(timestamp) +
				Integer.toString(nonce) +
				data.toString());
	}


	
	public long getTimestamp() {return timestamp;}

	
	public void setTimestamp(long timestamp) {this.timestamp = timestamp;}

	public Transaction getData() {return data;}

	public void setData(Transaction data) {this.data = data;}

	public boolean hasBeenMined() {
		return hasBeenMined;
	}

	public void setAsMined() {
		this.hasBeenMined = true;
	}
}