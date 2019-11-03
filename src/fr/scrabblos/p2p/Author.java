package fr.scrabblos.p2p;

import java.util.Random;
import java.util.Vector;

public class Author implements Runnable {

	private int publicKey;
	public Vector<Lettre> letterBag;
	private BlockChain blockChain;
	int nbLettre = 0;

	public Author(int publicKey, int nbLettre, BlockChain b) {
		this.nbLettre = nbLettre;
		this.letterBag = genLetter(nbLettre);
		this.blockChain = b;
		this.publicKey = publicKey;
	}

	@Override
	public void run() {
		int t = this.letterBag.size();
		while (t > 0) {
			blockChain.injectLetter(letterBag.remove(0));
			t--;
			try {
				Thread.sleep(1500);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public Vector<Lettre> genLetter(int nb) {
		Random r = new Random();
		int l;
		Vector<Lettre> res = new Vector<>();
		for (int i = 0; i < this.nbLettre; i++) {
			l = r.nextInt(122 - 97) + 97;
			res.add(new Lettre(((char) l), publicKey));
		}
		return res;

	}

}
