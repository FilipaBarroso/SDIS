package Protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import channels.MDB;
import channels.MulticastChannel;

public class MsgHandler {

	
	public static String[] msgTokens;
	
	public static void decypherMsg(byte[] msg) {
		// extract header
		String s = new String(msg, 0, msg.length);
		msgTokens = s.split("\\s+");
		
		switch(msgTokens[0]) {
		case "PUTCHUNK":
			MDB.handlePUTCHUNK(msg);
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
		
		headerBytes += Protocol.CRLF.getBytes().length;
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
