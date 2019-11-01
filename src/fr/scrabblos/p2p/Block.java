package fr.scrabblos.p2p;

import java.util.ArrayList;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Block  {

	private int index;
	private Long timestamp;
	private String hash;
	private String previousHash;
	private int creator;
	public Mots mot;

	public Block() {
	}
	
	public Block(int index, String preHash, int creator, Mots mot) {
		this.index = index;
		this.previousHash = preHash;
		this.creator = creator;
		this.mot = mot;
		timestamp = System.currentTimeMillis();
		this.hash = calculateHash(previousHash);
		for(Lettre l : this.mot.mot){
			l.blockHash = this.hash;
		}
		mot.blockHash = this.hash;

	}
	private String calculateHash(String text) {
		byte[] hash = new byte[256];
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
		byte[] digest = digestSHA3.digest(hash);
		return Hex.toHexString(digest);
	}
	public String hashSeed() {
		int result = index;
		result = 31 * result + timestamp.hashCode();
		result = 31 * result + hash.hashCode();
		result = 31 * result + previousHash.hashCode();
		result = 31 * result + mot.hashCode();
		return ""+result;
	}
	
	public boolean isValid(int difficulte) {
		if(mot.mot.size()<difficulte) {
			return false;
		}
		ArrayList<Integer> dejaVu = new ArrayList<>(mot.mot.size());
		for(Lettre l : mot.mot) {
			if(dejaVu.contains(l.publicKey)) {
				return false;
			}
			dejaVu.add(l.publicKey);
			if(!l.blockHash.equals(this.hash)){
				return false;
			}
		}
		if(!mot.blockHash.equals(this.hash)) {
			return false;
		}
		return true;
	}
	

	@Override
	public String toString() {
		return "Block{" + "index=" + index + ", timestamp=" + timestamp + ", creator=" + creator + ", mot='" 
				+ mot.toString() + ", hash='"+hash+ '}';
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
		if (block.mot.mot.size() != mot.mot.size()) {
			sameWord = false;
		} else {
			for (int i = 0; i < block.mot.mot.size(); i++) {
				{
					if (mot.mot.get(i) != block.mot.mot.get(i)) {
						sameWord = false;
						break;
					}
					if (!mot.mot.get(i).blockHash.equals(block.mot.mot.get(i).blockHash)) {
						sameWord = false;
						break;
					}
				}
			}

		}
		if(block.mot.blockHash != mot.blockHash) {
			return false;
		}
		return sameWord && index == block.index && timestamp.equals(block.timestamp) && hash.equals(block.hash)
				&& previousHash.equals(block.previousHash) && creator == block.creator;
	}

	public int getCreator() {
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
	
}
