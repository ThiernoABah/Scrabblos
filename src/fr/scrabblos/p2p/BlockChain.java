package fr.scrabblos.p2p;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain {
	
	public static BlockChain BLOCKCHAIN = new BlockChain();
	public static int MAX_TOUR = 100;
	
	Vector<Lettre> lettrePool= new Vector<Lettre>();
	Vector<Mots> motsCandidat= new Vector<Mots>();
	
	Vector<Block> blockChain = new Vector<Block>();
	
	public ReentrantLock lock = new ReentrantLock();
	public ReentrantLock ajout = new ReentrantLock();
	
	
	public boolean newBlock = false;
	
	public int tour = 0;
	public int difficulty = 4;
	
	public BlockChain() {
		Vector<Lettre> genese = new Vector<>();
		Lettre a =new Lettre('a',-1);
		Lettre b =new Lettre('a',-1);
		Lettre c =new Lettre('a',-1);
		Lettre d =new Lettre('a',-1);
		a.blockHash="head";
		b.blockHash="head";
		c.blockHash="head";
		d.blockHash="head";
		genese.add(a);
		genese.add(b);
		genese.add(c);
		genese.add(d);
		Mots bc = new Mots(genese,-1,"head");
		blockChain.add(new Block(tour, "head",-1,bc));
	}
	
	public Vector<Lettre> getLetterPool(){
		return lettrePool;
	}
	public Vector<Mots> getMotsCandidat(){
		return motsCandidat;
	}
	public Vector<Block> getBlockChain(){
		return blockChain;
	}
	public void injectLetter(Lettre l) {
		lettrePool.add(l);
	}
	public void injectMot(Mots m) {
		motsCandidat.add(m);
	}
	
	public String toString() {
		String res = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		res += "turn : "+tour+"\n";
		for(Block b : this.blockChain) {
			res += b.toString()+"\n";
		}
		res += "number of letter : "+lettrePool.size()+"\n";
		res += ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		return res;
	}
	

}
