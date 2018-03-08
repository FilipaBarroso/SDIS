package channels;

import java.net.DatagramPacket;
import java.net.InetAddress;

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

}
