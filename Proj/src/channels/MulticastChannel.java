package channels;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
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

	public static String[] msgTokens;

	public static void decypherMsg(DatagramPacket msg, Peer sender) {
		// extract header
		String s = new String(msg.getData(), 0, msg.getLength());
		msgTokens = s.split("\\s+");

		switch(msgTokens[0]) {
		case "PUTCHUNK":
			Protocol.handlePUTCHUNK(msg, sender);
			break;
		case "STORED":
			Protocol.handleSTORED(msg, sender);
			break;
		case "GETCHUNK":
			Protocol.handleGETCHUNK(msg, sender);
			break;
		case "CHUNK":
			Protocol.handleCHUNK(msg, sender);
			break;
		case "DELETE":
			Protocol.handleDELETE(msg, sender);
			break;
		default:
			break;
		}
	}

	public static byte[] extractBody(DatagramPacket msg) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(msg.getData());
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		// ler a msg até encontrar o CRLF
		String line = null;
		int headerBytes = 0;
		do {
			line = reader.readLine();
			headerBytes += line.getBytes().length;
		} while(!line.isEmpty());

		headerBytes += 2*Protocol.CRLF.getBytes().length;
		byte[] body = Arrays.copyOfRange(msg.getData(), headerBytes, msg.getLength());
		
		return body;
	}

	public static byte[] readyPacket(byte[] header, byte[] data) {
		byte[] packet = new byte[header.length + data.length];

		System.arraycopy(header, 0, packet, 0, header.length);
		System.arraycopy(data, 0, packet, header.length, data.length);

		return packet;
	}

}
