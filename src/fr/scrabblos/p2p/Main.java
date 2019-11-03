package fr.scrabblos.p2p;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Trie t = new Trie();
//		BufferedReader br = new BufferedReader(new FileReader("C:/Users/Thierno/Documents/Git/Scrabblos/src/fr/scrabblos/p2p/test/dict_100000_1_10.txt"));
//		try {
//		    String line = "";
//		    while (line != null) {
//		        line = br.readLine();
//		        if(line == null) {
//		        	break;
//		        }
//		        t.insert(line);
//		    }
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//		    br.close();
//		}
//		System.out.println(t.getWordsSizeI(t.root, 2));
		BlockChain bc = new BlockChain();
		Timer timer = new Timer();
		
		Politicien test = new Politicien(100,bc, t);
		Politicien test1 = new Politicien(101,bc, t);
		Politicien test2 = new Politicien(102,bc, t);

		ExecutorService pool = Executors.newFixedThreadPool(13);
		for(int i=0;i<10;i++) {
			 pool.execute(new Author(i,10, bc));
		}
		pool.execute(test);
		pool.execute(test1);
		pool.execute(test2);
		
		
		timer.schedule(new Printeur(bc), 0, 2000);

	}

}
