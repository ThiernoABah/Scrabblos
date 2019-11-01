package fr.scrabblos;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.*;

public class Author implements Runnable {

	private KeyPair pair;
	private ArrayList<Character> bag=new ArrayList<Character>();
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
			
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getBag() {
		JSONObject msg = readMessage();
		JSONArray array = msg.getJSONArray("letters_bag");
		List<Object> list = array.toList();
		for(Object o : list) {
			String c = (String) o;
			Character cc = c.charAt(0);
			bag.add(cc);
		}
	}

	/* ici le client envoi sa clé public au serveur */
	public void connect() {
		sendMessage("{ \"register\" : "+"\"b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f\""+" }");
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
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(length);
		byte[] array  = buffer.array();
		try {
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.write(array);
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.println(msgJson);
		System.out.println(msgJson);
	}
	
	public JSONObject readMessage() {
		JSONObject msg=null;
		try {
			byte[] length = new byte[8];
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			dIn.read(length);
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		    buffer.put(length);
		    buffer.flip();//need flip 
		    long lgth = buffer.getLong();
		    	int lgt = (int) lgth; // bof
			char[] buff = new char[lgt];
			reader.read(buff);
			String msgJson = new String(buff);
			msg = new JSONObject(msgJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public static void main(String[] args) {
		Author a = new Author("127.0.0.1", 12345);
		new Thread(a).start();
	}
}
