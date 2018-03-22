package channels;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MulticastChannel {

	private InetAddress ip;
	private int port;

	public MulticastChannel(InetAddress ip, int port) {
		this.setIp(ip);
		this.setPort(port);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	// SHA256 hash function
	public static final String sha256(String str) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");

			byte[] hash = sha.digest(str.getBytes(StandardCharsets.UTF_8));

			StringBuffer hexStringBuffer = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);

				if (hex.length() == 1)
					hexStringBuffer.append('0');

				hexStringBuffer.append(hex);
			}

			return hexStringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
