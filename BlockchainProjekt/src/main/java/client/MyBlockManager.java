package client;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import client.RSA;
import dao.BlockManager;
import dao.MyBlockchainuserKeysDao;
import dao.NoSuchRowException;
import model.block.Block;
import model.entity.MyBlockchainuserKeys;


public class MyBlockManager extends BlockManager {

	public MyBlockManager(String persistenceUnit, String userId, String password) {
		super(persistenceUnit, userId, password);
	}

	@Override
	public BigDecimal calculateNextId() {
		return (BigDecimal) super.getEntityManager().createNativeQuery("select block_seq.nextval from dual")
				.getSingleResult();
	}

	// Proprietaere Datenbankanfragen - zu ersetzen bei Portierung!
	@Override
	public String getInstanceId() {
		return (String) super.getEntityManager()
				.createNativeQuery("select value from v$parameter where upper(name) = 'INSTANCE_NAME'")
				.getSingleResult();
	}

	// wird genutzt in MyBlockManager "getSelectedPartyFromBlock" zur Überprüfung
	// einer gültigen "userid"
	@Override
	public String getUserId() {
		return (String) super.getEntityManager().createNativeQuery("select user from dual").getSingleResult();
	}

	@Override
	public Date getInsertDate() {
		return (Date) super.getEntityManager().createNativeQuery("select sysdate from dual").getSingleResult();
	}

	@Override
	public String getSelectedPartyFromBlock(Block theBlock) {
		System.out.println("getSelectedPartyFromBlock:: Block als Bytearray = " + Arrays.toString(theBlock.getDataAsObject()));

		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = null;
		try {
			keys = keysDao.getMyKeys();
		} catch (NoSuchRowException e) {
			e.printStackTrace();
		}
		String decryptedText = RSA.decrypt(theBlock.getDataAsObject(), keys.getPrivatekey());

		String wahl = decryptedText.split("Wahlergebnis: ")[1];
		return wahl;
	}
	
	public boolean CheckIfUserElected(byte[] encryptedUser, Block block) {
		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = null;
		try {
			keys = keysDao.getMyKeys();
		} catch (NoSuchRowException e) {
			e.printStackTrace();
		}
		String decryptedText = RSA.decrypt(block.getDataAsObject(), keys.getPrivatekey());

		String user = decryptedText.split("Wahlergebnis: ")[0];
		if(encryptedUser.toString().equals(user)) {
			System.out.println("Du hast schon gewählt.");
			return true;
		}else {
			return false;
		}

	}
}
