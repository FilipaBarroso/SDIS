package channels;

import java.net.DatagramPacket;
import java.net.InetAddress;

/*
 * Stored information related to the MDR channel
 * Calls MsgHandler
 */
public class MDR extends MulticastChannel{

	public MDR(InetAddress ip, int port) {
		super(ip, port);
		
	}
	
}
