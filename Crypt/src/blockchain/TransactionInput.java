package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;

// empty for now
public class TransactionInput {
	
	public String transOutputID;
	// Unspent Transaction Output
	public TransactionOutput UTXO;
	
	public TransactionInput(String transOutputID) {
		this.transOutputID = transOutputID;
	}
	
}
