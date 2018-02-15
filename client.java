import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class client {
	public static void main(String[] args) throws UnknownHostException, SocketException, IOException {
		// Inet
		InetAddress inet = InetAddress.getLocalHost();
		int port = 9999;	// temp port used

		// UDP
		DatagramPacket packet = null;
		DatagramSocket socket = new DatagramSocket();
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		String s;
		byte[] buffer;
		
		// read/send msg
		System.out.println("Enter msg:");
		s = (String)cin.readLine();
		buffer = s.getBytes();
		
		packet = new DatagramPacket(buffer, buffer.length, inet, port);
		socket.send(packet);
		
		System.out.println("\nClient closing...");
		socket.close();
	}	
}
