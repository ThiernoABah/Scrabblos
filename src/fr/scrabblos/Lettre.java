package fr.scrabblos;

import java.io.Serializable;

public class Lettre implements Serializable{
	private static final long serialVersionUID = 1L;
	public char lettre ;
	public int period ;
	public String head;
	public String author;
	public String signature;
	
	
	public Lettre(char c, String pk,int period, String head, String signature ) {
		this.lettre = c;
		this.author = pk;
		this.period = period;
		this.head = head;
		this.signature = signature;
	}
	
	public String toString() {
		return "lettre : "+lettre+" period : "+period+" author : "+author+" head : " + head+" signature : "+signature;
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
		
		return lettre == l.lettre && head.equals(l.head) && author==l.author
				&& period == l.period && signature.equals(l.signature);
	}

}
