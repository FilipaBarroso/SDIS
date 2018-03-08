package service;

import java.net.InetAddress;

public class Peer {
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
	
	public boolean equals(Object obj) {
		if(obj == null || getClass() != obj.getClass()) return false;
		
		Peer peerObj = (Peer)obj;
		
		if(peerObj.get_ip() != ip || peerObj.get_port() != port) return false;
		
		return true;
	}
	
}
