package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import protocol.Protocol;

import java.io.FileInputStream;

import channels.MulticastChannel;
import database.ChunkKey;
import service.Chunk;
import service.PeerService;

public class Files {

	public static String FILE_PATH = "FILES/";
	public static String RESTORED_FILES_PATH = "RESTORED_FILES/";
	public static String RESTORED_CHUNKS_PATH = "RESTORED_CHUNKS/";
	public static String CHUNKS_PATH = "SAVED_CHUNKS/";

	public static String getFileID(File file) {
		String file_metadata = file.getName() + file.lastModified() + PeerService.getLocalPeer().get_ip();

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

	/*
	public static void storeRestoredChunks(Chunk[] tmp_chunkArray, String filename, String fileID) throws FileNotFoundException {
		// create folder for these CHUNKS
		String path = RESTORED_CHUNKS_PATH + filename;
		File dir = new File(path);
		if(!dir.exists() || !dir.isDirectory())	
			dir.mkdir();

		Chunk[] chunkArray = new Chunk[tmp_chunkArray.length];

		// the chunk array might have undesired chunks in it, so we compare fileIDs to pick out the ones we want
		int j, n=0;
		for(j=0; j < tmp_chunkArray.length; j++) {
			if(tmp_chunkArray[j].getFileID().equals(fileID)) {
				chunkArray[n] = tmp_chunkArray[j];
				n++;
			}
		}

		int i;
		for(i=0; i < chunkArray.length; i++) {
			try {
				FileOutputStream chunkStream = new FileOutputStream(path + "/" + fileID + "-" + chunkArray[i].getNo());
				chunkStream.write(chunkArray[i].getData());
				chunkStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		System.out.println("FILES: Chunks saved locally");
	}
	*/

	public static boolean hasChunk(ChunkKey chunk) {
		File chunksFolder = new File(CHUNKS_PATH);
		if(!chunksFolder.exists() || !chunksFolder.isDirectory())
			chunksFolder.mkdir();

		File chunkFile = new File(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getChunkNo());
		if(chunkFile.exists() && chunkFile.isFile()) {
			return true;
		}

		return false;
	}

	public static Chunk getChunk(ChunkKey chunk) throws Exception {
		File chunkFile = new File(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getChunkNo());
		if(chunkFile.exists() && chunkFile.isFile()) {
			FileInputStream instream = new FileInputStream(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getChunkNo());

			byte[] body = new byte[(int) chunkFile.length()];
			instream.read(body, 0 , body.length);
			instream.close();

			Chunk tmp = new Chunk(chunk.getFileID(), chunk.getChunkNo(), 0, body);
			return tmp;
		}

		return null;
	}

	public static void storeChunk(Chunk chunk) {
		File chunksFolder = new File(CHUNKS_PATH);
		if(!chunksFolder.exists() || !chunksFolder.isDirectory())
			chunksFolder.mkdir();

		try {
			FileOutputStream stream = new FileOutputStream(CHUNKS_PATH + "/" + chunk.getFileID() + "-" + chunk.getNo());
			stream.write(chunk.getData());
			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ChunkKey chunkKey = new ChunkKey(chunk.getNo(), chunk.getFileID());
		PeerService.getDatabase().addChunk(chunkKey, chunk.getRepD());

		System.out.println("FILES: Saved a chunk successfully with " + chunk.getData().length + "bytes");
	}

	public static void convertChunks(Chunk[] tmp_chunkArray, String filename, String fileID) throws FileNotFoundException {
		// create folder for these CHUNKS
		String path = RESTORED_FILES_PATH + filename;
		File file = new File(path);

		Chunk[] chunkArray = new Chunk[tmp_chunkArray.length];

		// the chunk array might have undesired chunks in it, so we compare fileIDs to pick out the ones we want
		int j, n=0;
		for(j=0; j < tmp_chunkArray.length; j++) {
			if(tmp_chunkArray[j].getFileID().equals(fileID)) {
				chunkArray[n] = tmp_chunkArray[j];
				n++;
			}
		}

		try {
			FileOutputStream stream = new FileOutputStream(path);

			int i;
			for(i=0; i < chunkArray.length; i++) {
				stream.write(chunkArray[i].getData());
			} 
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("FILES: File restored");
	}

	public static void deleteRestoredFile(String filename) {
		String path = RESTORED_FILES_PATH + filename;
		File file = new File(path);
		if(file.exists() && file.isFile()) {
			file.delete();
			System.out.println("FILES: Deleted a file");
		}
		
		File rest = new File(RESTORED_FILES_PATH);
		if(rest.listFiles().length == 0)
			rest.delete();
	}

	public static int deleteSavedChunksFrom(String fileID) {
		int size_counter = 0;
		File chunksFolder = new File(CHUNKS_PATH);
		if(chunksFolder.exists() && chunksFolder.isDirectory()) {
			File[] saved_chunks = chunksFolder.listFiles();

			// percorrer todos os chunks guardados, deletar os chunks que pertencem ao ficheiro de fileID
			int i;
			for(i = 0; i < saved_chunks.length; i++) {
				if(saved_chunks[i].getName().contains(fileID)) {
					File chunkfile = new File(saved_chunks[i].getPath());
					if(chunkfile.exists() && chunkfile.isFile()) {
						size_counter += chunkfile.length();
						chunkfile.delete();
						System.out.println("FILES: Deleted a chunk");
					}
				}
			}
		}
		
		if(chunksFolder.listFiles().length == 0)
			chunksFolder.delete();
	
		return size_counter;
	}
}
