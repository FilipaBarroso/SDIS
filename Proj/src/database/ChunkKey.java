package database;

import java.io.Serializable;
import java.util.Objects;

public class ChunkKey  implements Serializable{
	
	private static final long serialVersionUID = 3L;
	
	private int chunkN;
	private String fileID;
	
	public ChunkKey(int chunkN, String fileID){
		this.chunkN = chunkN;
		this.fileID = fileID;
	}
	
	public int getChunkNo(){
		return chunkN;
	}
	
	public String getFileID(){
		return fileID;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		ChunkKey other = (ChunkKey) obj;

		if (chunkN != other.chunkN)
			return false;

		if (fileID == null && other.fileID != null){
			return false;
		} else if (!fileID.equals(other.fileID))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(chunkN, fileID);
	}


	

}
