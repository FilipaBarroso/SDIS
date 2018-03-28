package database;

import java.io.Serializable;
import java.util.ArrayList;

public class ChunkDetails implements Serializable {
	private static final long serialVersionUID = 3L;
	
	private int repD;
	private ArrayList<PeerKey> peerList;
	
	public ChunkDetails(int r, ArrayList<PeerKey> p){
		setRepD(r);
		setPeerList(p);
		
	}

	public int getRepD() {
		return repD;
	}

	public void setRepD(int repD) {
		this.repD = repD;
	}

	public ArrayList<PeerKey> getPeerList() {
		return peerList;
	}

	public void setPeerList(ArrayList<PeerKey> peersList) {
		this.peerList = peersList;
	}
	
	public void removePeer(PeerKey peerKey){
		peerList.remove(peerKey);
	}

}
