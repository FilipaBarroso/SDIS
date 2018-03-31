package protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Arrays;

import channels.MC;
import data.Files;
import database.ChunkKey;
import database.FileDetails;
import service.Chunk;
import service.PeerService;


public class Delete implements Runnable {

	private String filename;
	private String fileID;
	private int num_chunks;
	
	public Delete(String filename, FileDetails fd) {
		this.filename = filename;
		this.fileID = fd.getFileID();
		num_chunks = fd.getnChunks();
	}

	public void run() {
		try {
			// send a DELETE msg
			Protocol.sendDELETE(PeerService.getLocalPeer(), fileID);
			
			// delete file from RESTORED_FILES and from database
			Files.deleteRestoredFile(filename);
			PeerService.getDatabase().removeRestorableFile(filename);
			System.out.println("DELETE: " + filename + " has been succesfully deleted\n--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
