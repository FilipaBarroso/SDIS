package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import service.PeerService;
import service.Peer;


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
	
	public synchronized FileDetails getFileDetails(String filename){
		return restorableFiles.get(filename);
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
			ChunkDetails chunkDetails = new ChunkDetails(repD, new ArrayList<Peer>());
			
			chunkDB.put(chunkKey, chunkDetails);
			
			PeerService.saveDatabase();
		}
	}
	
	public synchronized void removeChunk(ChunkKey chunkKey) {
		chunkDB.remove(chunkKey);
		
		PeerService.saveDatabase();
	}
	
	public synchronized void addPeerToChunkPeerList(ChunkKey chunkKey, Peer peer){
		if(containsChunk(chunkKey) && !chunkDB.get(chunkKey).getPeerList().contains(peer)){
			chunkDB.get(chunkKey).getPeerList().add(peer);
			
			PeerService.saveDatabase();
		}
	}
	
	public synchronized void removePeerFromChunkPeerList(ChunkKey chunkKey, Peer peer){
		if(chunkDB.get(chunkKey).getPeerList().contains(peer)){
			chunkDB.get(chunkKey).removePeer(peer);
			
			PeerService.saveDatabase();
		}
	}
	
	public synchronized int getChunkNofPeers(ChunkKey chunkKey){
		return chunkDB.get(chunkKey).getPeerList().size();
	}
	
	
	/*
	 * restorable files database
	 */
	public synchronized void addRestorableFile(String filename, FileDetails fileDetails){
		restorableFiles.put(filename, fileDetails);
		
		PeerService.saveDatabase();
	}
	
	public synchronized void removeRestorableFile(String filename){
		restorableFiles.remove(filename);
		
		PeerService.saveDatabase();
	}
	
	public synchronized boolean fileWasSaved(String filename){
		return restorableFiles.containsKey(filename);
	}
	
}