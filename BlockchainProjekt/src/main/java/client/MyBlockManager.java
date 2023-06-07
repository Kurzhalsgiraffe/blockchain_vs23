package client;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
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

	// wird genutzt in MyBlockManager "doSomethingWithTheBlock" zur �berpr�fung
	// einer g�ltigen "userid"
	@Override
	public String getUserId() {
		return (String) super.getEntityManager().createNativeQuery("select user from dual").getSingleResult();
	}

	@Override
	public Date getInsertDate() {
		return (Date) super.getEntityManager().createNativeQuery("select sysdate from dual").getSingleResult();
	}

	@Override
	public int doSomethingWithTheBlock(Block theBlock, String searchText) {
//		System.out.println("doSomethingWithTheBlock:: theBlock = " + theBlock);
		System.out.println(
				"doSomethingWithTheBlock:: Datum als Bytearray = " + Arrays.toString(theBlock.getDataAsObject()));

		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = null;
		try {
			keys = keysDao.getMyKeys();
		} catch (NoSuchRowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String decryptedText = RSA.decrypt(theBlock.getDataAsObject(), keys.getPrivatekey());

//		String text = new String(theBlock.getDataAsObject(), StandardCharsets.UTF_8);
		System.out.println(
				"doSomethingWithTheBlock :: text (von Bytearray zurueckkonvertiertes Datum) = " + decryptedText);

		// nur zur Demonstration: Textersetzung, z.B. f�r internationale Ausgabe
		// (logischerweise KEIN Speichern im Block)
		int returnValue = 0;
		String result = null;
//		String wahl = decryptedText.split("Wahlergebnis: ");
		switch (searchText) {
		case "CDU":
			if (decryptedText.contains(new StringBuffer("CDU")))
				returnValue = 1;
			break;
		case "FDP":
			if (decryptedText.contains(new StringBuffer("FDP")))
				returnValue = 1;
			break;
		}
		return returnValue;
	}

}
