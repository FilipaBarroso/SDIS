package protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Random;

import channels.MC;
import channels.MDB;
import channels.MDR;
import channels.Messenger;
import channels.MulticastChannel;
import data.Files;
import data.Storage;
import database.ChunkKey;
import database.FileDetails;
import service.Chunk;
import service.Peer;
import service.PeerService;

public class Protocol {

	public static String VERSION = "1.0";
	public static String CRLF = "\r" + "\n";
	public static int MAX_BUFFER = 65000;
	public static Random random = new Random();

	public static void initiateBackup(String filename, int repD) {
		File file = new File(Files.FILE_PATH + filename);

		if(!PeerService.getDatabase().fileWasSaved(filename)) {
			Backup backup = new Backup(file, repD);
			new Thread(backup).start();
		}
		else System.out.println("PROTOCOL: File with that name already exists in the server");
	}

	public static void initiateRestore(String filename) {
		File file = new File(Files.FILE_PATH + filename);

		if(PeerService.getDatabase().getRestorableFiles().keySet().isEmpty()) {
			System.out.println("PROTOCOL: No files in the database");
			return;
		}

		if(PeerService.getDatabase().fileWasSaved(filename)) {
			FileDetails fd = PeerService.getDatabase().getFileDetails(filename);
			Restore restore = new Restore(filename, fd);
			new Thread(restore).start();
		}
		else System.out.println("PROTOCOL: File with that name doesn't exist in the server");
	}	

	public static void initiateDelete(String filename) {
		File file = new File(Files.FILE_PATH + filename);

		if(PeerService.getDatabase().getRestorableFiles().keySet().isEmpty()) {
			System.out.println("PROTOCOL: No files in the database");
			return;
		}

		if(PeerService.getDatabase().fileWasSaved(filename)) {
			FileDetails fd = PeerService.getDatabase().getFileDetails(filename);
			Delete delete = new Delete(filename, fd);
			new Thread(delete).start();
		}
		else System.out.println("PROTOCOL: File with that name doesn't exist in the server");
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

	public static void handlePUTCHUNK(DatagramPacket msg, Peer sender) {
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
			if(Files.hasChunk(ck)) {
				//System.out.println("MDB: already have this chunk");
				Protocol.sendSTORED(sender, chunk);	
			}

			// else wait between 0 and 400ms and send STORED confirmation if needed
			else {
				Thread.sleep(random.nextInt(400));
				
				// add file only if there's space for it and if needed
				if(MC.getNumStoredConfs(ck) < repD && Storage.addFile(body.length)) {
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

	public static void handleSTORED(DatagramPacket msg, Peer sender) {
		try {
			String[] msg_tokens = MulticastChannel.msgTokens;

			// ChunkKey(chunkNo, fileID)
			ChunkKey chunkKey = new ChunkKey(Integer.parseInt(msg_tokens[4]), msg_tokens[3]);

			MC.addStoredConfirm(chunkKey, sender);

			// update chunk database
			PeerService.getDatabase().addPeerToChunkPeerList(chunkKey, sender);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendGETCHUNK(Peer sender, String fileID, int chunkNo) throws Exception {
		String header = "GETCHUNK" + " " + VERSION +
				" " + sender.get_ip() +
				" " + fileID +
				" " + chunkNo +
				" " + CRLF + CRLF;

		byte[] packet = MulticastChannel.readyPacket(header.getBytes(), "".getBytes());

		Messenger.sendToMC(packet);

		System.out.println("PROTOCOL: sent a GETCHUNK to MC");
	}

	public static void handleGETCHUNK(DatagramPacket msg, Peer sender) {
		System.out.println("MC: received a GETCHUNK");

		try {
			String[] msg_tokens = MulticastChannel.msgTokens;
			String fileID = msg_tokens[3];
			int chunkNo = Integer.parseInt(msg_tokens[4]);

			// Chunk(fileID, chunkNo, repDegree, data)
			ChunkKey ck = new ChunkKey(chunkNo, fileID);

			// if the peer doesn't have this chunk then it ignores the GETCHUNK msg
			if(!Files.hasChunk(ck)) {
				System.out.println("PROTOCOL: Ignored GETCHUNK\n--------------------");
				return;
			}

			// wait between 0 and 400ms and send CHUNK if needed
			Thread.sleep(random.nextInt(400));

			if(!MDR.hasChunkConf(ck)) {
				MDR.addChunkConfirm(ck);
				Chunk chunk = Files.getChunk(ck);
				sendCHUNK(PeerService.getLocalPeer(), chunk);
			}
			else System.out.println("PROTOCOL: Decided not to send a CHUNK\n--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendCHUNK(Peer sender, Chunk chunk) throws Exception {
		String header = "CHUNK" + " " + VERSION +
				" " + sender.get_ip() +
				" " + chunk.getFileID() +
				" " + chunk.getNo() +
				" " + CRLF + CRLF;

		byte[] packet = MulticastChannel.readyPacket(header.getBytes(), chunk.getData());

		Messenger.sendToMDR(packet);

		System.out.println("PROTOCOL: sent a CHUNK to MDR\n--------------------");		
	}

	public static void handleCHUNK(DatagramPacket msg, Peer sender) {
		System.out.println("MDR: received a CHUNK");

		try {
			byte[] body = MulticastChannel.extractBody(msg);
			String[] msg_tokens = MulticastChannel.msgTokens;
			String fileID = msg_tokens[3];
			int chunkNo = Integer.parseInt(msg_tokens[4]);

			// update restored chunk array if the peer is expecting to receive chunks
			if(MDR.isExpectingChunks()) {
				Chunk new_chunk = new Chunk(fileID, chunkNo, 0, body);
				Restore.addChunk(new_chunk);
			}

			// update MDR confirms
			ChunkKey chunkKey = new ChunkKey(chunkNo, fileID);
			MDR.addChunkConfirm(chunkKey);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendDELETE(Peer sender, String fileID) throws Exception {
		String header = "DELETE" + " " + VERSION +
				" " + sender.get_ip() +
				" " + fileID +
				" " + CRLF + CRLF;

		byte[] packet = MulticastChannel.readyPacket(header.getBytes(), "".getBytes());

		Messenger.sendToMC(packet);

		System.out.println("PROTOCOL: sent a DELETE to MC");	
	}

	public static void handleDELETE(DatagramPacket msg, Peer sender) {
		System.out.println("MC: received a DELETE");
		
		try {
			String[] msg_tokens = MulticastChannel.msgTokens;
			String fileID = msg_tokens[3];
			
			int removed_space = Files.deleteSavedChunksFrom(fileID);
			Storage.removeFile(removed_space);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
