package peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.util.Random;

import blockchain.Wallet;

/*
 * connect to multicast channel thread
 * ask for user input regarding transactions, checking wallet balance etc..
 */
public class User {
	
	public Wallet user_wallet;
	public String user_name;
	
	public User(String user_name) {
		this.user_name = user_name;
	}
	
	public void addWallet(Wallet userwallet) {
		this.user_wallet = userwallet;
	}
	
	// runnable
	// options:
	// send to_wallet value
	// check balance
}
