import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class multicast_client extends Thread {

	private static boolean client_up = true;

	public void run() {
		System.out.println("Client online... \n\ntype exit/close to terminate\nregister <plate number> <name>\nlookup <plate number>\n");
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		while(true) { try {
			String s = (String)cin.readLine();
			if(s.equals("exit") || s.equals("close")) client_up = false;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	public static void main(String[] args) throws Exception {
		// Inet
		String inet_name = "224.0.0.1";
		InetAddress group = InetAddress.getByName(inet_name);
		int port = 9999;

		// UDP
		String s;
		byte[] send_buffer = new byte[1024];
		byte[] receive_buffer = new byte[1024];
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		
		DatagramPacket send_packet = null;
		DatagramPacket receive_packet = null;
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(group); //subscribe to group
			    
		// client loop
		System.out.println("Client online... \n\ntype exit/close to terminate\nregister <plate number> <name>\nlookup <plate number>");
		//dnt think we need to use a thread for this tbh
		//client client = new client();
		//client.start();

		while(true) {
			// Send msg
			sleep(100);
			System.out.println("\nEnter msg:");
			s = (String)cin.readLine();
			send_buffer = s.getBytes();
			if(s.equals("exit") || s.equals("close")) break;
			
			send_packet = new DatagramPacket(send_buffer, send_buffer.length, group, port);
			socket.send(send_packet);
			
			// receive server msg
			receive_packet = new DatagramPacket(receive_buffer, receive_buffer.length);
			socket.receive(receive_packet);
			receive_buffer = receive_packet.getData();
			s = new String(receive_buffer, 0, receive_packet.getLength());
			System.out.println("Received msg from server: "+ s);
		}
		
		System.out.println("\nClient closing...");
		socket.leaveGroup(group); //unsubscribe from group
		socket.close();
		
		return;
	}	
}
