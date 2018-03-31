package protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Arrays;

import channels.MC;
import channels.MDR;
import data.Files;
import database.ChunkKey;
import service.Chunk;
import service.PeerService;

public class Restore implements Runnable {

	private File file;
	private String fileID;
	private int num_chunks;
	private static Chunk[] chunkArray;

	public Restore(File file) {
		this.file = file;
		this.fileID = Files.getFileID(file);
		num_chunks = (int) (Math.floor(file.length() / Chunk.MAX_SIZE) + 1);
		chunkArray = new Chunk[num_chunks];
		
		// create RESTORED_CHUNKS folder
		File res_files = new File(Files.RESTORED_FILES_PATH);
		if(!res_files.exists() || !res_files.isDirectory())
			res_files.mkdir();
		
		File res_chunks = new File(Files.RESTORED_CHUNKS_PATH);
		if(!res_chunks.exists() || !res_chunks.isDirectory())
			res_chunks.mkdir();
	}

	public void run() {
		try {
			// tell MDR that we're expecting to receive chunks
			MDR.expectChunks();
			
			int i;
			for(i=0; i < num_chunks; i++) {
				Protocol.sendGETCHUNK(PeerService.getLocalPeer(), fileID, i);
				
				// wait 400ms for the CHUNK msg
				Thread.sleep(400);
			}	
			
			MDR.doneExpectingChunks();
			
			if(MDR.numChunkConfirmsForFile(fileID) < num_chunks) {
				System.out.println("RESTORE: Couldn't restore file");
				return;
			}

			// debbugging, saving the chunks locally
			Files.loadChunks(chunkArray, file.getName(), fileID);	
			
			// convert chunks into a single file
			Files.convertChunks(chunkArray, file.getName(), fileID);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addChunk(Chunk chunk) {
		chunkArray[chunk.getNo()] = chunk;
	}
	
}