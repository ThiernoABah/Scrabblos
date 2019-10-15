import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Author implements Runnable {

	private int privateKey;
	private int publicKey;
	private ArrayList<Character> bag=null;
	private Socket socket;
	
	public Author(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		//this.socket.connect();  Connection au serveur
		
		try {
			this.socket.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean fin = false;
		
		while(!fin) {
			Random r = new Random();
			int rand = r.nextInt(bag.size());
			sendLetter(bag.get(rand));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void connect() {
		// ici le client envoie sa clé public au serveur et recoit un sac de lettre du serveur
	}
	
	public void receiveLetter(ArrayList<Character> sac) {
		this.bag = sac;
	}
	
	public void sendLetter(Character c) {
		// ici le client envoie une lettre de son sac au serveur
		/*** exemple hash SHA3-512 ***/
		byte[] hash = { 0, 1, 0, 0, 1};
		SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
		byte[] digest = digestSHA3.digest(hash);
	    System.out.println("SHA3-512 = " + Hex.toHexString(digest));
	    /***/
	}
	
	public void askLetters() {
		
	}
	
	
	
	
	
}
