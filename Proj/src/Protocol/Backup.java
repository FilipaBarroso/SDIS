package Protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Arrays;

import service.Chunk;
import Data.Files;


public class Backup implements Runnable {

	private File file;
	private String fileID;
	private int replicationDegree;

	public Backup(File file, int replicationDegree) {
		this.file = file;
		this.replicationDegree = replicationDegree;

		this.fileID = Files.getFileID(file);
	}

	public void run() {
		Chunk[] chunkArray;
		try {
			chunkArray = getChunks(file);

			backupChunks(chunkArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Chunk[] getChunks(File file) throws FileNotFoundException {
		byte[] fileData = Files.getFileData(file);

		// num_chunk deve ter um ultimo chunk vazio
		int num_chunks = (int) (Math.floor(file.length() / Chunk.MAX_SIZE) + 1);
		Chunk[] chunkArray = new Chunk[num_chunks];

		ByteArrayInputStream fileStream = new ByteArrayInputStream(fileData);
		byte[] chunkStream = new byte[Chunk.MAX_SIZE];

		int i;
		for(i=0; i < num_chunks - 1; i++) {
			byte[] tmp_chunk;
			fileStream.read(chunkStream, 0, chunkStream.length);
			tmp_chunk = Arrays.copyOfRange(chunkStream, 0, Chunk.MAX_SIZE);	

			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, tmp_chunk);
			chunkArray[i] = chunk;
		}

		if(file.length() % Chunk.MAX_SIZE == 0) {
			byte[] empty_chunk = new byte[0];
			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, empty_chunk);
			chunkArray[i] = chunk;
		}
		
		else {
			byte[] tmp_chunk;
			int bytesRead = fileStream.read(chunkStream, 0, chunkStream.length);
			tmp_chunk = Arrays.copyOfRange(chunkStream, 0, bytesRead);	

			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, tmp_chunk);
			chunkArray[i] = chunk;
		}

		return chunkArray;
	}

	public void backupChunks(Chunk[] chunkArray) throws FileNotFoundException {
		// send PUTCHUNKs 
		
		/**
		 * temporario para testar
		 * guardar os chunks localmente
		 */
		Protocol.loadChunks(chunkArray, this.file.getName());

	}

}
