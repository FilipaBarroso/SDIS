package database;

import java.io.Serializable;
import java.util.ArrayList;

import service.Peer;

public class ChunkDetails implements Serializable {
	private static final long serialVersionUID = 3L;
	
	private int repD;
	private ArrayList<Peer> peerList;
	
	public ChunkDetails(int r, ArrayList<Peer> p){
		setRepD(r);
		setPeerList(p);
		
	}

	public int getRepD() {
		return repD;
	}

	public void setRepD(int repD) {
		this.repD = repD;
	}

	public ArrayList<Peer> getPeerList() {
		return peerList;
	}

	public void setPeerList(ArrayList<Peer> peersList) {
		this.peerList = peersList;
	}
	
	public void removePeer(Peer peer){
		peerList.remove(peer);
	}

}
