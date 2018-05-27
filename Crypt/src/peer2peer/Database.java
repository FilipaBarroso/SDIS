package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

import blockchain.TransactionOutput;
import blockchain.Wallet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/*
 * maintains the blockchain stored
 * existing users and their wallets
 */
public class Database implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	//volatile to guarantee visibility across threads
	private volatile HashMap<String,TransactionOutput> UTXOs;
	private volatile ArrayList<Wallet> wallets;
	
	public Database(){
		setUTXOs(new HashMap<String,TransactionOutput>());
		setWallets(new ArrayList<Wallet>());
		
	}
	
	/*
	 * wallets
	 */
	public synchronized ArrayList<Wallet> getWallets() {
		return wallets;
	}
	
	public synchronized void setWallets(ArrayList<Wallet> wallets) {
		this.wallets = wallets;
	}
	
	public synchronized void addWallet(Wallet wallet, User user){
		if(!userHasWallet(user))
			wallets.add(wallet);
	}
	
	public synchronized boolean userHasWallet(User user){
		for(int i=0; i < wallets.size(); i++){
			if(wallets.get(i).getOwner() == user)
				return true;
		}
		return false;
	}
	
	/*
	 * UTXOs
	 */
	public synchronized HashMap<String, TransactionOutput> getUTXOs() {
		return UTXOs;
	}

	public synchronized void setUTXOs(HashMap<String, TransactionOutput> UTXOs) {
		this.UTXOs = UTXOs;
	}
	
	public synchronized void addUTXOs(String string, TransactionOutput tOut){
		UTXOs.put(string, tOut);
	}
	
	public synchronized void removeUTXO(String string){
		UTXOs.remove(string);
	}

	
	/*
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
	*/
	
}
