package fr.scrabblos;

import java.io.Serializable;
import java.util.ArrayList;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Block implements Serializable {
	private static final long serialVersionUID = 1L;

	private int index;
	private Long timestamp;
	private String hash;
	private String previousHash;
	private String creator;
	private Mot mot;

	public Block() {
	}
	
	public Block(int index, String preHash, String creator, Mot mot) {
		this.index = index;
		this.previousHash = preHash;
		this.creator = creator;
		this.mot = mot;
		
		timestamp = System.currentTimeMillis();
		hash = calculateHash(previousHash);
	}
	private String calculateHash(String text) {
		byte[] hash = { 0, 1, 0, 0, 1 }; 
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
		byte[] digest = digestSHA3.digest(hash);
		return Hex.toHexString(digest);
	}
	
	public boolean isValid() {
		ArrayList<Integer> dejaVu = new ArrayList<>();
		for(Lettre l : mot.mot) {
			if(dejaVu.contains(l.author)) {
				return false;
			}
			//dejaVu.add(l.author);
			if(!l.head.equals(this.hash)){
				return false;
			}
		}
		if(!mot.head.equals(this.hash)) {
			return false;
		}
		return true;
	}
	

	@Override
	public String toString() {
		return "Block{" + "index=" + index + ", timestamp=" + timestamp + ", creator=" + creator + ", mot='"
				+ mot.toString() + '}';
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Block block = (Block) o;
		if(block.mot.mot.size()!=this.mot.mot.size()) {
			return false;
		}
		if(!mot.head.equals(block.mot.head)) {
			return false;
		}
		for(int i = 0;i<this.mot.mot.size();i++) {
			if(!mot.mot.get(i).equals(block.mot.mot)) {
				return false;
			}
		}
		
		return index == block.index && timestamp.equals(block.timestamp) && hash.equals(block.hash)
				&& previousHash.equals(block.previousHash) && creator.equals(block.creator);
	}

	public String getCreator() {
		return creator;
	}

	public int getIndex() {
		return index;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}
	
	public int getScore() {
		Score s = new Score();
		
		int res = 0;
		
		for(Lettre l : mot.mot) {
			res += s.tableauScore.get(String.valueOf(l.lettre));
		}
		
		return res;
	}

	
	
}
