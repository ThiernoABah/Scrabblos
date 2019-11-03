package fr.scrabblos.p2p;


import java.io.Serializable;

public class Trie implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public TrieNode root;

    Trie() {
        root = new TrieNode();
    }

    void insert(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setEndOfWord(true);
    }

    boolean delete(String word) {
        return delete(root, word, 0);
    }

    boolean containsNode(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    boolean isEmpty() {
        return root == null;
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }
    public String getWordsSizeI(TrieNode t, int i){
    	String res = "";
    	if(i == 1) {
    		for(java.util.Map.Entry<Character, TrieNode> entry : t.getChildren().entrySet()) {
    			Character cle = entry.getKey();
    			TrieNode valeur = entry.getValue();
    		    if(valeur.isEndOfWord()) {
    		    	return cle.toString()+";";
    		    }
    		}
    	}
    	i--;
    	for(java.util.Map.Entry<Character, TrieNode> entry : t.getChildren().entrySet()) {
			Character cle = entry.getKey();
			TrieNode valeur = entry.getValue();
			if(valeur.isEndOfWord()) {
				continue;
			}
			String tmp = getWordsSizeI(valeur,i);
			if(!tmp.equals("")) {
				res+=cle.toString()+tmp;
			}
		}
    	return res;
    }
    
    
    public static void main(String[] args) {
    	Trie trie = new Trie();

        trie.insert("Programming");
        trie.insert("is");
        trie.insert("a");
        trie.insert("way");
        trie.insert("of");
        trie.insert("life");
        
        System.out.println(trie.containsNode("Programming"));
        System.out.println(trie.containsNode("P"));
        System.out.println(trie.containsNode("wa"));
        System.out.println(trie.getWordsSizeI(trie.root, 2));
        
    }
}