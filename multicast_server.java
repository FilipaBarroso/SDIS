import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class multicast_server extends Thread{

	private static boolean server_up = true;

	public void run() {
		System.out.println("Server up... \t type exit/close to terminate\n");
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		while(true) { try {
			String s = (String)cin.readLine();
			if(s.equals("exit") || s.equals("close")) server_up = false;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public static void main(String[] args) throws Exception{
		// Inet
		String inet_name = "224.0.0.1";
		InetAddress group = InetAddress.getByName(inet_name);
		int port = 9999;

		// UDP
		byte[] receive_buffer = new byte[1024];
		byte[] send_buffer = new byte[1024];
		DatagramPacket receive_packet = new DatagramPacket(receive_buffer, receive_buffer.length);
		DatagramPacket respond_packet = null;
		MulticastSocket socket = new MulticastSocket(port);

		// server loop
		multicast_server m_server = new multicast_server();
		m_server.start();

		while(server_up) {
			// check for client requests
			socket.receive(receive_packet);
			receive_buffer = receive_packet.getData();
			String s = new String(receive_buffer, 0, receive_packet.getLength());
			System.out.println("Received msg from client: "+ s);
			
			// respond to client
			InetAddress client_addr = receive_packet.getAddress();
			int client_port = receive_packet.getPort();
			send_buffer = s.getBytes();
			respond_packet = new DatagramPacket(send_buffer, send_buffer.length, client_addr, client_port);
			socket.send(respond_packet);
			System.out.println("Replied..\n");
		}
		
		System.out.println("Closing server...");
		socket.close();
		
		return;
	}
}
