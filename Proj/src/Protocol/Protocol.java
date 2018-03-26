package Protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import channels.Messenger;
import service.Chunk;
import service.PeerService;

public class Protocol {

	public static String VERSION = "1.0";
	public static String CRLF = "\r" + "\n";
	public static String FILE_PATH = "FILES/";
	public static String CHUNKS_PATH = "CHUNKS/";
	public static int MAX_BUFFER = 100000;

	public static void initiateBackup(String filename, int repD) {
		filename = FILE_PATH + filename;
		File file = new File(filename);
		
		// create CHUNKS folder
		File chunks = new File(CHUNKS_PATH);
		chunks.mkdir();
		
		Backup backup = new Backup(file, repD);
		new Thread(backup).start();
	}
	
	public static void sendPUTCHUNKS(Chunk[] chunkArray, String fileID) throws Exception {
		int i;
		for(i=0; i < chunkArray.length; i++) {
		String header = "PUTCHUNK" + " " + VERSION + 
				" " + PeerService.getLocalPeer().get_ip() +
				" " + fileID +
				" " + i +
				" " + chunkArray[i].getRepD() +
				" " + CRLF + CRLF;
		
		byte[] packet = MsgHandler.readyPacket(header.getBytes(), chunkArray[i].getData());
		
		Messenger.sendToMDB(packet);
		}
		
		System.out.println("PROTOCOL: done sending chunks to MDB\n");
	}
	
	// TODO
	public static void sendSTORED() {}
	
	public static void loadChunks(Chunk[] chunkArray, String filename) throws FileNotFoundException {
		// create folder for these CHUNKS
		String path = CHUNKS_PATH + filename;
		File dir = new File(path);
		dir.mkdir();
		
		int i;
		for(i=0; i < chunkArray.length; i++) {
			FileOutputStream chunkFile = new FileOutputStream(path + "/" +  chunkArray[i].getNo());
			
			try {
				chunkFile.write(chunkArray[i].getData());
				chunkFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		System.out.println("PROTOCOL: Done loading chunks");
	}

	// TODO
	public static void storeChunk(Chunk chunk) {}
}
