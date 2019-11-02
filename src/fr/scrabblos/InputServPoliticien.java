package fr.scrabblos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONObject;

public class InputServPoliticien implements Runnable {
	private boolean isRunning = true;
	private Politicien myPoliticien;
	private BufferedReader reader;
	private InputStream in;

	public InputServPoliticien(Politicien a, BufferedReader reader, InputStream in) {
		myPoliticien = a;
		this.reader = reader;
		this.in = in;
	}

	public void stopRunning() {
		isRunning = false;
	}

	@Override
	public void run() {
		JSONObject msg = null;
		DataInputStream dIn = new DataInputStream(in);
		while (isRunning) {
			try {
				byte[] length = new byte[8];
				dIn.read(length);
				ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
				buffer.put(length);
				buffer.flip();// need flip
				long lgth = buffer.getLong();
				int lgt = (int) lgth;
				char[] buff = new char[lgt];
				reader.read(buff);
				String msgJson = new String(buff);
				msg = new JSONObject(msgJson);
				System.out.println("RECEIVE : " + msgJson);

				try {
					JSONObject full_letterpool = msg.getJSONObject("full_letterpool");
					myPoliticien.setFullLetterPool(full_letterpool);
				} catch (Exception ee) {
					try {
						int integer = msg.getInt("next_turn");
						myPoliticien.setNextTurn(integer);
					} catch (Exception eee) {
						try {
							JSONObject inject_word = msg.getJSONObject("inject_word");
							myPoliticien.receiveInjectWord(inject_word);
						} catch (Exception eeee) {
							try {
								JSONObject inject_letter = msg.getJSONObject("inject_letter");
								myPoliticien.receiveInjectLetter(inject_letter);
							}catch(Exception eeeee) {
								
							}
						}
					}
				}

			} catch (Exception e) {
				if (isRunning) {
					e.printStackTrace();
				}
			}
		}

	}

}
