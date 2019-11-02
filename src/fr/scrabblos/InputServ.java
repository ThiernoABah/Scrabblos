package fr.scrabblos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class InputServ implements Runnable {

	private boolean isRunning=true;
	private Author myAuthor;
	private BufferedReader reader;
	private InputStream in;
	
	public InputServ(Author a, BufferedReader reader, InputStream in) {
		myAuthor=a;
		this.reader=reader;
		this.in=in;
	}
	
	public void stopRunning() {
		isRunning=false;
	}
	
	@Override
	public void run() {
		JSONObject msg=null;
		DataInputStream dIn = new DataInputStream(in);
		while(isRunning) {
			try {
				byte[] length = new byte[8];
				dIn.read(length);
				ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
			    buffer.put(length);
			    buffer.flip();//need flip 
			    long lgth = buffer.getLong();
			    	int lgt = (int) lgth; 
				char[] buff = new char[lgt];
				reader.read(buff);
				String msgJson = new String(buff);
				msg = new JSONObject(msgJson);
				System.out.println("RECEIVE : "+msgJson);
				
				try {
					JSONArray array = msg.getJSONArray("letters_bag");
					myAuthor.bagSet(array);
				}catch(Exception e) {
					try {
						JSONObject full_letterpool = msg.getJSONObject("full_letterpool");
						myAuthor.setFullLetterPool(full_letterpool);
					}catch(Exception ee) {
						try {
							int integer = msg.getInt("next_turn");
							myAuthor.setNextTurn(integer);
						}catch(Exception eee) {
							try {
								JSONObject inject_word = msg.getJSONObject("inject_word");
								myAuthor.receiveInjectWord(inject_word);
							}catch(Exception eeee) {
								
							}
						}
					}
				}
				
			} catch (IOException e) {
				if(isRunning) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
