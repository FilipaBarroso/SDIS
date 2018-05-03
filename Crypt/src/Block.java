import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

public class Block {
	
	private Data data;
	public String Hash;
	public String prevHash;
	private long timestamp;
	
	public Block(Data data, String prevHash) {
		this.setData(data);
		this.prevHash = prevHash;
		this.setTimestamp(new Date().getTime());
		this.Hash = calculateHash();
	}
	
	public String calculateHash() {
		return sha256(this.prevHash + this.timestamp + this.data.toString());
	}
	
	public static String sha256(String base) {
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	
	public long getTimestamp() {return timestamp;}

	
	public void setTimestamp(long timestamp) {this.timestamp = timestamp;}

	public Data getData() {return data;}

	public void setData(Data data) {this.data = data;}
	
	public String getHash() {return this.Hash;}
}