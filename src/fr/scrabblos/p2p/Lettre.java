package fr.scrabblos.p2p;


public class Lettre {
	public static int idGen = 0;
	public int id;
	public char lettre ;
	public int author;
	public String blockHash = ""; // le hash du block sur lequel cette lettre est (par defaut chaine vide, la lettre n'est sur aucune lettre)

	public Lettre(char c, int pk) {
		this.lettre = c;
		this.author = pk;
		id = idGen;
		idGen ++;
	}
	
	public String toString() {
		return String.valueOf(this.lettre+":"+id+":"+author+":"+blockHash);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Lettre l = (Lettre) o;
		
		return lettre == l.lettre && id == l.id;
	}
	
}
