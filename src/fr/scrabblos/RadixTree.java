package fr.scrabblos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// on stock tout les mots possibles ici de cette facon on pourra use cette classe pour que les politiciens verifie si le mots qu'ils ont construit est valide ou pas
public class RadixTree {

	ArrayList<RadixTree> fils = new ArrayList<>();
	String value;

	public RadixTree(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<RadixTree> getFils() {
		return fils;
	}

	public void setFils(ArrayList<RadixTree> fils) {
		this.fils = fils;
	}

	
	public void build(String chemin) {
		System.out.println("start");
		String line = null;

		FileReader fileReader = null;
		try {
			fileReader = new FileReader(chemin);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try {
				while ((line = bufferedReader.readLine()) != null) {
					add(line);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			fileReader.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public void add(String line) {
		if (line.length() == 0) {
			return;
		}
		String reste = line;
		RadixTree rt = null;
		
		ArrayList<RadixTree> current = fils;
		if (current.size() <= 0) {
			current.add(new RadixTree(line));
		} else {
			String prefixe;
			int i = 0;
			while (current.size() > 0 && current.size() > i) {
				rt = current.get(i);
				if (rt.value.equals(reste)) {
					return;
				} else {
					if (isPrefix(reste, rt.getValue())) {
						prefixe = getPrefix(reste, rt.getValue());
						reste = reste.substring(prefixe.length(), reste.length());
						if (reste.length() == 0) {
							String tmp = rt.value.substring(prefixe.length(), rt.value.length());
							rt.value = prefixe;
							String tupleFils = tmp;
							RadixTree rtFils = new RadixTree(tupleFils);
							rtFils.setFils(rt.fils);
							ArrayList<RadixTree> nouveauFils = new ArrayList<>();
							nouveauFils.add(rtFils);
							rt.setFils(nouveauFils);
							return;
						} else {
							current = rt.getFils();
							i = 0;
						}
					} else {
						i++;
					}
				}
			}
			String tupleFils = reste;
			RadixTree rtFils = new RadixTree(tupleFils);
			current.add(rtFils);
		}
	}

	public boolean search(String r) {
		if (r.length() == 0) {
			return true;
		}
		String reste = r;
		RadixTree rt;
		ArrayList<RadixTree> current = fils;
		if (current.size() <= 0) {
			return false;
		}
		String prefixe;
		int i = 0;
		while (current.size() > 0 && current.size() > i) {
			rt = current.get(i);
			if (rt.value.equals(reste)) {
				return true;
			} else {
				if (isPrefix(reste, rt.getValue())) {
					prefixe = getPrefix(reste, rt.getValue());
					reste = reste.substring(prefixe.length(), reste.length());
					if (reste.length() == 0) {
						return false;
					} else {
						current = rt.getFils();
						i = 0;
					}
				} else {
					i++;
				}
			}
		}
		return false;
	}

	public boolean isPrefix(String s1, String s2) {
		return s1.charAt(0) == s2.charAt(0);
	}

	public String getPrefix(String s1, String s2) {
		StringBuilder res = new StringBuilder();
		int i = 0;
		if (s1.length() > s2.length()) {
			while (i < s2.length() && s1.charAt(i) == s2.charAt(i)) {
				res.append(s1.charAt(i));
				i++;
			}
			return res.toString();
		} else {
			if (s1.length() < s2.length()) {
				while (i < s1.length() && s1.charAt(i) == s2.charAt(i)) {
					res.append(s1.charAt(i));
					i++;
				}
				return res.toString();
			} else {
				while (i < s2.length() && s1.charAt(i) == s2.charAt(i)) {
					res.append(s1.charAt(i));
					i++;
				}
				return res.toString();
			}
		}
	}

	public static void main(String args[]) {
		String fileName ="C:/Users/Thierno/Documents/Git/Scrabblos/src/fr/scrabblos/test.txt";
		try (Scanner scanner = new Scanner(System.in)) {
			RadixTree racine = new RadixTree("");
			racine.build(fileName);
			String[] tab = fileName.split("/");
			while (true) {
				System.out.print("Enter a word to find in " + tab[tab.length - 1] + " >>> ");
				String w = scanner.nextLine();
				boolean res = racine.search(w);
				System.out.println(res);

			}
		}
	}
}