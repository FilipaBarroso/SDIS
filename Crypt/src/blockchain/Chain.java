package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.*;

public class Chain {

	private static ArrayList<Block> blockchain;
	private Random rand = new Random();
	public static Transaction genesisTransaction;
	public static Block genesis_block;
	public static Wallet bank, genesisWallet;
	public static float miningReward = (float)(Cryptocoin.miningDifficulty); // the reward is the same value as the difficultry
	public static float bankFunds = 1000000000f;
	public Block currentBlock; // this block is always the one next to be added. It's created after the current one is mined and added to the chain
	public Block tmpRewardBlock; // this block exists as a temporary block while the current one is being added, during mining

	public Chain() {
		Chain.setChain(new ArrayList<Block>());

		// adds an empty block so the chain isn't empty
		genesis_block = new Block("0");

		genesisWallet = new Wallet("genesis", 0);
		bank = new Wallet("bank", 1);

		// create genesis transaction, which fills the bank up
		genesisTransaction = new Transaction(genesisWallet.publicKey, bank.publicKey, bankFunds, null);
		genesisTransaction.generateSignature(genesisWallet.privateKey);	 //manually sign the genesis transaction
		genesisTransaction.id = "0"; //manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.id)); //manually add the Transactions Output
		Cryptocoin.addUTXOtoDB(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
		System.out.println("Bank funds = " + bank.getBalance());

		genesis_block.addTransaction(genesisTransaction);
		blockchain.add(genesis_block);
		System.out.println("\nAdded genesis block with hash: " + genesis_block.hash);
		currentBlock = new Block(genesis_block.hash);
	}

	public static ArrayList<Block> getChain() {
		return blockchain;
	}

	public static void setChain(ArrayList<Block> blockchain) {
		Chain.blockchain = blockchain;
	}

	public int getStake() {
		int stake = 0;
		for(Wallet w : Cryptocoin.getDatabase().getWallets()) {
			stake += w.getBalance();
		}

		//System.out.println("Stake is " + stake);

		return stake;
	}

	public Wallet getStakeWinner(int n) {
		Wallet winner = null;
		int i;

		if(n < Cryptocoin.getDatabase().getWallets().get(0).getBalance())
			return Cryptocoin.getDatabase().getWallets().get(0);

		for(i = 1; i <= Cryptocoin.getDatabase().getWallets().size(); i++) {
			if(n >= Cryptocoin.getDatabase().getWallets().get(i-1).getBalance())
				if(n <= Cryptocoin.getDatabase().getWallets().get(i).getBalance())
					return Cryptocoin.getDatabase().getWallets().get(i);
		}

		return winner;
	}

	public void addCurrentBlock() {
		String mined_hash = currentBlock.mineBlock();
		int n = rand.nextInt(getStake());
		//System.out.println("Rand is " + n);
		Wallet winner = getStakeWinner(n);
		giveMiningReward(winner, mined_hash);

		Chain.blockchain.add(currentBlock);
		currentBlock = tmpRewardBlock;

		System.out.println("Added block to blockchain\n");

		if(!isChainValid()) {
			System.out.println("ERROR: chain invalid .. removing block\n");
			blockchain.remove(currentBlock);
		}
	}

	public void giveMiningReward(Wallet w, String prevHash) {
		// create a new block, to store this new reward transactionUTXO
		System.out.println("Giving mining reward of " + miningReward + " to " + w.owner.username);
		tmpRewardBlock = new Block(prevHash);
		tmpRewardBlock.addTransaction(bank.sendFunds(w.publicKey, miningReward));
	}

	public void printChain() {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
	}

	public String getLastHash() {
		return blockchain.get(blockchain.size()).hash;
	}

	public boolean isChainValid() {
		Block curr, prev;
		int i;
		String targetHash = new String(new char[Cryptocoin.miningDifficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		for(i=1; i < blockchain.size(); i++) {
			curr = blockchain.get(i);
			prev = blockchain.get(i-1);

			// validate hashes
			if(!curr.hash.equals(curr.calculateHash())) {
				System.out.println("CHAIN ERROR: this block is defective - " + curr.hash);
				return false;
			}

			if(!prev.hash.equals(curr.previousHash)) {
				System.out.println("CHAIN ERROR: this block doesn't match with the rest of the chain\ncurrent's previous hash: " + curr.previousHash + "\nprevious hash: " + prev.hash);
				return false;
			}

			if(!curr.hash.substring(0, Cryptocoin.miningDifficulty).equals(targetHash)) {
				System.out.println("CHAIN ERROR: there's a block that hasn't been mined yet");
			}

			// validate transactions
			TransactionOutput tempOutput;
			for(int t=0; t < curr.transaction_list.size(); t++) {
				Transaction currTransaction = curr.transaction_list.get(t);

				if(!currTransaction.verifySignature()) {
					System.out.println("CHAIN ERROR: couldn't verify a signature for a transaction in this block");
					return false;
				}

				if(currTransaction.getTotalInputs() != currTransaction.getTotalOutputs()) {
					System.out.println("CHAIN ERROR: inputs don't equal outputs for a transaction in this block");
					return false;
				}

				for(TransactionInput ti : currTransaction.inputs) {
					tempOutput = tempUTXOs.get(ti.transOutputID);

					if(tempOutput == null) {
						System.out.println("CHAIN ERROR: input is missing on a transaction");
						return false;
					}

					if(ti.UTXO.value != tempOutput.value) {
						System.out.println("CHAIN ERROR: value is invalid on a transaction");
						return false;
					}

					tempUTXOs.remove(ti.transOutputID);
				}

				for(TransactionOutput to : currTransaction.outputs) {
					tempUTXOs.put(to.id, to);
				}

				if(currTransaction.outputs.get(0).recipient != currTransaction.recipient) {
					System.out.println("CHAIN ERROR: recipient mismatch on a transaction");
					return false;
				}

				if(currTransaction.outputs.get(1).recipient != currTransaction.sender) {
					System.out.println("CHAIN ERROR: sender mismatch on a transaction");
					return false;
				}
			}
		}

		System.out.println("Chain is valid");

		return true;
	}
}
