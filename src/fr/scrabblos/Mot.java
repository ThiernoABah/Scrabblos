package fr.scrabblos;

import java.io.Serializable;
import java.util.ArrayList;

public class Mot implements Serializable {

	public ArrayList<Lettre> mot ;
	public String head;
	public int politician;
	public String signature;
	
	public Mot(ArrayList<Lettre> mot, String head, int politician, String signature) {
		super();
		this.mot = mot;
		this.head = head;
		this.politician = politician;
		this.signature = signature;
	}
	
	public String toString() {
		return "mot : "+mot.toString()+" politician : "+politician+" head : " + head+" signature : "+signature;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Mot mot = (Mot) o;
		if(mot.mot.size()!=this.mot.size()) {
			return false;
		}
		if(!mot.head.equals(this.head)) {
			return false;
		}
		for(int i = 0;i<this.mot.size();i++) {
			if(!mot.mot.get(i).equals(mot.mot.get(i))) {
				return false;
			}
		}
		
		return politician == mot.politician && head.equals(mot.head)
				&& signature.equals(mot.signature);
	}
}
