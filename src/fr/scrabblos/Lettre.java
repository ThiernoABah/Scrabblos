package fr.scrabblos;

import java.io.Serializable;

public class Lettre implements Serializable{
	private static final long serialVersionUID = 1L;
	public char lettre ;
	public int publicKey;
	
	public Lettre(char c, int pk) {
		this.lettre = c;
		this.publicKey = pk;
	}
	
	

}
