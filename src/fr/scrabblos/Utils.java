package fr.scrabblos;

import java.security.KeyPair;
import java.util.Base64;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

public class Utils {
	public static KeyPair getNewKey(String wordToCrypt) {
		KeyPair pair = null;
		
			 //byte[] msg = "eyJhbGciOiJFZERTQSJ9.RXhhbXBsZSBvZiBFZDI1NTE5IHNpZ25pbmc".getBytes(StandardCharsets.UTF_8);
			 //String expectedSig = "hgyY0il_MGCjP0JzlnLWG1PPOt7-09PGcvMg3AIbQR6dWbhijcNR4ki4iylGjg5BhVsPt9g7sVvpAr_MuM0KAg";
	
			 byte[] privateKeyBytes = Base64.getUrlDecoder().decode("nWGxne_9WmC6hEr0kuwsxERJxWl7MmkZcDusAxyuf2A");
			 byte[] publicKeyBytes = Base64.getUrlDecoder().decode("11qYAYKxCrfVS_7TyWQHOg7hcvPapiMlrwIaaPcHURo");
	
			 Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);
			 Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(publicKeyBytes, 0);

	        // Generate new signature
	        Signer signer = new Ed25519Signer();
	        signer.init(true, privateKey);
	        //signer.update(msg, 0, msg.length);
	        byte[] signature = null;
			try {
				signature = signer.generateSignature();
			} catch (DataLengthException | CryptoException e) {
				e.printStackTrace();
			}
	        String actualSignature = Base64.getUrlEncoder().encodeToString(signature).replace("=", "");
	        /*
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ed25519", "DSA"); // essayer avec edDSA
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			pair = keyGen.generateKeyPair();
			*/
		return pair;
	}
}
