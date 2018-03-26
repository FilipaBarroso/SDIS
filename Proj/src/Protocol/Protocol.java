package Protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import service.Chunk;

public class Protocol {

	public static String PUTCHUNK = "PUTCHUNK";
	public static String VERSION = "1.0";
	public static String CRLF = "\r" + "\n";
	public static String FILE_PATH = "FILES/";
	public static String CHUNKS_PATH = "CHUNKS/";
	
	public static void decypherMsg(byte[] msg) {
		// see if it's PUTCHUNK, STORED, etc..
	}
	
	public static void initiateBackup(String filename, int repD) {
		filename = FILE_PATH + filename;
		File file = new File(filename);
		
		// create CHUNKS folder
		File chunks = new File(CHUNKS_PATH);
		chunks.mkdir();
		
		Backup backup = new Backup(file, repD);
		new Thread(backup).start();
	}
	
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
		
		System.out.println("Done");
	}
}
