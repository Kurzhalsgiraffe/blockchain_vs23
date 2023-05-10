package model.block;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Validator {

//	public boolean validate(Block toValidateBlock) {
//		Collection<Block> blockList = getBlockByHash(toValidateBlock.getHash());
//		for (Block b : blockList) {
//			if (toValidateBlock.getData().compareTo(b.getData()) == 0)
//				return true;
//		}
//		return false;
//	}

	// Vergleich zweier Block-Arrays
	public static int validate(Block[] realBlock, Block[] validateBlock) {
		System.out.println("validate:: realBlock = " + realBlock);
		System.out.println("validate:: validateBlock = " + validateBlock);
		int i = 0;
		for (i = 0; i < realBlock.length; i++) {
			if (realBlock[i] != null && validateBlock[i] != null) {
//				System.out.println("---------------------------------");
//				System.out.printf("realBlock[%d] = %s\n", i, realBlock[i]);
//				System.out.printf("validateBlock[%d] = %s\n", i, validateBlock[i]);
//				System.out.println("realBlock[i].getHash().compareTo(validateBlock[i].getHash())= "
//						+ realBlock[i].getData().compareTo(validateBlock[i].getData()));
//				if (realBlock[i].getData().compareTo(validateBlock[i].getData()) != 0
//						&& realBlock[i].getHash().compareTo(validateBlock[i].getHash()) != 0) {
				if (realBlock[i].getHash().compareTo(validateBlock[i].getHash()) != 0) {
//					System.out.println("ungleich i = " + i);
					return i;
				}
			}
		}
		return realBlock.length;
	}

	// fuer spaetere Nutzung: Uberpruefung einer Kette in sich ...
	// (beispielsweise beim Kopieren einer Kette auf einen anderen Knoten, User,
	// Datenbank)
//	public static int validate(Block[] realBlock) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//		boolean flag = true;
//		for (int i = 0; i < realBlock.length; i++) {
//			if (realBlock[i] != null) {
//				System.out.println("\ni = " + i);
//				String previousHash = i == 0 ? "0" : realBlock[i - 1].getHash();
//				flag = realBlock[i].getHash().equals(realBlock[i].calculateBlockHash())
//						&& previousHash.equals(realBlock[i].getPreviousHash());
//				System.out.println("flag = " + flag);
//				System.out.println("blockchain = " + realBlock);
//				System.out.println("blockchain.hashCode() = " + realBlock.hashCode());
//				if (!flag)
//					break;
//			}
//		}
//		return 0;
//	}
}
