package diary.blockchain;

import java.util.Date;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;

public class Block {
	private String hash;
	private String previousHash;
	private String data;
	private long timeStamp;

	private static final String HASH_LABEL = "HASH = ";
	private static final String PREV_HASH_LABEL = "PREV HASH = ";
	private static final String TIMESTAMP_LABEL = "TIMESTAMP = ";
	private static int HASH_LENGTH;
	private static int TIME_STAMP_LENGTH;

	{
		HASH_LENGTH = calculateHash().length();
		TIME_STAMP_LENGTH = Long.toString(new Date().getTime()).length();
	}

	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public Block(String strBlock) throws IllegalArgumentException {
		try {
			int indexOfHash = strBlock.indexOf(HASH_LABEL);
			int indexOfPrevHash = strBlock.indexOf(PREV_HASH_LABEL);
			int indexOfTimeStamp = strBlock.indexOf(TIMESTAMP_LABEL);

			this.data = strBlock.substring(0, indexOfHash - 1);
			this.hash = strBlock.substring(indexOfHash + HASH_LABEL.length(),
					indexOfHash + HASH_LABEL.length() + HASH_LENGTH);
			if ((indexOfTimeStamp - indexOfPrevHash) < HASH_LENGTH / 2) {
				this.previousHash = "0";
			} else {
				this.previousHash = strBlock.substring(indexOfPrevHash + PREV_HASH_LABEL.length(),
						indexOfPrevHash + PREV_HASH_LABEL.length() + HASH_LENGTH);
			}

			this.timeStamp = Long.parseLong(strBlock.substring(indexOfTimeStamp + TIMESTAMP_LABEL.length(),
					indexOfTimeStamp + TIMESTAMP_LABEL.length() + TIME_STAMP_LENGTH));
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public String calculateHash() {
		String calculatedHash = StringUtil.getSHA256(previousHash + Long.toString(timeStamp) + data);
		return calculatedHash;
	}

	@Override
	public String toString() {
		return data + "\n" + HASH_LABEL + hash + "\n" + PREV_HASH_LABEL + previousHash + "\n" + TIMESTAMP_LABEL
				+ timeStamp;
	}

	private static class StringUtil {
		public static String getSHA256(String input) {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(input.getBytes("UTF-8"));
				StringBuilder hexString = new StringBuilder();
				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if (hex.length() == 1) {
						hexString.append('0');
					}
					hexString.append(hex);
				}
				return hexString.toString();

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public String getData() {
		return data;
	}

	public String getDate() {
		Date date = new Date(this.timeStamp);
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMM HH-mm-ss");
		return dateFormat.format(date);
	}

}
