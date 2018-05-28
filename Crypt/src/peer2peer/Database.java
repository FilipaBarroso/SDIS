package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

import blockchain.TransactionOutput;
import blockchain.Wallet;
import blockchain.Cryptocoin;

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
	private volatile int user_port;

	public Database(){
		setUTXOs(new HashMap<String,TransactionOutput>());
		setWallets(new ArrayList<Wallet>());
		user_port = 8001;
	}

	/*
	 * user port
	 */
	 public synchronized int getUserPort() {
		 return user_port;
	 }

	 public synchronized void updateUserPort(int p) {
		 this.user_port = p;
		 Cryptocoin.saveDatabase();
	 }

	/*
	 * wallets
	 */
	public synchronized ArrayList<Wallet> getWallets() {
		return wallets;
	}

	public synchronized void setWallets(ArrayList<Wallet> wallets) {
		this.wallets = wallets;
		Cryptocoin.saveDatabase();
	}

	public synchronized void addWallet(Wallet wallet){
			wallets.add(wallet);
			Cryptocoin.saveDatabase();
	}

	public synchronized boolean userHasWallet(User user){
		for(int i=0; i < wallets.size(); i++){
			if(wallets.get(i).owner.username.equals(user.username))
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
		Cryptocoin.saveDatabase();
	}

	public synchronized void addUTXOs(String string, TransactionOutput tOut){
		UTXOs.put(string, tOut);
		Cryptocoin.saveDatabase();
	}

	public synchronized void removeUTXO(String string){
		UTXOs.remove(string);
		Cryptocoin.saveDatabase();
	}
}
