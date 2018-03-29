package data;

import java.io.Serializable;
import service.PeerService;

public class Storage implements Serializable{
	private static final long serialVersionUID = 3L;
	
	//10 GB
	private static final int DEFAULT_STORAGE = 10485760;
	
	private int totalStorage;
	private int usedStorage;
	
	public Storage(){
		this.totalStorage=DEFAULT_STORAGE;
		this.usedStorage=0;
	}

	public synchronized int getTotalStorage() {
		return totalStorage;
	}

	public synchronized void setTotalStorage(int totalStorage) {
		this.totalStorage = totalStorage;
		
		PeerService.saveStorage();
	}

	public synchronized int getUsedStorage() {
		return usedStorage;
	}
	
	public synchronized void setUsedStorage(int usedStorage) {
		this.usedStorage = usedStorage;
	}

	public synchronized int getFreeStorage(){
		return totalStorage - usedStorage;
	}
	
	public synchronized void addStorage(int storage){
		int newStorage = totalStorage + storage;
		setTotalStorage(newStorage);
	}
	
	public synchronized void addFile(int fileSize){
		if(fileSize <= getFreeStorage()){
			int newUsedStorage = usedStorage + fileSize;
			
			setUsedStorage(newUsedStorage);
			PeerService.saveStorage();
			
		}
		else{
			System.out.println("Not enough storage for this file");
		}
	}
	
	public synchronized void removeFile(int fileSize){
		int newUsedStorage = usedStorage - fileSize;
		setUsedStorage(newUsedStorage);
		
		PeerService.saveStorage();
	}
	
	
}
