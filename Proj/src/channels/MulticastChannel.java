package channels;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

import protocol.Protocol;
import service.Peer;

public abstract class MulticastChannel {

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


	public static String[] msgTokens;

	public static void decypherMsg(byte[] msg, Peer sender) {
		// extract header
		String s = new String(msg, 0, msg.length);
		msgTokens = s.split("\\s+");

		switch(msgTokens[0]) {
		case "PUTCHUNK":
			Protocol.handlePUTCHUNK(msg, sender);
			break;
		case "STORED":
			Protocol.handleSTORED(msg, sender);
			break;
		case "GETCHUNK":
			break;
		case "CHUNK":
			break;
		case "DELETE":
			break;
		default:
			break;
		}
	}

	public static byte[] extractBody(byte[] msg) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(msg);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		// ler a msg até encontrar o CRLF
		String line = null;
		int headerBytes = 0;
		do {
			line = reader.readLine();
			headerBytes += line.getBytes().length;
		} while(!line.isEmpty());

		headerBytes += 2*Protocol.CRLF.getBytes().length;
		byte[] body = new byte[msg.length - headerBytes];
		System.arraycopy(msg, headerBytes, body, 0, msg.length - headerBytes);

		return body;
	}

	public static byte[] readyPacket(byte[] header, byte[] data) {
		byte[] packet = new byte[header.length + data.length];

		System.arraycopy(header, 0, packet, 0, header.length);
		System.arraycopy(data, 0, packet, header.length, data.length);

		return packet;
	}

}
