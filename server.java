import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class server extends Thread{
	
	private boolean server_up = true;
	
	public void run() {
		System.out.println("Server up... \t type exit/close to terminate\n");
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		try {
			String s = (String)cin.readLine();
			if(s.equals("exit") || s.equals("close")) server_up = false;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		// Inet
		InetAddress inet = InetAddress.getLocalHost();
		int port = 9999;	// temp port used

		// UDP
		byte[] buffer = new byte[320];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		DatagramSocket socket = new DatagramSocket(port);
		
		
		// server loop
		server server = new server();
		server.start();
		
		while(server.server_up) {	
			socket.receive(packet);
			buffer = packet.getData();
			String s = new String(buffer, 0, packet.getLength());
			System.out.println("Received msg: "+ s);
		}
	
	socket.close();
	}
}
