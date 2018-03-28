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
	
	public boolean same(Object obj) {		
		if(obj == null || getClass() != obj.getClass()) return false;
		
		Peer peerObj = (Peer)obj;
		
		if(!peerObj.get_ip().equals(ip) || peerObj.get_port() != port) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(ip, port);
	}
}
