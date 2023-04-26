package model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class Block {
//	private final static String HASH_ALGORITHM = "SHA-256";
	private final static String HASH_ALGORITHM = "SHA-1";

	private final static int DEFAULT_PREFIX = 4;

	private String originHash;
	private String hash;
	private String previousHash;
	private String data;
	private Date date;
	private int nonce;
	private MessageDigest digest = null;

	public Block(String data, String previousHash, Date date, int prefix)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.data = data;
		this.previousHash = previousHash;
		this.date = date;
		
		// Festlegen des Hashalgorithmus
		digest = MessageDigest.getInstance(HASH_ALGORITHM);

		this.originHash = this.hash = calculateBlockHashForOrigin();
//		System.out.printf("Konstruktor:\tpreviousHash = %s Long.toString(date.getTime()) = %s nonce = %d \n",
//				previousHash, Long.toString(date.getTime()), nonce);
//		System.out.printf("\t\tdata = %s hash = %s\n", data, hash);
		mineBlock(prefix);
	}

	public Block(String data, String previousHash, Date date)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this(data, previousHash, date, DEFAULT_PREFIX);
	}

	public Block(String data, String previousHash) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this(data, previousHash, null, DEFAULT_PREFIX);
	}

	// "Schuerfen" nach einem Hashcode mit einer gegebenen Anzahl ("prefix") an
	// Führungszeichen (hier '0');
	// mit jedem Schritt wird die "nonce"-Zahl erhöht
	public String mineBlock(int prefix) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String prefixString = new String(new char[prefix]).replace('\0', '0');
		while (!hash.substring(0, prefix).equals(prefixString)) {
			nonce++;
			hash = calculateBlockHash();
		}
		System.out.printf("mineBlock: nonce = %d hash = %s\n", nonce, hash);
		return hash;
	}

	// Berechnen des Hashcodes zu einem Text, i.e. MIT Hashcode des
	// vorausgehenden Knotens und MIT Erzeugungsdatum
	public String calculateBlockHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// pruefe, ob Datum == null (evtl. durch alternativen Konstruktor der Fall
		String dataToHash = date != null ? previousHash + Long.toString(date.getTime()) + Integer.toString(nonce) + data
				: previousHash + Integer.toString(nonce) + data;

		byte[] bytes = null;

		// Festlegung der Basis der Zeichencodierung
		bytes = digest.digest(dataToHash.getBytes("UTF-8"));

		// Konvertierung des als Bytearray berechneten Hashcodes nach Stringbuffer
		StringBuffer buffer = new StringBuffer();
		for (byte b : bytes)
			buffer.append(String.format("%02x", b));

		return buffer.toString();
	}

	public String calculateBlockHashForOrigin() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String dataToHash = data;
		byte[] bytes = null;
		// Festlegung der Basis der Zeichencodierung
		bytes = digest.digest(dataToHash.getBytes("UTF-8"));

		// Konvertierung des als Bytearray berechneten Hashcodes nach Stringbuffer
		StringBuffer buffer = new StringBuffer();
		for (byte b : bytes)
			buffer.append(String.format("%02x", b));
		return buffer.toString();
	}

	public String getHash() {
		return this.hash;
	}

	public String getOriginHash() {
		return originHash;
	}

	public int getNonce() {
		return nonce;
	}

	public String getPreviousHash() {
		return this.previousHash;
	}

	@Override
	public String toString() {
		return "Block [originHash=" + originHash + "\n\thash=" + hash + "\n\tpreviousHash=" + previousHash + "\n\tdata="
				+ data + "\n\tdate=" + date + "\n\tnonce=" + nonce + "]";
	}

}