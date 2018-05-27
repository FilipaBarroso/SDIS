package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

import blockchain.TransactionOutput;
import blockchain.Wallet;

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

	public Database(){
		setUTXOs(new HashMap<String,TransactionOutput>());
		setWallets(new ArrayList<Wallet>());

	}

	/*
	 * wallets
	 */
	public synchronized ArrayList<Wallet> getWallets() {
		return wallets;
	}

	public synchronized void setWallets(ArrayList<Wallet> wallets) {
		this.wallets = wallets;
	}

	public synchronized void addWallet(Wallet wallet){
			wallets.add(wallet);
	}

	public synchronized boolean userHasWallet(User user){
		for(int i=0; i < wallets.size(); i++){
			if(wallets.get(i).owner == user)
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
	}

	public synchronized void addUTXOs(String string, TransactionOutput tOut){
		UTXOs.put(string, tOut);
	}

	public synchronized void removeUTXO(String string){
		UTXOs.remove(string);
	}
}
