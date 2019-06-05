package diary.blockchain;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockChainIterator implements Iterator<Block> {

	private ArrayList<Block> chain;
	private int current;

	public BlockChainIterator(ArrayList<Block> chain) {
		this.chain = chain;
		current = -1;
	}

	@Override
	public boolean hasNext() {
		if (chain.size() > current + 1) {
			return true;
		}
		return false;
	}

	@Override
	public Block next() {
		if (hasNext()) {
			current++;
			return chain.get(current);
		}
		return null;
	}

}
