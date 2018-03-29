package database;

import java.io.Serializable;

public class FileDetails implements Serializable{
	private static final long serialVersionUID = 3L;
	
	private String fileID;


	private int nChunks;
	
	public FileDetails(String fileID, int nChunks){
		setFileID(fileID);
		setnChunks(nChunks);
	}
	
	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	
	public int getnChunks() {
		return nChunks;
	}

	public void setnChunks(int nChunks) {
		this.nChunks = nChunks;
	}
	
	@Override
	public String toString(){
		return fileID + " (" + nChunks + " chunks" + ")";
	}
}
