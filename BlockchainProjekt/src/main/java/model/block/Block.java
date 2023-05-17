package model.block;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;

//import jakarta.persistence.*;
import javax.persistence.*;

import dao.MyBlockchainuserKeysDao;
import dao.NoSuchRowException;
import model.entity.MyBlockchainuserKeys;

// create table Block (
//    id integer primary key,              
//    dataAsobject blob,
//    previousHash varchar2(255) not null,
//    hash varchar2(255) not null, 
//    insertDate date not null,             
//    userId varchar2(255) not null,       
//    instanceId varchar2(32) not null,    
//    nonce integer not null,
//    hashAlgorithm varchar2(20) not null,
//    codeBase varchar2(20)not null,
//    prefix integer not null
// );

@Entity
@Table (name="block")
public class Block implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

//	private final static String DEFAULT_HASH_ALGORITHM = "SHA-256";
	private final static String DEFAULT_HASH_ALGORITHM = "SHA-1";
	private final static String DEFAULT_CODE_BASE = "UTF-8";	
	
	@Id
	private BigDecimal id;
	//	@Lob
	private byte[] dataAsObject;
	private String userId;
	private String previousHash;
	private String hash;
	private Date insertDate;
	private long nonce;
	private String instanceId;	
	private String codeBase;


	private String hashAlgorithm;
	private int prefix;

	// Notwendige Bedingung fuer JPA!!
	protected Block() {
	}

	private Block(byte[] dataAsObject, String previousHash, String userId, String instanceId, Date insertDate,
			String hashAlgorithm, String codeBase, int prefix)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.previousHash = previousHash;
		this.dataAsObject = dataAsObject;
		this.insertDate = insertDate;
		this.userId = userId;
		this.instanceId = instanceId;
		this.hashAlgorithm = hashAlgorithm;
		this.codeBase = codeBase;
		this.hash = calculateBlockHash();
		this.userId = userId;
		this.insertDate = insertDate;
		this.prefix = prefix;
		this.hash = mineBlock(prefix);
	}

	public Block(byte[] dataAsObject, int prefix) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this(dataAsObject, "", "", "", null, DEFAULT_HASH_ALGORITHM, DEFAULT_CODE_BASE, prefix);
	}

	// "Schuerfen" nach einem Hashcode mit einer gegebenen Anzahl ("prefix") an
	// Fuehrungszeichen (hier '0');
	// Mit jedem Schritt wird dabei die "nonce"-Zahl erhoeht
	public String mineBlock(int prefix) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String prefixString = new String(new char[prefix]).replace('\0', '0');
		while (!hash.substring(0, prefix).equals(prefixString)) {
			nonce++;
			hash = calculateBlockHash();
		}
		return hash;
	}

	// Berechnen des Hashcodes zu einem Text, i.e. MIT Hashcode des
	// vorausgehenden Knotens und MIT Erzeugungsdatum
	public String calculateBlockHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// pruefe, ob Datum == null (evtl. durch alternativen Konstruktor der Fall
//		String dataToHash = date != null ? previousHash + Long.toString(date.getTime()) + Integer.toString(nonce) + data
//				: previousHash + Integer.toString(nonce) + data;
		/////////////////////////////////
		// Fuer Testzwecke und zur Ueberpruefung der Hashwerte Berechnung ohne Datum
//		String dataToHash = previousHash + Long.toString(nonce) + dataAsObject;
		/////////////////////////////////
		String dataToHash = previousHash + Long.toString(nonce) + Arrays.toString(dataAsObject);
//		String dataToHash = previousHash + Long.toString(nonce) + getDataAsString();

		MessageDigest digest = null;
		byte[] bytes = null;
		// Festlegen des Hashalgorithmus
		digest = MessageDigest.getInstance(hashAlgorithm);

		// Festlegung der Basis der Zeichencodierung
		bytes = digest.digest(dataToHash.getBytes(codeBase));
		
		StringBuffer buffer = new StringBuffer();
		for (byte b : bytes)
			buffer.append(String.format("%02x", b));
		return buffer.toString();
	}

	private PublicKey getMinerPublicKey() throws NoSuchRowException {
		MyBlockchainuserKeysDao bcuK = new MyBlockchainuserKeysDao();
		PublicKey publicKeyMiner = bcuK.getMyKeys().getPublickey();
		return publicKeyMiner;
	}
	private String getDataAsString() {
		int result = 0;
		for (int i = 0; i < dataAsObject.length; i++)
			result += dataAsObject[i];
		return String.format("%s", result);
	}

	public int getPrefix() {
		return prefix;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getHash() {
		return this.hash;
	}

	public String getPreviousHash() {
		return this.previousHash;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public String getCodeBase() {
		return codeBase;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public long getNonce() {
		return nonce;
	}

	public byte[] getDataAsObject() {
		return dataAsObject;
	}

	public void setDataAsObject(byte[] dataAsObject) {
		this.dataAsObject = dataAsObject;
	}

	public Block copy() {
		Block blockToCopy = new Block();
		blockToCopy.id = id;
		blockToCopy.dataAsObject = dataAsObject;
		blockToCopy.previousHash = previousHash;
		blockToCopy.hash = hash;
		blockToCopy.insertDate = insertDate;
		blockToCopy.nonce = nonce;
		blockToCopy.userId = userId;
		blockToCopy.instanceId = instanceId;
		blockToCopy.hashAlgorithm = hashAlgorithm;
		blockToCopy.codeBase = codeBase;
		return blockToCopy;
	}

	@Override
	public String toString() {
		if (insertDate != null)
			return "Block [id=" + id + "\n\tdataAsObject=" + Arrays.toString(dataAsObject) + "\n\tpreviousHash="
					+ previousHash + "\n\thash=" + hash + "\n\tinsertDate=" + insertDate
					+ "\n\tLong.toString(date.getTime())=" + Long.toString(insertDate.getTime()) + "\n\tnonce=" + nonce
					+ "\n\tuserId=" + userId + "\n\tinstanceId=" + instanceId + "\n\thashAlgorithm=" + hashAlgorithm
					+ "\n\tcodeBase=" + codeBase + "\n\tprefix=" + prefix + "]";
		else
			return "Block [id=" + id + "\n\tdataAsObject=" + Arrays.toString(dataAsObject) + "\n\tpreviousHash="
					+ previousHash + "\n\thash=" + hash + "\n\tinsertDate=" + insertDate + "\n\tnonce=" + nonce
					+ "\n\tuserId=" + userId + "\n\tinstanceId=" + instanceId + "\n\thashAlgorithm=" + hashAlgorithm
					+ "\n\tcodeBase=" + codeBase + "]";
	}

}