package fr.scrabblos;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Random;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Author implements Runnable {

	private KeyPair pair;
	private ArrayList<Character> bag=null;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Author(String host, int port) {
		try {
			socket = new Socket(host, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (IOException e) {
			e.printStackTrace();
		}
		pair = Utils.getNewKey("author");
	}

	@Override
	public void run() {
		connect();
		getBag();
		
		/*
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
		*/
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getBag() {
		try {
			String line = reader.readLine();
			System.out.println("msg from serv : "+line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* ici le client envoi sa clé public au serveur */
	public void connect() {
		sendMessage("{ \"register\" : "+Hex.toHexString(pair.getPublic().getFormat().getBytes())+" }");
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
	
	public void sendMessage(String msgJson) {
		long length = msgJson.length();
		String msgToSend = Long.toBinaryString(length) + msgJson;
		// send to serv
		writer.println(msgToSend);
	}
	
	
	
	public static void main(String[] args) {
		Author a = new Author("127.0.0.1", 12346);
		new Thread(a).start();
	}
}
