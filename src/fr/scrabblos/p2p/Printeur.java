package fr.scrabblos.p2p;

import java.util.TimerTask;

public class Printeur extends TimerTask {
	BlockChain bc;
	
	public Printeur(BlockChain b) {
		this.bc = b;
	}
    public void run() {
    	if(bc.lettrePool.size() <= bc.difficulty) {
    		return;
    	}
       System.out.println(bc); 
    }
}