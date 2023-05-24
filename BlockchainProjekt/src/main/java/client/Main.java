package client;

import java.io.UnsupportedEncodingException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.classmate.util.ResolvedTypeCache.Key;

import dao.*;
import model.block.Block;
import model.entity.MyBlockchainuserKeys;

public class Main {

	public static void usage() {
		System.out.println("Usage: java Main EinrichtenMiner");				// Schritt 1
		System.out.println("Usage: java Main ErsterLoginErsterBenutzer");	// Schritt 2
		System.out.println("Usage: java Main LoginZweiterBenutzer");		// Schritt 3
		System.out.println("Usage: java Main Read");						// beliebig nach Schritt 2
		System.out.println("Usage: java Main Validate");					// beliebig nach Schritt 2
		System.out.println("Usage: java Main DoSomethingWithTheBlock");		// beliebig nach Schritt 2
	}

	public static void main(String[] args) throws NoSuchRowException {
		// Zu ersetzen mit konkreten User-Angaben
		String miner = "minerProjektVS_SS23";          
		String minerPassword = "minerProjektVS_SS23";
		String firstUser = "hantscma";
		String firstPassword = "hantscma";
		String secondUser = "rothnina";
		String secondPassword = "rothnina";
		String thirdUser = "heinzelu";
		String thirdPassword = "heinzelu";

		// new ElectionGui();

		if (args.length < 1) {
			usage();
		} else {
			MyBlockManager blockManagerMiner = new MyBlockManager("BlockchainMiner", miner, minerPassword);
			MyBlockManager blockManagerFirstUser = new MyBlockManager("FirstUser", firstUser, firstPassword);
			MyBlockManager blockManagerSecondUser = new MyBlockManager("SecondUser", secondUser, secondPassword);
			MyBlockManager blockManagerThirdUser = new MyBlockManager("ThirdUser", thirdUser, thirdPassword);

			List<Block> blockList = new ArrayList<Block>();
	
//EinrichtenMiner
			if (args[0].equals("EinrichtenMiner")) {
				initMiner(miner, minerPassword);

//ErsterLoginErsterBenutzer
			} else if (args[0].equals("ErsterLoginErsterBenutzer")) {			
				Block block = null;
				
				if (!userExists(firstUser, firstPassword)) {
					initUser("FirstUser", firstUser, firstPassword);
				}
				InitBlockchainManager bc1User = getUser("FirstUser", firstUser, firstPassword);
				
				try {
					byte[] encryptedFirstUser = RSA.encrypt(firstUser, bc1User.getMyKeys().getPublickey());
					byte[] data = RSA.encrypt(new String(encryptedFirstUser) + "Wahlergebnis: CDU", bc1User.getMyKeys().getPublickey()); // TODO: MUSS PUBLIC KEY DES MINERS SEIN
					block = new Block(data, 0);
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				List<Block> myBlockList = blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock());
				for (Block b : myBlockList) {
					System.out.println("block = " + b);
				}

				// Speicherung der Bloecke fuer Miner und Benutzer
				try {
					blockList.add(block); // Kopiere in Liste fuer Validierung (s.u.)
					block.setId(blockManagerMiner.calculateNextId());
					// je nach Anwendungsfalls ggf. vor Speicherung einzubauen!?
					// blockManagerMiner.doSomethingWithTheBlock(block, user);

					blockManagerMiner.append(block); // Speichern des Blocks auf DB des Miners
					blockManagerFirstUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));
					
				} catch (SaveException e) {
					e.printStackTrace();
				} catch (NoEntityFoundException e) {
					e.printStackTrace();
				} catch (TargetListNotEmptyException e) {
					e.printStackTrace();
				}

//LoginZweiterBenutzer
			} else if (args[0].equals("LoginZweiterBenutzer")) {
				Block block = null;

				if (!userExists(firstUser, firstPassword)) {
					initUser("SecondUser", secondUser, secondPassword);
				}
				InitBlockchainManager bc2User = getUser("SecondUser", secondUser, secondPassword);
				
				try {
					byte[] encryptedSecondUser = RSA.encrypt(firstUser, bc2User.getMyKeys().getPublickey());
					byte[] data = RSA.encrypt(new String(encryptedSecondUser) + "Wahlergebnis: FDP", bc2User.getMyKeys().getPublickey()); // TODO: MUSS PUBLIC KEY DES MINERS SEIN
 					block = new Block(data, 0);
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// Kopiere zuerst Liste von Miner
				List<Block> newList = blockManagerMiner.list();
				for (Block b : newList) {
					System.out.println("block = " + b);
					try {
						Block newBlock = b.copy();
						blockManagerSecondUser.append(newBlock);
					} catch (SaveException e) {
						e.printStackTrace();
					} catch (NoEntityFoundException e) {
						e.printStackTrace();
					}
				}

				// Speicherung der Bloecke fuer Miner und Benutzer (secondUser)
				try {
						blockList.add(block); // Kopiere in Liste fuer Validierung (s.u.)
						block.setId(blockManagerMiner.calculateNextId());
						// je nach Anwendungsfalls ggf. vor Speicherung einzubauen!?
						// blockManagerMiner.doSomethingWithTheBlock(block, user);

						blockManagerMiner.append(block); // Speichern des Blocks auf DB des Miners
						blockManagerSecondUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock()));
				} catch (SaveException e) {
					e.printStackTrace();
				} catch (NoEntityFoundException e) {
					e.printStackTrace();
				} catch (TargetListNotEmptyException e) {
					e.printStackTrace();
				}

//Read
			} else if (args[0].equals("Read")) {
				// Auslesen der Bloecke fuer Miner
				System.out.println("---------------------------------");
				System.out.println("Ausgabe der Blockliste des Miners");
				for (Block obj : blockManagerMiner.list())
					System.out.println("obj = " + obj);

				System.out.println("---------------------------------");
				System.out.println("---------------------------------");
				System.out.println("Ausgabe der Blockliste des Miners mit Beschraenkung auf Daten");
				for (Block obj : blockManagerMiner.list()) {
					System.out.println("\nDatum als Bytearray = " + Arrays.toString(obj.getDataAsObject()));
					System.out.println("von Bytearray zurueckkonvertiertes Datum als Text = " + new String(obj.getDataAsObject(), StandardCharsets.UTF_8));
				}

				System.out.println("---------------------------------");
				System.out.println("---------------------------------");
				System.out.println("Ausgabe der Blockliste des Users mit Beschraenkung auf Daten");
				for (Block obj : blockManagerFirstUser.list()) {
					System.out.println("\nDatum als Bytearray = " + Arrays.toString(obj.getDataAsObject()));
					System.out.println("von Bytearray zurueckkonvertiertes Datum als Text = " + new String(obj.getDataAsObject(), StandardCharsets.UTF_8));
				}

//Validate
			} else if (args[0].equals("Validate")) {
				// Validierung kann einfach ueberprueft werden, wenn Block in Miner manipuliert wird
				BlockValidator blockvalidator = blockManagerMiner.validateBlockChain(blockManagerMiner.list());
				System.out.println("blockvalidator = " + blockvalidator);

//DoSomethingWithTheBlock
			} else if (args[0].equals("DoSomethingWithTheBlock")) {
				int numberCoronaVaccinations = 0;
				for (Block obj : blockManagerFirstUser.list()) {
					numberCoronaVaccinations += blockManagerFirstUser.doSomethingWithTheBlock(obj, "Covid-19");
					System.out.println("numberCoronaVaccinations = " + numberCoronaVaccinations);
				}

			} else {
				usage();
			}
		}
	}

	public static void initMiner(String miner, String minerPassword) throws NoSuchRowException {
		InitBlockchainManagerMiner initForMiner = new InitBlockchainManagerMiner("BlockchainMiner", miner, minerPassword);
		try {
			initForMiner.initDatabase(); // initialisiere Datenbank fuer Miner
			initForMiner.initKeys();
		} catch (InitializationAlreadyDoneException e) {
			e.printStackTrace();
		}
	}

	public static void initUser(String persistanceUnit, String username, String userPassword) throws NoSuchRowException {

		InitBlockchainManager initForUser = new InitBlockchainManager(persistanceUnit, username, userPassword);
		try {
			initForUser.initDatabase(); // initialisiere Datenbank fuer User
			initForUser.initKeys();
		} catch (InitializationAlreadyDoneException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean userExists(String username, String userPassword) throws NoSuchRowException {
		BlockchainuserDao b1 = new BlockchainuserDao();
		return b1.userExists(username, userPassword);
	}
	
	public static InitBlockchainManager getUser(String persistanceUnit, String username, String userPassword) throws NoSuchRowException {
		BlockchainuserDao b1 = new BlockchainuserDao();
		return b1.getUser(persistanceUnit, username, userPassword);
	}
}
