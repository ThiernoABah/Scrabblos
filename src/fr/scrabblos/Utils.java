package fr.scrabblos;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class Utils {
	public static String getNewPublicKeyToHexa() {
		String hexa ="";
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair pair = keyGen.generateKeyPair();
			
			byte[] priv = pair.getPrivate().getEncoded();
			byte[] publ = ed25519.publickey(priv);
			//byte[] encodedMsg = ed25519.signature("".getBytes(Charset.forName("UTF-8")), priv, publ);
			
			StringBuilder sb = new StringBuilder();
	        for (byte b : publ) {
	            sb.append(String.format("%02x", b));
	        }
	        hexa = sb.toString();
	        
	        //System.out.println("check valid : "+ed25519.checkvalid(encodedMsg, "".getBytes(Charset.forName("UTF-8")), publ));
	       
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hexa;
	}
	
}