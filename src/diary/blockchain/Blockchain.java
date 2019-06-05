package diary.blockchain;

import java.util.ArrayList;
import java.util.Iterator;

public class Blockchain implements Iterable {

	private ArrayList<Block> blockchain = new ArrayList<Block>();

	public boolean isChainValid() {
		Block currentBlock = null;
		Block previousBlock = null;
		String error = null;
		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				error = "Current Hashes not equal";
				break;
			}

			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				error = "Previous Hashes not equal";
				break;
			}
		}

		if (error != null) {
			System.out.println(error);
			System.out.println("Current block:");
			System.out.println(currentBlock.toString());
			System.out.println("Previous block:");
			System.out.println(previousBlock.toString());
			return false;
		}
		return true;

	}

	public boolean addBlock(Block block) {
		if (isChainValid() && block.getHash().equals(block.calculateHash())
				&& block.getPreviousHash().equals(getLastHash())) {
			blockchain.add(block);
			return true;
		}
		return false;
	}

	public String getLastHash() {
		if (blockchain.size() > 0 && isChainValid()) {
			return blockchain.get(blockchain.size() - 1).getHash();
		}
		return "0";
	}

	public ArrayList<Block> getBlockChain() {
		return this.blockchain;
	}

	public int size() {
		return blockchain.size();

	}

	@Override
	public Iterator iterator() {
		return new BlockChainIterator(blockchain);
	}

}
