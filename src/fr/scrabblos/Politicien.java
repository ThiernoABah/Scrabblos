package fr.scrabblos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Politicien extends Person implements Runnable{
	
	private ArrayList<Block> currentChain;
	private ArrayList<Lettre> currentCharList = new ArrayList<Lettre>();
	
	private boolean isGameRunning = true;
	private int period = 0;
	private int currentPoint=0;
	private boolean isFullLetterPool=false;
	
	private InputServPoliticien in;
	
	public Politicien(String host, int port) {
		super(host,port);
		try {
			in = new InputServPoliticien(this, reader, socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		new Thread(in).start();
		connect();
		getFullLetterPool();
		listen();
		
		while(isGameRunning) {
			tryToFindAWord();
		}
		
		stopListen();
		try {
			in.stopRunning();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tryToFindAWord() {
		ArrayList<Lettre> newWord = new ArrayList<Lettre>();

		//...
		
		JSONArray array = new JSONArray(newWord);
		sendMessage("{ \"inject_word\" : { \"word\": "+array.toString()+","
				+ "\"head\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"," + 
				"     \"politician\":\""+pk+"\"," + 
				"     \"signature\":\"c7a41b5bfcec80d3780bfc5d3ff8c934f7c7f41b27956a8acb20aee066b406edc5d1cb26c42a1e491da85a97650b0d5854680582dcad3b2c99e2e04879769307\"" + 
				" }");
	}

	public void setFullLetterPool(JSONObject full_letterpool) {
		period = full_letterpool.getInt("current_period");
		JSONArray array = full_letterpool.getJSONArray("letters");
		List<Object> list = array.toList();
		for(Object o : list) {
			JSONObject obj = (JSONObject) o;
			Character cc = obj.getString("letter").charAt(0);
			currentCharList.add(new Lettre(cc, obj.getString("author"), obj.getInt("period"),obj.getString("head"), obj.getString("signature")));
		}
		
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
	}

	public void setNextTurn(int integer) {
		// TODO Auto-generated method stub
		
	}

	public void receiveInjectWord(JSONObject inject_word) {
		// TODO Auto-generated method stub
		
	}

	public void receiveInjectLetter(JSONObject inject_letter) {
		// TODO Auto-generated method stub
		
	}

}
