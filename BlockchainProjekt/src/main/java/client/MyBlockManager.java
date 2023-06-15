package client;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

	// wird genutzt in MyBlockManager "getSelectedPartyFromBlock" zur Ueberpruefung
	// einer gueltigen "userid"
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
		byte[] decryptedText = RSA.decrypt(theBlock.getDataAsObject(), keys.getPrivatekey());
		
		int encryptedUsernameLength = decryptedText[0];
		int choiceLength = decryptedText[1];
		if (encryptedUsernameLength < 0) {
			encryptedUsernameLength += 255;
		}
		if (choiceLength < 0) {
			choiceLength += 255;
		}
		String choice = new String(Arrays.copyOfRange(decryptedText, 2+encryptedUsernameLength, choiceLength + encryptedUsernameLength + 3));

		return choice;
	}
	
	public boolean checkIfUserHasVoted(byte[] encryptedUser) {
		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = null;
		try {
			keys = keysDao.getMyKeys();
		} catch (NoSuchRowException e) {
			e.printStackTrace();
		}

		List<Block> blockList = list();
		for (Block block : blockList){
			byte[] decryptedText = RSA.decrypt(block.getDataAsObject(), keys.getPrivatekey());
			
			int encryptedUsernameLength = decryptedText[0];
			if (encryptedUsernameLength < 0) {
				encryptedUsernameLength += 255;
			}

			byte[] encryptedUsername = Arrays.copyOfRange(decryptedText, 2, encryptedUsernameLength + 3);


			if(Arrays.equals(encryptedUser, encryptedUsername)) {
				return true;
			}
		}
		return false;
	}
}
