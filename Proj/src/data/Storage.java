package data;

import java.io.Serializable;
import service.PeerService;

public class Storage implements Serializable{
	private static final long serialVersionUID = 3L;
	
	//1000 Bytes
	private static final int DEFAULT_STORAGE = 1000;
	
	private static int totalStorage;
	private static int usedStorage;
	
	public Storage(){
		this.totalStorage=DEFAULT_STORAGE;
		this.usedStorage=0;
	}

	public synchronized int getTotalStorage() {
		return totalStorage;
	}

	public synchronized void setTotalStorage(int ts) {
		totalStorage = ts;
		
		PeerService.saveStorage();
	}

	public synchronized int getUsedStorage() {
		return usedStorage;
	}
	
	public synchronized static void setUsedStorage(int us) {
		usedStorage = us;
	}

	public synchronized static int getFreeStorage(){
		return totalStorage - usedStorage;
	}
	
	public synchronized void addStorage(int storage){
		int newStorage = totalStorage + storage;
		setTotalStorage(newStorage);
	}
	
	public synchronized static boolean addFile(int fileSize){
		if(fileSize <= getFreeStorage()){
			int newUsedStorage = usedStorage + fileSize;
			
			setUsedStorage(newUsedStorage);
			PeerService.saveStorage();
			return true;
		}
		else{
			System.out.println("STORAGE: Not enough local space for this file");
			return false;
		}
	}
	
	public synchronized static void removeFile(int fileSize){
		int newUsedStorage = usedStorage - fileSize;
		setUsedStorage(newUsedStorage);
		
		PeerService.saveStorage();
	}
}