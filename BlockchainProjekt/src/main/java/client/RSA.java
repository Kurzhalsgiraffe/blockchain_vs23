package client;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
		
	public static void main(String[] args) {
		KeyPair key = gen();
		
		String message = "Hallo Welt";
		byte[] enc = encrypt(message, key.getPublic());
		String dec = decrypt(enc, key.getPrivate());

		System.out.println(new String(enc));
		System.out.println(new String(dec));
	}
	
	public static KeyPair gen() {
		KeyPairGenerator keygen = null;
		try {
			keygen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygen.initialize(1024);
		KeyPair key = keygen.generateKeyPair();
		return key;
	}
	
	public static byte[] encrypt(String message, PublicKey pk) {
		byte[] encryptedMessage = null;
		Cipher cipher = null;

		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pk);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		try {
			encryptedMessage = cipher.doFinal(message.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return encryptedMessage;
	}
	
	public static String decrypt(byte[] encryptedMessage, PrivateKey sk) {
		byte[] dec = null;
		Cipher cipher = null;

		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, sk);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		try {
			dec = cipher.doFinal(encryptedMessage);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(dec);
	}
}
