package fr.scrabblos;

import java.io.Serializable;

public class Lettre implements Serializable{
	private static final long serialVersionUID = 1L;
	public char lettre ;
	public int publicKey;
	public String blockHash = -1; // le hash du block sur lequel cette lettre est (par defaut -1, la lettre n'est sur aucune lettre)

	public Lettre(char c, int pk) {
		this.lettre = c;
		this.publicKey = pk;
	}
	
	

}
