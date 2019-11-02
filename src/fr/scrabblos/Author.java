package fr.scrabblos;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.*;

public class Author implements Runnable {

	private String pk;
	private ArrayList<Character> bag=new ArrayList<Character>();
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private int period = 0;
	
	public Author(String host, int port) {
		pk = Utils.getNewPublicKeyToHexa();
		try {
			socket = new Socket(host, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		connect();
		getBag();
		getFullLetterPool();
		listen();
		
		boolean notOver = true;
		while(notOver) {
			injectLetter();
			waitForNextTurn();
		}
		
		stopListen();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForNextTurn() {
		try {
			Thread.sleep(100000); // effacer apres implem de la fonction
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//read = next_turn ou fin du jeu;
		//fin_du_jeu => notOver=false;
	}

	private void getFullLetterPool() {
		sendMessage("{ \"get_full_letterpool\": null}");
		JSONObject response = readMessage();
		JSONObject full_letterpool = response.getJSONObject("full_letterpool");
		period = full_letterpool.getInt("current_period");
		JSONArray array = full_letterpool.getJSONArray("letters");
		List<Object> list = array.toList();
		for(Object o : list) {
			JSONObject obj = (JSONObject) o;
			Character cc = obj.getString("letter").charAt(0);
			bag.remove(cc);
		}
	}

	private void injectLetter() {
		Random r = new Random();
		int rand = r.nextInt(bag.size());
		char letter = bag.get(rand).charValue();
		bag.remove(rand);
		
		sendMessage("{ \"inject_letter\": "
				+ "{ \"letter\":\""+letter+"\","
				+ " \"period\":"+period+"," + 
				"\"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"," + 
				"\"author\":\""+pk+"\"," + 
				"\"signature\":\"8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ceb6108043d4a080223b467bb810c52b5975960eea96a2203a877f32bbd6c4dac16ec07\"" + 
				" } }");
	}

	private void stopListen() {
		sendMessage("{ \"stop_listen\" : null }");
	}

	private void listen() {
		sendMessage("{ \"listen\" : null }");
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
		sendMessage("{ \"register\" : "+"\""+pk+"\""+" }");
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
		writer.printf("%s", msgJson);
		System.out.println("SEND : "+ msgJson);
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
			System.out.println("RECEIVE : "+msgJson);
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
