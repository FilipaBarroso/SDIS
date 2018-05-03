import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

public class Main {
	
	private static Chain blockchain;
	
	public static void main(String args[]) throws Exception {
		blockchain = new Chain();
	}
}