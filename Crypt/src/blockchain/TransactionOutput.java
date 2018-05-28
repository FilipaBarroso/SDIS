package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;
import java.io.Serializable;

public class TransactionOutput implements Serializable {

	private static final long serialVersionUID = 3L;

	public String id;
	public PublicKey recipient;
	public float value;
	public String parentTransID;

	public TransactionOutput(PublicKey recipient, float value, String parentTransID) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransID = parentTransID;
		this.id = Cryptocoin.sha256(
				Cryptocoin.getKeyAsString(recipient)
				+ Float.toString(value)
				+ parentTransID
				);
	}

	public boolean belongsTo(PublicKey pubK) {
		return pubK == recipient;
	}
}
