import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

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
	}
	
	public void askLetters() {
		
	}
	
	
	
	
	
}
