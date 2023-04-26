package model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NativeBlock {
	private String hashAlgorithm;
	private String originHash;
	private String hash;
	private String data;
	private int nonce;

	public NativeBlock(String hashAlgorithm, String data, int prefix)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.hashAlgorithm = hashAlgorithm;
		this.data = data;
		this.originHash = this.hash = calculateBlockHashForOrigin();
		this.hash = mineBlock(prefix);
	}

	// "Schuerfen" nach einem Hashcode mit einer gegebenen Anzahl ("prefex") an
	// Führungszeichen (hier '0'); mit jedem Schritt wird die "nonce"-Zahl erhöht
	public String mineBlock(int prefix) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String prefixString = new String(new char[prefix]).replace('\0', '0');
		while (!hash.substring(0, prefix).equals(prefixString)) {
			nonce++;
			hash = calculateBlockHash();
//			System.out.printf("mineBlock: nonce = %d hash = %s\n", nonce, hash);
		}
		return hash;
	}

	public String calculateBlockHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String dataToHash = Integer.toString(nonce) + data;

		MessageDigest digest = null;
		byte[] bytes = null;
		// Festlegen des Hashalgorithmus
		digest = MessageDigest.getInstance(hashAlgorithm);

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
		MessageDigest digest = null;
		byte[] bytes = null;
		// Festlegen des Hashalgorithmus
		digest = MessageDigest.getInstance(hashAlgorithm);

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

	@Override
	public String toString() {
		return super.toString() + "\tNativeBlock [hashAlgorithm=" + hashAlgorithm + ", originHash=" + originHash
				+ ", hash=" + hash + ", data=" + data + ", nonce=" + nonce + "]";
	}

}