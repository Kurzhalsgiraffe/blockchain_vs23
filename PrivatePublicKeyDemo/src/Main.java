import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Main {

	public static void usage() {
		System.out.println("Usage: java Main WriteKeysAndMessage");           // Option mit anschliessendem "Read"
		System.out.println("Usage: java Main WriteKeys");                     // oder alternativ: 1. Schritt
		System.out.println("Usage: java Main ReadPublicKeyAndWriteMessage");  // gefolgt vom 2. Schritt mit anschliessendem "Read"
		System.out.println("Usage: java Main Read");                          // 3. Schritt
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1)
				usage();
			else {
				if (args[0].equals("WriteKeysAndMessage")) {
					// -----------------------------
					// Erzeugen des Schluesselpaares
					// -----------------------------
					KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
					generator.initialize(2048);
					KeyPair pair = generator.generateKeyPair();

					PrivateKey privateKey = pair.getPrivate();
					PublicKey publicKey = pair.getPublic();

					// ------------------------------
					// Speichern des Schluesselpaares
					// ------------------------------
					FileOutputStream fosPublicKey = new FileOutputStream("public.key");
					FileOutputStream fosPrivateKey = new FileOutputStream("private.key");
					fosPublicKey.write(publicKey.getEncoded());
					fosPrivateKey.write(privateKey.getEncoded());

					// ------------------------------------------------
					// Vorbereitung zur Verschluesselung mit Public Key
					// ------------------------------------------------
					Cipher encryptCipher = Cipher.getInstance("RSA");
					// Direkte Verschluesselung mit Public Key (zum Vergleich siehe "ReadPublicKeyAndWriteMessage")
					encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey); // <----------

					// ---------------------------
					// Konvertierung der Nachricht
					// ---------------------------
					// Bemerkung: RSA-Alg. akzeptiert ausschliesslich Byte-Array
					// Strings muessen deshalb konvertiert werden!
					String secretMessage = "Irgend eine Nachricht secret message";
					
					System.out.println("secretMessage " + secretMessage);
					System.out.println("secretMessage.length() " + secretMessage.length());
					
					byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
					byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

					// Speicherung als byte-Array
					String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
					System.out.println("encodedMessage = " + encodedMessage);

					FileOutputStream fosMessage = new FileOutputStream("message.key");
					fosMessage.write(encryptedMessageBytes);
				} else if (args[0].equals("WriteKeys")) {
					// -----------------------------
					// Erzeugen des Schluesselpaares
					// -----------------------------
					KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
					generator.initialize(2048);
					KeyPair pair = generator.generateKeyPair();

					PrivateKey privateKey = pair.getPrivate();
					PublicKey publicKey = pair.getPublic();

					// ------------------------------
					// Speichern des Schluesselpaares
					// ------------------------------
					FileOutputStream fosPublicKey = new FileOutputStream("public.key");
					FileOutputStream fosPrivateKey = new FileOutputStream("private.key");
					fosPublicKey.write(publicKey.getEncoded());
					fosPrivateKey.write(privateKey.getEncoded());
				} else if (args[0].equals("ReadPublicKeyAndWriteMessage")) {
					// ------------------------------
					// Auslesen des Public Keys
					// ------------------------------
					File publicKeyFile = new File("public.key");
					byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

					// -------------------------------------------
					// Erzeugung des Public Keys ueber Key-Factory
					// -------------------------------------------
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					// "Indirekte" Verschluesselung mit eingelesenem Wert des "Public Key"
					EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

					// ---------------------------
					// Konvertierung der Nachricht
					// ---------------------------
					String secretMessage = "Irgend eine Nachricht secret message";
					System.out.println("secretMessage " + secretMessage);

					// ------------------------------------------------
					// Vorbereitung zur Verschluesselung mit Public-Key
					// ------------------------------------------------
					Cipher encryptCipher = Cipher.getInstance("RSA");
					// "indirekte" Verschluesselung mit eingelesener PublicKeySpec (zum Vergleich siehe "WriteKeysAndMessage")
					encryptCipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(publicKeySpec)); // <----------

					// Bemerkung: RSA-Alg. akzeptiert ausschliesslich Byte-Array
					// Strings muessen deshalb konvertiert werden!
					byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
					byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

					String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
					System.out.println("encodedMessage = " + encodedMessage);

					FileOutputStream fosMessage = new FileOutputStream("message.key");
					fosMessage.write(encryptedMessageBytes); // Speichern des chiffrierten Textes
				} else if (args[0].equals("Read")) {
					// Auslesen des Private Key aus Datei
					File privateKeyFile = new File("private.key");
					byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

					// -------------------------------------------------
					// Vorbereitung zur Entschluesselung mit Private-Key
					// -------------------------------------------------
					Cipher decryptCipher = Cipher.getInstance("RSA");
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

					decryptCipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(privateKeySpec));

					// Auslesen des chiffrierten Textes
					File messageFile = new File("message.key");
					byte[] messageBytes = Files.readAllBytes(messageFile.toPath());

					System.out.println("Auslesen der Nachricht");
					byte[] decryptedMessageBytes = decryptCipher.doFinal(messageBytes);
					String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
					System.out.println("decryptedMessage = " + decryptedMessage);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}