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

			// debbugging, saving the chunks locally
			Files.loadChunks(chunkArray, file.getName());	
			
			// TODO
			// convert chunks into a single file
			
			// no longer dealing with CHUNK messages
			MDR.doneExpectingChunks();
			
			if(MDR.numChunkConfirmsForFile(fileID) < num_chunks) {
				System.out.println("RESTORE: Couldn't restore file");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addChunk(Chunk chunk) {
		chunkArray[chunk.getNo()] = chunk;
	}
	
}