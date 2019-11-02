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
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Politicien implements Runnable{
	
	private ArrayList<Block> currentChain;
	private ArrayList<Character> currentCharList = new ArrayList<Character>();
	
	private String pk;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private boolean isGameRunning = true;
	private int period = 0;
	
	private byte[] publicKey;
	private byte[] privateKey;
	
	private int currentPoint=0;
	
	
	public Politicien(String host, int port) {
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
		getFullLetterPool();
		listen();
		
		while(isGameRunning) {
			tryToFindAWord();
		}
		
		stopListen();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tryToFindAWord() {
		
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
			//bag.remove(cc);
			
			// remplir currentCharList
		}
	}
	
	public void connect() {
		sendMessage("{ \"register\" : "+"\""+pk+"\""+" }");
	} 

	private void stopListen() {
		sendMessage("{ \"stop_listen\" : null }");
	}

	private void listen() {
		sendMessage("{ \"listen\" : null }");
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
			
			StringBuilder sb = new StringBuilder();
	        for (byte b : publicKey) {
	            sb.append(String.format("%02x", b));
	        }
	        hexa = sb.toString();
	        
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hexa;
	}
	

}
