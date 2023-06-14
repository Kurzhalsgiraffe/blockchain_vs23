package client;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

	public static KeyPair gen(int keysize) {
		KeyPairGenerator keygen = null;
		try {
			keygen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygen.initialize(keysize);
		KeyPair key = keygen.generateKeyPair();
		return key;
	}

	public static byte[] encrypt(byte[] message, PublicKey pk, int keysize) {
		byte[] encryptedMessage = null;
		Cipher cipher = null;

		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, pk);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		try {
			byte[] paddedMessage = Arrays.copyOf(message, keysize/8);
			encryptedMessage = cipher.doFinal(paddedMessage);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return encryptedMessage;
	}

	public static byte[] decrypt(byte[] encryptedMessage, PrivateKey sk) {
		byte[] dec = null;
		Cipher decryptCipher = null;

		try {
			decryptCipher = Cipher.getInstance("RSA/ECB/NoPadding");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(sk.getEncoded());

			decryptCipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(privateKeySpec));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		try {
			dec = decryptCipher.doFinal(encryptedMessage);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return dec;
    }
}
