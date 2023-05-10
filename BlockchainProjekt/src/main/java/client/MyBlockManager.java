package client;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import dao.BlockManager;
import model.block.Block;


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
	public int doSomethingWithTheBlock(Block theBlock, String substitution) {
//		System.out.println("doSomethingWithTheBlock:: theBlock = " + theBlock);
		System.out.println(
				"doSomethingWithTheBlock:: Datum als Bytearray = " + Arrays.toString(theBlock.getDataAsObject()));
		String text = new String(theBlock.getDataAsObject(), StandardCharsets.UTF_8);
		System.out.println("doSomethingWithTheBlock :: text (von Bytearray zur�ckkonvertiertes Datum) = " + text);

		// nur zur Demonstration: Textersetzung, z.B. f�r internationale Ausgabe
		// (logischerweise KEIN Speichern im Block)
		String result = text.replace("Corona", substitution);

		System.out.println("doSomethingWithTheBlock :: result = " + result);
		if (result.compareTo(text) == 0) 
			return 0; // Text gefunden
		else
			return 1; // Text nicht gefunden
	}

}
