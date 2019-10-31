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
	private ArrayList<Lettre> mot;

	public Block() {
	}
	
	public Block(int index, String preHash, String creator, ArrayList<Lettre> mot) {
		this.index = index;
		this.previousHash = preHash;
		this.creator = creator;
		this.mot = mot;
		
		timestamp = System.currentTimeMillis();
		hash = calculateHash(String.valueOf(index) + previousHash + String.valueOf(timestamp));

		for(Lettre l : this.mot){
			l.blockHash = this.hash;
		}

	}
	private String calculateHash(String text) {
		byte[] hash = { 0, 1, 0, 0, 1 }; // ?
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
		byte[] digest = digestSHA3.digest(hash);
		return Hex.toHexString(digest);
	}
	public String hashSeed() {
		int result = index;
		result = 31 * result + timestamp.hashCode();
		result = 31 * result + hash.hashCode();
		result = 31 * result + previousHash.hashCode();
		result = 31 * result + creator.hashCode();
		result = 31 * result + mot.hashCode();
		return ""+result;
	}
	
	public boolean isValid(int difficulté) {
		if(mot.size()<difficulté) {
			return false;
		}
		ArrayList<Integer> dejaVu = new ArrayList<>(mot.size());
		for(Lettre l : mot) {
			if(dejaVu.contains(l.publicKey)) {
				return false;
			}
			dejaVu.add(l.publicKey);
			if(l.blockHash != this.hash){
				return false;
			}
		}
		return true;
	}
	

	@Override
	public String toString() {
		return "Block{" + "index=" + index + ", timestamp=" + timestamp + ", creator=" + creator + ", mot='"
				+ motToString() + '}';
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
		boolean sameWord = true;
		if (block.mot.size() != mot.size()) {
			sameWord = false;
		} else {
			for (int i = 0; i < block.mot.size(); i++) {
				{
					if (mot.get(i) != block.mot.get(i)) {
						sameWord = false;
						break;
					}
					if (mot.get(i).blockHash != block.mot.get(i).blockHash) {
						sameWord = false;
						break;
					}
				}
			}

		}
		return sameWord && index == block.index && timestamp.equals(block.timestamp) && hash.equals(block.hash)
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

	public String motToString() {
		StringBuilder res = new StringBuilder();
		for (Lettre l : mot) {
			res.append(l.lettre);
		}
		return res.toString();
	}

	
}
