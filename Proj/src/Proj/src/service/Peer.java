package service;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;


public class Peer implements Serializable {
	private static final long serialVersionUID = 3L;
	private InetAddress ip;
	private int port;
	
	public Peer(InetAddress ip, int port) {
		this.set_ip(ip);
		this.set_port(port);
	}

	public Peer() {}

	public InetAddress get_ip() {
		return ip;
	}

	public void set_ip(InetAddress ip) {
		this.ip = ip;
	}

	public int get_port() {
		return port;
	}

	public void set_port(int port) {
		this.port = port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Peer other = (Peer) obj;

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