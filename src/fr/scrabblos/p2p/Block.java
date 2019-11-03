package fr.scrabblos.p2p;

import java.nio.charset.StandardCharsets;
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
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
		byte[] digest = digestSHA3.digest(text.getBytes(StandardCharsets.UTF_8));
		return Hex.toHexString(digest);
	}
	
	public boolean isValid(int difficulte) {
		if(mot.mot.size()<difficulte) {
			return false;
		}
		ArrayList<Integer> dejaVu = new ArrayList<>(mot.mot.size());
		for(Lettre l : mot.mot) {
			if(dejaVu.contains(l.author)) {
				return false;
			}
			dejaVu.add(l.author);
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
		return "Block{ " + "index = " + index + ", timestamp = " + timestamp + ", creator = " + creator + ", hash = '"+hash+ ", previousHash = '"+previousHash+ ", mot = '" 
				+ mot.toString() + " } ";
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
