package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import protocol.Protocol;

import java.io.FileInputStream;

import service.Chunk;
import service.PeerService;

public class Files {

	public static String FILE_PATH = "FILES/";
	public static String RESTORED_CHUNKS_PATH = "RESTORED_CHUNKS/";
	public static String CHUNKS_PATH = "CHUNKS/";
	
	public static String getFileID(File file) {
		String file_metadata = file.getName() + file.lastModified() + PeerService.getLocalPeer().toString();

		return getSha256(file_metadata);
	}

	public static String getSha256(String base) {
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	public static byte[] getFileData(File file) throws FileNotFoundException {
		FileInputStream fileStream = new FileInputStream(file);


		byte[] fileData = new byte[(int) file.length()];

		try {
			fileStream.read(fileData);
			fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileData;
	}
	
	public static void loadChunks(Chunk[] chunkArray, String filename) throws FileNotFoundException {
		// create folder for these CHUNKS
		String path = RESTORED_CHUNKS_PATH + filename;
		File dir = new File(path);
		dir.mkdir();
		
		int i;
		for(i=0; i < chunkArray.length; i++) {
			FileOutputStream chunkFile = new FileOutputStream(path + "/" +  chunkArray[i].getNo());
			
			try {
				chunkFile.write(chunkArray[i].getData());
				chunkFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println("FILES: Chunks saved locally");
	}
	
	public static boolean hasChunk(Chunk chunk) {
		File chunksFolder = new File(CHUNKS_PATH);
		if(!chunksFolder.exists() || !chunksFolder.isDirectory())
			chunksFolder.mkdir();
		
		File chunkFile = new File(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getNo());
		if(chunkFile.exists() && chunkFile.isFile()) {
			System.out.println("FILES: Rejected a duplicate chunk");
			return true;
		}
		
		return false;
	}
	
	public static void storeChunk(Chunk chunk) {
		File chunksFolder = new File(CHUNKS_PATH);
		if(!chunksFolder.exists() || !chunksFolder.isDirectory())
			chunksFolder.mkdir();
		
		try {
			FileOutputStream stream = new FileOutputStream(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getNo());
			stream.write(chunk.getData());
			stream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("FILES: Saved a chunk successfully");
		
		// TODO
		// update database
		
	}
}
