package fr.scrabblos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class Person {
	protected PrintWriter writer;
	protected BufferedReader reader;
	protected Socket socket;
	protected String pk;
	
	protected byte[] publicKey;
	protected byte[] privateKey;
	
	public Person(String host, int port) {
		pk = getNewPublicKeyToHexa();
		try {
			socket = new Socket(host, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void connect() {
		sendMessage("{ \"register\" : "+"\""+pk+"\""+" }");
	} 

	protected void stopListen() {
		sendMessage("{ \"stop_listen\" : null }");
	}

	protected void listen() {
		sendMessage("{ \"listen\" : null }");
	}
	/*
	protected void getFullLetterPoolSince(int periodSince) {
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
	*/
	protected void sendMessage(String msgJson) {
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
	
	protected String getNewPublicKeyToHexa() {
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
