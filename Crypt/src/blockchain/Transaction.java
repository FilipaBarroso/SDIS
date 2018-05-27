	package blockchain;

	import java.io.IOException;
	import java.net.DatagramPacket;
	import java.net.InetAddress;
	import java.net.MulticastSocket;
	import java.security.InvalidKeyException;
	import java.security.MessageDigest;
	import java.security.NoSuchAlgorithmException;
	import java.security.NoSuchProviderException;
	import java.security.PrivateKey;
	import java.security.PublicKey;
	import java.security.Signature;
	import java.util.ArrayList;
	import java.util.Random;

	public class Transaction {

		public String id;
		public PublicKey sender, recipient;
		public float value;
		public byte[] signature;

		public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

		private static int transaction_counter = 0;

		public Transaction() {
			//empty transaction
		}

		public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs) {
			this.sender = sender;
			this.recipient = recipient;
			this.value = value;
			this.inputs = inputs;
		}

		public boolean processTransaction() {

			if(!verifySignature()) {
				System.out.println("TRANSACTION ERROR: transaction signature couldn't be verified");
				return false;
			}

			// make sure transaction inputs are unspent
			if(inputs != null) { // make sure it isnt the genesis block
				for(TransactionInput ti : inputs) {
					ti.UTXO = Cryptocoin.getDatabase().getUTXOs().get(ti.transOutputID);
				}


			// make sure transaction value is enough
			if(getTotalInputs() < Cryptocoin.minimunTransactionAmount) {
				System.out.println("TRANSACTION ERROR: transaction amount is too small.. (below " + Cryptocoin.minimunTransactionAmount + ")");
				return false;
			}

			// generate trans outputs
			float leftover = getTotalInputs() - value;
			generateTransID();
			outputs.add(new TransactionOutput(this.recipient, value, id));
			outputs.add(new TransactionOutput(this.sender, leftover, id));



			// remove inputs from unspent list (because they're being spent)
			for(TransactionInput ti : inputs) {
				if(ti.UTXO == null) continue;
				Cryptocoin.removeUTXOfromDB(ti.UTXO.id);
			}
		}

		// add outputs to unspent list
		for(TransactionOutput to : outputs) {
			Cryptocoin.addUTXOtoDB(to.id, to);
		}
			return true;
		}

		//returns sum of inputs(UTXOs)
		public float getTotalInputs() {
			float total = 0;

			for(TransactionInput ti : inputs) {
				if(ti.UTXO == null) continue; //if Transaction can't be found skip it
				total += ti.UTXO.value;
			}

			return total;
		}

		//returns sum of outputs
		public float getTotalOutputs() {
			float total = 0;

			for(TransactionOutput to : outputs) {
				total += to.value;
			}

			return total;
		}

		public void generateTransID() {
			// avoiding equal transactions from having the same ID
			transaction_counter++;

			id = Cryptocoin.sha256(
					Cryptocoin.getKeyAsString(sender)
					+ Cryptocoin.getKeyAsString(recipient)
					+ Float.toString(value)
					+ transaction_counter);
		}

		public void generateSignature(PrivateKey privKey) {
			try {
				Signature dsa;
				String data = Cryptocoin.getKeyAsString(sender) + Cryptocoin.getKeyAsString(recipient) + Float.toString(value);

				dsa = Signature.getInstance("ECDSA", "BC");

				dsa.initSign(privKey);

				byte[] strBytes = data.getBytes();
				dsa.update(strBytes);

				byte[] tmp_sig = dsa.sign();
				signature = tmp_sig;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean verifySignature() {
			String data = Cryptocoin.getKeyAsString(sender) + Cryptocoin.getKeyAsString(recipient) + Float.toString(value);

			return Cryptocoin.verifyECDSA(sender, data, signature);
		}
	}
