package service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;

import Data.Files;


public class Backup implements Runnable {

	private File file;
	private int replicationDegree;

	public Backup(File file, int replicationDegree) {
		this.file = file;
		this.replicationDegree = replicationDegree;
	}
	
	public void run() {
		Chunk[] chunks;
		try {
			chunks = getChunks(file);
			
			backupChunks(chunks);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Chunk[] getChunks(File file) throws FileNotFoundException {
		byte[] fileData = Files.getFileData(file);
		
		// num_chunk deve ter um ultimo chunk vazio
		int num_chunks = (int) (Math.floor(file.length() / Chunk.MAX_SIZE) + 1);
		Chunk[] chunks = new Chunk[num_chunks];
		
		ByteArrayInputStream fileStream = new ByteArrayInputStream(fileData);
		byte[] chunkData = new byte[Chunk.MAX_SIZE];
		
		for(int i=0; i < num_chunks; i++) {
			// fieStream.read(chunkData)
			// ver se é o ultimo
		}
		
		return chunks;
	}
	
	public void backupChunks(Chunk[] chunks) {
		
	}
	
}
