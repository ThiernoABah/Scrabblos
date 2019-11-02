package fr.scrabblos;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

public class Author implements Runnable {

	private String pk;
	private ArrayList<Character> bag=new ArrayList<Character>();
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private boolean isGameRunning = true;
	private int period = 0;
	
	private byte[] publicKey;
	private byte[] privateKey;
	
	private int currentPoint=0;
	
	public Author(String host, int port) {
		pk = getNewPublicKeyToHexa();
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
		//getFullLetterPoolSince(5);
		//listen();
		
		while(isGameRunning) {
			injectLetter();
			waitForNextTurn();
		}
		
		//stopListen();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getFullLetterPoolSince(int periodSince) {
		sendMessage("{ \"get_letterpool_since\": "+periodSince+"}");
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

	private void waitForNextTurn() {
		if(!isGameRunning) return;
		JSONObject response = readMessage();
		try {
			int integer = response.getInt("next_turn");
			period = integer;
		}catch(Exception e) { // fin du jeu ou autre message si en mode ecoute
			isGameRunning=false;
		}
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
	
	public String getNewPublicKeyToHexa() {
		String hexa ="";
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair pair = keyGen.generateKeyPair();
			
			privateKey = pair.getPrivate().getEncoded();
			publicKey = ed25519.publickey(privateKey);
			//byte[] encodedMsg = ed25519.signature("".getBytes(Charset.forName("UTF-8")), priv, publ);
			
			StringBuilder sb = new StringBuilder();
	        for (byte b : publicKey) {
	            sb.append(String.format("%02x", b));
	        }
	        hexa = sb.toString();
	        
	        //System.out.println("check valid : "+ed25519.checkvalid(encodedMsg, "".getBytes(Charset.forName("UTF-8")), publ));
	       
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hexa;
	}
	
	public static void main(String[] args) {
		Author a = new Author("127.0.0.1", 12345);
		Author b = new Author("127.0.0.1", 12345);
		Author c= new Author("127.0.0.1", 12345);
		//Politicien p = new Politicien();
		new Thread(a).start();
		new Thread(b).start();
		new Thread(c).start();
	}
}
