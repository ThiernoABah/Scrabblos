package fr.scrabblos.p2p;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class Politicien implements Runnable{
	private Score tableauScore = new Score();
	private BlockChain blockChain;
	
	private int publicKey;
	public Vector<Lettre> letterPool;
	public Vector<Mots> motsCandidat;
	
	public Trie trie;
	String[] motsPossible;
		
	public Politicien(int publicKey, BlockChain b,Trie rt) {
		
		this.letterPool = new Vector<>();
		this.trie = rt;
		this.blockChain = b;
		this.publicKey = publicKey;
//		String s = trie.getWordsSizeI(trie.root, 4);
//		this.motsPossible = s.split(";");
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		while(blockChain.lettrePool.size() > blockChain.difficulty && blockChain.tour < BlockChain.MAX_TOUR) {
			try {
				blockChain.injectMot(genMot());
				Thread.sleep(200);
				Mots bestMot = consensusMots(blockChain.getMotsCandidat()); // doivent tous elire le meme mots
				if(bestMot != null) {
					blockChain.ajout.lock();
					if(!blockChain.newBlock) {
						blockChain.tour ++;
						Block toAdd = new Block(blockChain.tour, blockChain.blockChain.get(blockChain.blockChain.size()-1).getHash(), bestMot.politicien,bestMot);
						blockChain.blockChain.add(toAdd); // new block avec le mot elu bestMot
						blockChain.newBlock = true;
						for(Lettre l :  bestMot.mot) {
							blockChain.lettrePool.remove(l);
						}
					}
					blockChain.motsCandidat = new Vector<>();
					blockChain.ajout.unlock();
				}
				Thread.sleep(1500);
				if(blockChain.newBlock) {
					blockChain.lock.lock();
					blockChain.newBlock = false;
					blockChain.lock.unlock();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	public Mots genMot() {
		// PoW
		letterPool = (Vector<Lettre>) blockChain.getLetterPool().clone();
		Collections.shuffle(letterPool);
		
//		ArrayList<String> s = new ArrayList<>();
//		for(Lettre l : letterPool) {
//			s.add(String.valueOf(l.lettre));
//		}
//		
//		for(int i=0; i<motsPossible.length;i++) {
//			ArrayList<String> c = new ArrayList<>();
//			for(char q :motsPossible[i].toCharArray() ) {
//				c.add(String.valueOf(q));
//			}
//			System.out.println(c.size());
//			c.retainAll(s);
//			System.out.println(c.size());
//			
//		}
		
		int i = 0;
		Vector<Lettre> mot = new Vector<>();
		
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
		if(vm.size()==0) {
			return null;
		}
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
			res += tableauScore.tableauScore.get(String.valueOf(l.lettre));
		}
		return res;
	}
	

}
