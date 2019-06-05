package diary.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import diary.blockchain.Block;
import diary.blockchain.Blockchain;

public class FileWorker {
	private File workingDirecktory;
	private Blockchain chain;

	private File[] listOfFiles;
	private ArrayList<File> badFiles = new ArrayList<File>();
	private ArrayList<Block> corruptedBlocks = new ArrayList<Block>();

	public FileWorker(File workingDirecktory) {
		this.workingDirecktory = workingDirecktory;
		chain = new Blockchain();
		scanAllBlocks();
		System.out.println(this.workingDirecktory.getPath());
	}

	public void setWorkingDirectory(File workingDirecktory) {
		this.workingDirecktory = workingDirecktory;
		chain = new Blockchain();
		scanAllBlocks();
	}

	public String[] getListOfBlocks() {
		String[] list = new String[chain.size()];

		Iterator block = chain.iterator();
		int i = 0;
		while (block.hasNext()) {
			Block b = (Block) block.next();
			list[i] = b.getDate();
			i++;
		}

		return list;
	}

	public String getBlockTextByIndex(int index) {
		Iterator block = chain.iterator();
		int i = 0;
		while (block.hasNext()) {
			Block b = (Block) block.next();
			if (i == index) {
				return b.getData();
			}
			i++;
		}
		return "";
	}

	public void createBlock(String text) {
		Block block = new Block(text, chain.getLastHash());
		chain.addBlock(block);
		writeBlockToFileSystem(block);
	}

	private void writeBlockToFileSystem(Block block) {
		String fileContent = block.toString();
		String fileName = block.getDate() + ".txt";
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(workingDirecktory.getPath() + "\\" + fileName));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			System.out.println("IO exception, write block to file system");
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				System.out.println("Cant create writer");
				e.printStackTrace();
			}
		}

	}

	private void scanAllBlocks() {
		chain = new Blockchain();
		listOfFiles = workingDirecktory.listFiles();

		ArrayList<Block> blocks = new ArrayList<Block>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Block block = parseBlockFromFile(listOfFiles[i]);
				if (block != null) {
					blocks.add(block);
				}
			}
		}

		int blocksPrevSize;
		do {
			blocksPrevSize = blocks.size();
			Iterator<Block> block = blocks.iterator();
			while (block.hasNext()) {
				if (chain.addBlock(block.next())) {
					block.remove();
				}
			}
		} while (blocks.size() > 0 && blocksPrevSize != blocks.size());
		corruptedBlocks = blocks;
	}

	private Block parseBlockFromFile(File file) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("try to read block from file but file not found");
			e.printStackTrace();
		}

		StringBuffer str = new StringBuffer();
		String st;
		try {
			while ((st = br.readLine()) != null) {
				str.append(st);
				str.append("\n");
			}
			br.close();
		} catch (IOException e) {
			System.out.println("IO Exception, when read block from file");
			e.printStackTrace();
		}

		String strBlock = str.toString();
		System.out.println("File: " + file.getAbsolutePath());
		System.out.println("String of a block from file" + strBlock);
		Block parsedBlock = null;
		try {
			parsedBlock = new Block(strBlock);
			System.out.println("PARSED: " + parsedBlock);

		} catch (IllegalArgumentException e) {
			System.out.println("Bad file" + file.getAbsolutePath());
			if (!badFiles.contains(file)) {
				badFiles.add(file);
			}
		}
		return parsedBlock;
	}

	public String checkChain() {
		scanAllBlocks();
		return getStatus();
	}

	private String getStatus() {
		StringBuffer status = new StringBuffer("Статус: ");

		if (badFiles.size() > 0) {
			status.append("\n" + String.valueOf(badFiles.size()) + " файлов не распознанных как блоки:");
			for (File f : badFiles) {
				status.append("\n" + f.getAbsolutePath());
			}
		}

		if (corruptedBlocks.size() > 0) {
			status.append("\n" + String.valueOf(corruptedBlocks.size()) + " поврежденных блоков:");
			for (Block block : corruptedBlocks) {
				status.append("\n" + block.getDate());
			}
			status.append("\nЭти блоки НЕ добавлены в блокчейн");
		}

		if (corruptedBlocks.size() == 0) {
			status.append("\nЦепь целостна");
		}
		if (corruptedBlocks.size() == 0 && badFiles.size() == 0) {
			status.append("\nОшибок не обнаружено");
		}
		return status.toString();
	}

}
