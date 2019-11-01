package fr.scrabblos.p2p;

import java.util.Vector;

public class Mots {

	public Vector<Lettre> mot ;
	public int publicKey;
	public String blockHash = ""; // le hash du block sur lequel ce mot est (par defaut chaine vide)

	public Mots(Vector<Lettre> m, int pk,String bh) {
		this.mot = m;
		this.publicKey = pk;
		this.blockHash = bh;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (Lettre l : mot) {
			res.append(l.lettre);
		}
		return res.toString();
	}
}
