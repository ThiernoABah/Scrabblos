package fr.scrabblos.p2p;

public class Lettre {
	public static int idGen = 0;
	public int id;
	public char lettre ;
	public int publicKey;
	public String blockHash = ""; // le hash du block sur lequel cette lettre est (par defaut chaine vide, la lettre n'est sur aucune lettre)

	public Lettre(char c, int pk) {
		this.lettre = c;
		this.publicKey = pk;
		id = idGen;
		idGen ++;
	}
	
	public String toString() {
		return String.valueOf(this.lettre+":"+String.valueOf(publicKey));
	}

}
