package database;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class PeerKey implements Serializable {
	private static final long serialVersionUID = 3L;

	private InetAddress ip;
	private int port;
	
	public PeerKey(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		PeerKey other = (PeerKey) obj;

		if (ip == null && other.ip != null) {
			return false;
		} else if (!ip.equals(other.ip))
			return false;

		if (port != other.port)
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(ip, port);
	}

}
