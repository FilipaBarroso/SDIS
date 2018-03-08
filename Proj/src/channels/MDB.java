package channels;

import java.net.DatagramPacket;
import java.net.InetAddress;

/*
 * Stored info related to the MDB channel
 * Calls a MsgHangler thread
 */
public class MDB extends MulticastChannel{

	public MDB(InetAddress ip, int port) {
		super(ip, port);
		
	}
	
}
