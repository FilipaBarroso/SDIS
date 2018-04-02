package service;

/*
 * Contains info on file chunk
 */
public class Chunk {
	
	public static int MAX_SIZE = 6400;
	String fileID;
	int chunkNo;
	int repD;
	byte[] data;
	
	public Chunk(String fileID, int chunkNo, int repDegree, byte[] data) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.repD = repDegree;
		this.data = data;
	}
	
	public int getNo() {return chunkNo;}
	public byte[] getData() {return data;}
	public int getRepD() {return repD;}
	public String getFileID() {return fileID;}
}
