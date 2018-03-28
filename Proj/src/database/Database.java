package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import service.PeerService;


public class Database implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	//volatile to guarantee visibility across threads
	private volatile HashMap<ChunkKey, ChunkDetails> chunkDB;
	private volatile HashMap<String, FileDetails> restorableFiles;
	
	public Database(){
		setChunkDB(new HashMap<ChunkKey, ChunkDetails>());
		setRestorableFiles(new HashMap<String, FileDetails>());
		
	}
	
	/*
	 * gets and sets
	 */
	public  HashMap<ChunkKey, ChunkDetails> getChunkDB() {
		return chunkDB;
	}

	public void setChunkDB(HashMap<ChunkKey, ChunkDetails> chunkDB) {
		this.chunkDB = chunkDB;
	}

	public HashMap<String, FileDetails> getRestorableFiles() {
		return restorableFiles;
	}

	public void setRestorableFiles(HashMap<String, FileDetails> restorableFiles) {
		this.restorableFiles = restorableFiles;
	}
	
	
	/*
	 * chunksDB operations
	 */
	private synchronized boolean containsChunk(ChunkKey chunkKey){
		return chunkDB.containsKey(chunkKey);
		
	}
	
	public synchronized void addChunk(ChunkKey chunkKey, int repD){
		if(containsChunk(chunkKey) != true){
			ChunkDetails chunkDetails = new ChunkDetails(repD, new ArrayList<PeerKey>());
			
			chunkDB.put(chunkKey, chunkDetails);
			
			PeerService.saveDatabase();
		}
	}
	
	public synchronized void removeChunk(ChunkKey chunkKey) {
		chunkDB.remove(chunkKey);
		
		PeerService.saveDatabase();
	}
	
	

	
}