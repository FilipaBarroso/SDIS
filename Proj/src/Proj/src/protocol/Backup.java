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


public class Backup implements Runnable {

	private File file;
	private String fileID;
	private int replicationDegree;

	public Backup(File file, int replicationDegree) {
		this.file = file;
		this.replicationDegree = replicationDegree;

		this.fileID = Files.getFileID(file);
	}

	public void run() {
		Chunk[] chunkArray;
		try {
			chunkArray = getChunks(file);

			backupChunks(chunkArray);	
			
			// save file on database
			FileDetails fd = new FileDetails(fileID, chunkArray.length);
			PeerService.getDatabase().addRestorableFile(file.getName(), fd);
			System.out.println("BACKUP: Saved file on database\n--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Chunk[] getChunks(File file) throws Exception {
		byte[] fileData = Files.getFileData(file);

		// num_chunk deve ter um ultimo chunk vazio
		int num_chunks = (int) (Math.floor(file.length() / Chunk.MAX_SIZE) + 1);
		Chunk[] chunkArray = new Chunk[num_chunks];

		ByteArrayInputStream fileStream = new ByteArrayInputStream(fileData);
		byte[] chunkStream = new byte[Chunk.MAX_SIZE];

		int i;
		for(i=0; i < num_chunks - 1; i++) {
			byte[] tmp_chunk;
			fileStream.read(chunkStream, 0, chunkStream.length);
			tmp_chunk = Arrays.copyOfRange(chunkStream, 0, Chunk.MAX_SIZE);	

			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, tmp_chunk);
			chunkArray[i] = chunk;
		}

		if(file.length() % Chunk.MAX_SIZE == 0) {
			byte[] empty_chunk = new byte[0];
			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, empty_chunk);
			chunkArray[i] = chunk;
		}

		else {
			byte[] tmp_chunk;
			int bytesRead = fileStream.read(chunkStream, 0, chunkStream.length);
			tmp_chunk = Arrays.copyOfRange(chunkStream, 0, bytesRead);	

			Chunk chunk = new Chunk(this.fileID, i, this.replicationDegree, tmp_chunk);
			chunkArray[i] = chunk;
		}
		
		fileStream.close();

		return chunkArray;
	}

	public void backupChunks(Chunk[] chunkArray) throws Exception {
		int j;
		System.out.println("BACKUP: Number of chunks created = " + chunkArray.length + "\n");
		for(j=0; j < chunkArray.length; j++) {		
			int wait_time = 1000;
			int num_tries = 0;
			
			ChunkKey ck = new ChunkKey(chunkArray[j].getNo(), chunkArray[j].getFileID());

			while(num_tries < 5) {
				// sendPUTCHUNK also gets the MC ready to count the STORED confirmations for that chunk	
				Protocol.sendPUTCHUNK(chunkArray[j], ck);

				// wait for STORED confirms
				System.out.println("PROTOCOL: Waiting for STORED confirms for " + wait_time);
				Thread.sleep(wait_time);

				int numStoredConfs = MC.getNumStoredConfs(ck);
				
				if(numStoredConfs >= replicationDegree)	break;
				
				System.out.println("BACKUP: Failed to get desirable replication degree.. trying again\n--------------------");
				wait_time *= 2;
				num_tries++;
			}
			
			System.out.print("BACKUP: Saved chunk");
			if(num_tries < 5) System.out.print(" with a desirable replication degree");
			System.out.println("\n--------------------");
			
			MC.deleteStoredConfs(ck);
		}
				
		// debbugging, guarda-se os chunks localmente
		//Files.loadChunks(chunkArray, this.file.getName(), chunk.getFileID());
	}

}
