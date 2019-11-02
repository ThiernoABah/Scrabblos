package fr.scrabblos;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Author extends Person implements Runnable {

	private ArrayList<Character> bag=new ArrayList<Character>();
	private boolean isGameRunning = true;
	private int period = 0;
	
	private int currentPoint=0;
	
	private boolean isBagSet=false;
	private boolean isFullLetterPool=false;
	private boolean isNextTurnSet=false;
	private InputServ inServ;
	
	public Author(String host, int port) {
		super(host, port);
		try {
			inServ = new InputServ(this, reader, socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void bagSet(JSONArray array) {
		List<Object> list = array.toList();
		for(Object o : list) {
			String c = (String) o;
			Character cc = c.charAt(0);
			bag.add(cc);
		}
		isBagSet=true;
	}

	@Override
	public void run() {
		new Thread(inServ).start();
		connect();
		getBag();
		getFullLetterPool();
		//getFullLetterPoolSince(5);
		//listen();
		
		while(isGameRunning) {
			injectLetter();
			waitForNextTurn();
		}
		
		//stopListen();
		try {
			inServ.stopRunning();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForNextTurn() {
		if(!isGameRunning) return;
		while(!isNextTurnSet) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		isNextTurnSet=false;
		/*
		 * JSONObject response = readMessage();
		try {
			int integer = response.getInt("next_turn");
			period = integer;
		}catch(Exception e) { // fin du jeu ou autre message si en mode ecoute
			isGameRunning=false;
		}
		*/
	}
	
	
	public void setNextTurn(int integer) {
		period = integer;
		isNextTurnSet=true;
	}

	public void setFullLetterPool(JSONObject full_letterpool) {
		period = full_letterpool.getInt("current_period");
		JSONArray array = full_letterpool.getJSONArray("letters");
		List<Object> list = array.toList();
		/*
		for(Object o : list) {
			JSONObject obj = (JSONObject) o;
			Character cc = obj.getString("letter").charAt(0);
			//bag.remove(cc);
			// raisonnement : Quel lettre a le plus de value a envoyer?
		}
		*/
		isFullLetterPool=true;
	}
	
	private void getFullLetterPool() {
		sendMessage("{ \"get_full_letterpool\": null}");
		
		while(!isFullLetterPool) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//JSONObject response = readMessage();
		/*JSONObject full_letterpool = response.getJSONObject("full_letterpool");
		period = full_letterpool.getInt("current_period");
		JSONArray array = full_letterpool.getJSONArray("letters");
		List<Object> list = array.toList();
		for(Object o : list) {
			JSONObject obj = (JSONObject) o;
			Character cc = obj.getString("letter").charAt(0);
			bag.remove(cc);
		}
		*/
	}

	private void injectLetter() {
		if(bag.size()==0) {
			isGameRunning = false;
			return;
		}
		// get a letter from bag
		Random r = new Random();
		int rand = r.nextInt(bag.size());
		char letter = bag.get(rand).charValue();
		bag.remove(rand);
		
		
		try {
			/** binaries **/
			byte[] letterBinary = (letter+"").getBytes(Charset.forName("UTF-8"));
			long periods = (long) period;
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
			buffer.putLong(periods);
			byte[] periodBinary  = buffer.array();
			
			// hash du block précedent
			byte[] prevBlockHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855".getBytes(Charset.forName("UTF-8"));
			
			byte[] pkBytes = pk.getBytes(Charset.forName("UTF-8"));
			
			ByteBuffer bb = ByteBuffer.allocate(letterBinary.length + periodBinary.length + prevBlockHash.length + pkBytes.length);
			bb.put(letterBinary);
			bb.put(periodBinary);
			bb.put(prevBlockHash);
			bb.put(pkBytes);
			
			byte toDigest[] = bb.array();
			/**/
			// Str s = letter + period + "" + pk).getBytes(Charset();
			// sign sha and sign
			//SHA 256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] sha256 = digest.digest(toDigest);
			
			// generate signature for injecting
			byte[] encodedMsg = ed25519.signature(sha256, privateKey, publicKey);
			StringBuilder sb = new StringBuilder();
	        for (byte b : encodedMsg) {
	            sb.append(String.format("%02x", b));
	        }
			String signature= sb.toString();
			
			// send the msg
			sendMessage("{ \"inject_letter\": "
					+ "{ \"letter\":\""+letter+"\", "
					+ " \"period\":"+period+", " + 
					"\"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\", " + 
					"\"author\":\""+pk+"\", " + 
					"\"signature\":\""+signature+"\"" + 
					" } }");
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

	
	private void getBag() {
		while(!isBagSet) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//JSONObject msg = readMessage();
		//JSONArray array = msg.getJSONArray("letters_bag");
		/*
		List<Object> list = array.toList();
		for(Object o : list) {
			String c = (String) o;
			Character cc = c.charAt(0);
			bag.add(cc);
		}*/
	}
	
	public void receiveInjectWord(JSONObject inject_word) {
		
	}
	
	public static void main(String[] args) {
		Author a = new Author("127.0.0.1", 12345);
		//Author b = new Author("127.0.0.1", 12345);
		//Author c= new Author("127.0.0.1", 12345);
		//Politicien p = new Politicien();
		new Thread(a).start();
		//new Thread(b).start();
		//new Thread(c).start();
	}
}
