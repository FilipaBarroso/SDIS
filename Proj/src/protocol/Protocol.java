package protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Random;

import channels.MC;
import channels.MDB;
import channels.Messenger;
import channels.MulticastChannel;
import data.Files;
import database.ChunkKey;
import service.Chunk;
import service.Peer;
import service.PeerService;

public class Protocol {

	public static String VERSION = "1.0";
	public static String CRLF = "\r" + "\n";
	public static int MAX_BUFFER = 100000;
	public static Random random = new Random();

	public static void initiateBackup(String filename, int repD) {
		filename = Files.FILE_PATH + filename;
		File file = new File(filename);

		// create CHUNKS folder
		File chunks = new File(Files.LOCAL_CHUNKS_PATH);
		chunks.mkdir();

		Backup backup = new Backup(file, repD);
		new Thread(backup).start();
	}

	public static void sendPUTCHUNK(Chunk chunk, ChunkKey ck) throws Exception {
		MC.readyStoredConfirms(ck);

		String header = "PUTCHUNK" + " " + VERSION + 
				" " + PeerService.getLocalPeer().get_ip() +
				" " + chunk.getFileID() +
				" " + chunk.getNo() +
				" " + chunk.getRepD() +
				" " + CRLF + CRLF;

		byte[] packet = MulticastChannel.readyPacket(header.getBytes(), chunk.getData());

		Messenger.sendToMDB(packet);

		System.out.println("PROTOCOL: Sent a chunk to MDB");
	}
	
	/*
	 * verificar se o chunk ja existe
	 * guardar o chunk na Database 
	 */
	public static void handlePUTCHUNK(byte[] msg, Peer sender) {
		System.out.println("MDB: received a PUTCHUNK");
		
		try {
			byte[] body = MulticastChannel.extractBody(msg);
			String[] msg_tokens = MulticastChannel.msgTokens;
			String fileID = msg_tokens[3];
			int chunkNo = Integer.parseInt(msg_tokens[4]);
			int repD = Integer.parseInt(msg_tokens[5]);
			
			// Chunk(fileID, chunkNo, repDegree, data)
			Chunk chunk = new Chunk(fileID, chunkNo, repD, body);
			ChunkKey ck = new ChunkKey(chunkNo, fileID);
			
			// if the peer already has this chunk, send STORED confirmation
			if(Files.hasChunk(chunk)) {
				//System.out.println("MDB: already have this chunk");
				Protocol.sendSTORED(sender, chunk);	
			}
			
			// else wait between 0 and 400ms and send STORED confirmation if needed
			else {
				Thread.sleep(random.nextInt(399));
				
				if(MC.getNumStoredConfs(ck) < repD) {
					Files.storeChunk(chunk);
					Protocol.sendSTORED(PeerService.getLocalPeer(), chunk);
				}
				else System.out.println("PROTOCOL: chose not to save this chunk\n--------------------");

				MC.deleteStoredConfs(ck);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendSTORED(Peer sender, Chunk chunk) throws Exception {
		String header = "STORED" + " " + VERSION +
				" " + sender.get_ip() +
				" " + chunk.getFileID() +
				" " + chunk.getNo() +
				" " + CRLF + CRLF;

		byte[] packet = MulticastChannel.readyPacket(header.getBytes(), "".getBytes());

		Messenger.sendToMC(packet);

		System.out.println("PROTOCOL: sent a STORED confirm to MC\n--------------------");
	}
	
	public static void handleSTORED(byte[] msg, Peer sender) {
		try {
			byte[] body = MulticastChannel.extractBody(msg);
			String[] msg_tokens = MulticastChannel.msgTokens;
			
			// ChunkKey(chunkNo, fileID)
			ChunkKey chunkKey = new ChunkKey(Integer.parseInt(msg_tokens[4]), msg_tokens[3]);
			
			MC.addStoredConfirm(chunkKey, sender);
			
			// TODO
			// update database
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
