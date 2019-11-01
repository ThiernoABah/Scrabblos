package fr.scrabblos.p2p;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class Politicien implements Runnable{
	private int privateKey;
	private int publicKey;
	
	private final HashMap<String,Integer> tableauScore = new HashMap<>();
	
	public Vector<Lettre> letterPool;
	public Mots motCandidat;
	public Vector<Mots> motsCandidat;
	public RadixTree radix;
	private BlockChain blockChain;
	
	public Vector<Politicien> politiciens = new Vector<>();
	
	public Politicien(int publicKey, BlockChain b,RadixTree rt) {
		tableauScore.put("a", 1);
		tableauScore.put("b", 3);
		tableauScore.put("c", 3);
		tableauScore.put("d", 2);
		tableauScore.put("e", 1);
		tableauScore.put("f", 4);
		tableauScore.put("g", 2);
		tableauScore.put("h", 4);
		tableauScore.put("i", 1);
		tableauScore.put("j", 8);
		tableauScore.put("k", 10);
		tableauScore.put("l", 1);
		tableauScore.put("m", 2);
		tableauScore.put("n", 1);
		tableauScore.put("o", 1);
		tableauScore.put("p", 3);
		tableauScore.put("q", 8);
		tableauScore.put("r", 1);
		tableauScore.put("s", 1);
		tableauScore.put("t", 1);
		tableauScore.put("u", 1);
		tableauScore.put("v", 4);
		tableauScore.put("w", 10);
		tableauScore.put("x", 10);
		tableauScore.put("y", 10);
		tableauScore.put("z", 10);
		
		this.letterPool = new Vector<>();
		this.radix = rt;
		this.blockChain = b;
		this.publicKey = publicKey;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(true) {
			try {
				
				blockChain.injectMot(genMot());
				Thread.sleep(100);
				Mots bestMot = consensusMots(blockChain.getMotsCandidat()); // doivent tous elire le meme mots
				blockChain.ajout.lock();
				if(!blockChain.newBlock) {
					blockChain.blockChain.add( new Block(blockChain.tour, blockChain.blockChain.get(blockChain.blockChain.size()-1).getHash(), bestMot.publicKey,bestMot) ); // new block avec le mot élu bestMot
					blockChain.newBlock = true;
					for(Lettre l :  blockChain.blockChain.get(blockChain.blockChain.size()-1).mot.mot) {
						l.blockHash = blockChain.blockChain.get(blockChain.blockChain.size()-1).getHash();
					}
				}
				blockChain.blockChain.get(blockChain.blockChain.size()-1).mot.blockHash = blockChain.blockChain.get(blockChain.blockChain.size()-1).getHash();
				blockChain.motsCandidat = new Vector<>();
				blockChain.ajout.unlock();
				
				Thread.sleep(1500);
				System.out.println(blockChain.blockChain.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	public Mots genMot() {
		letterPool = (Vector<Lettre>) blockChain.lettrePool.clone();
		int i = 0;
		Vector<Lettre> mot = new Vector<>();
		Collections.shuffle(letterPool);
		for(Lettre l : letterPool) {
			if(i>blockChain.difficulty) {
				break;
			}
			i++;
			mot.add(l);
		}
		Mots m = new Mots(mot,publicKey,blockChain.blockChain.get(blockChain.blockChain.size()-1).getHash());
		return m;
	}
	
	public Mots consensusMots(Vector<Mots> vm) {
		int max = 0;
		Mots res = vm.get(0);
		for(Mots m : vm) {
			if(scoreMot(m)>max) {
				max = scoreMot(m);
				res = m;
			}
		}
		return res;
	}
	
	public int scoreMot(Mots m) {
		int res = 0;
		for(Lettre l : m.mot) {
			res += tableauScore.get(String.valueOf(l.lettre));
		}
		return res;
	}
	

}
