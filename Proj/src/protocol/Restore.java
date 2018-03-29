package protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Arrays;

import channels.MC;
import data.Files;
import database.ChunkKey;
import service.Chunk;
import service.PeerService;

public class Restore implements Runnable {

	private File file;
	private String fileID;
	private int num_chunks;
	private Chunk[] chunkArray;

	public Restore(File file) {
		this.file = file;
		this.fileID = Files.getFileID(file);
		num_chunks = (int) (Math.floor(file.length() / Chunk.MAX_SIZE) + 1);
		chunkArray = new Chunk[num_chunks];
	}

	public void run() {
		try {
			int i;
			for(i=0; i < num_chunks; i++) {
				Protocol.sendGETCHUNK(PeerService.getLocalPeer(), fileID, i);

				// wait 400ms for the CHUNK msg
				Thread.sleep(400);
				
				
			}

			Files.loadChunks(chunkArray, file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}