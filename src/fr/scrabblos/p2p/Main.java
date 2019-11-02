package fr.scrabblos.p2p;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) {
		BlockChain bc = new BlockChain();
		
		Vector<Politicien> poli = new Vector<>();
		Politicien test = new Politicien(100,bc, new Trie());
		poli.add(test);
		Politicien test1 = new Politicien(101,bc, new Trie());
		poli.add(test1);
		Politicien test2 = new Politicien(102,bc, new Trie());
		poli.add(test2);
		test.politiciens = poli;
		test1.politiciens = poli;
		test2.politiciens = poli;
		ExecutorService pool = Executors.newFixedThreadPool(13);
		Author a = null;
		for(int i=0;i<10;i++) {
			 pool.execute(new Author(i,15, bc));
		}
		pool.execute(test);
		pool.execute(test1);
		pool.execute(test2);
		

	}

}
