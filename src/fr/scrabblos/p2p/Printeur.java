package fr.scrabblos.p2p;

import java.util.TimerTask;

public class Printeur extends TimerTask {
	BlockChain bc;
	
	public Printeur(BlockChain b) {
		this.bc = b;
	}
    public void run() {
       System.out.println(bc); 
    }
}