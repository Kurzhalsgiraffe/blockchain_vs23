package client;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import model.Block;

public class Main {
	public static List<Block> blockchain = new ArrayList<Block>();
	public static int prefix = 4;
	public static String prefixString = new String(new char[prefix]).replace('\0', '0');

	public static void main(String[] args) {
//		Date date = new Date();

		// Erzeugen eines Blockes mit Textinhalt, Initialisierung
		// mit "0" (= Anfangsblock) und ggf. Übergabe des aktuellen Datums
		try {
//			Block genesisBlock = new Block("Dies ist der Ur-Block.", "0", new Date());
			Block genesisBlock = new Block("Dies ist der Ur-Block.", "0");
			blockchain.add(genesisBlock);

//			Block newBlock = new Block("Dies ist der erste Block.", genesisBlock.getHash(), new Date());
			Block newBlock = new Block("Dies ist der erste Block.", genesisBlock.getHash());
			blockchain.add(newBlock);

//			newBlock = new Block("Dies ist der zweite Block.", newBlock.getHash(), new Date());
			newBlock = new Block("Dies ist der zweite Block.", newBlock.getHash());
			blockchain.add(newBlock);

//			Block newBlock = new Block("Dies ist der dritte Block.", firstBlock.getHash(), new Date());
			newBlock = new Block("Dies ist der dritte Block.", newBlock.getHash());
			blockchain.add(newBlock);

			// Korrupter Block:
//			newBlock = new Block("Dies ist der zweite Block.", genesisBlock.getHash(), new Date());
			newBlock = new Block("Dies ist der zweite Block.", genesisBlock.getHash());
			blockchain.add(newBlock);

			for (int i = 0; i < blockchain.size(); i++)
				System.out.printf("blockchain.get(%d)=%s\n", i, blockchain.get(i));

			System.out.printf("\n\nValidierung:");
			// Validierung der Blockchain
			boolean flag = true;
			for (int i = 0; i < blockchain.size(); i++) {
				System.out.println("\ni = " + i);
				String previousHash = i == 0 ? "0" : blockchain.get(i - 1).getHash();
				flag = blockchain.get(i).getHash().equals(blockchain.get(i).calculateBlockHash())
						&& previousHash.equals(blockchain.get(i).getPreviousHash())
						&& blockchain.get(i).getHash().substring(0, prefix).equals(prefixString);
				System.out.println("flag = " + flag);
				System.out.println("blockchain = " + blockchain);
				System.out.println("blockchain.hashCode() = " + blockchain.hashCode());
				if (!flag)
					break;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}