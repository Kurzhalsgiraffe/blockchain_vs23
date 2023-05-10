package client;

import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.*;
import model.block.Block;

public class Main {

	public static void usage() {
		System.out.println("Usage: java Main EinrichtenMiner"); // zuerst auszufuehren, alternativ SQL-Skript fuer Miner ausfuehren
		System.out.println("Usage: java Main ErsterLoginErsterBenutzer");  // Schritt 2
		System.out.println("Usage: java Main ZweiterLoginErsterBenutzer"); // Schritt 3 oder 4
		System.out.println("Usage: java Main LoginZweiterBenutzer");       // Schritt 2 oder 4
		System.out.println("Usage: java Main Read");                       // beliebig nach Schritt 2
		System.out.println("Usage: java Main Validate");                   // beliebig nach Schritt 2
		System.out.println("Usage: java Main DoSomethingWithTheBlock");    // beliebig nach Schritt 2
	}

	public static void main(String[] args) {
		// Zu ersetzen mit konkreten User-Angaben
		String miner = "minerProjektVS_SS23";          
		String minerPassword = "minerProjektVS_SS23";
		String firstUser = "hantscma";
		String firstPassword = "hantscma";
		String secondUser = "heinzelu";
		String secondPassword = "heinzelu";

		if (args.length < 1)
			usage();
		else {
			MyBlockManager blockManagerMiner = new MyBlockManager("BlockchainMiner", miner, minerPassword);
			MyBlockManager blockManagerFirstUser = new MyBlockManager("FirstUser", firstUser, firstPassword);
			MyBlockManager blockManagerSecondUser = new MyBlockManager("SecondUser", secondUser, secondPassword);

			List<Block> blockList = new ArrayList<Block>();

//EinrichtenMiner
			if (args[0].equals("EinrichtenMiner")) {
				InitBlockchainManagerMiner initForMiner = new InitBlockchainManagerMiner("BlockchainMiner", miner, minerPassword);
				try {
					initForMiner.initDatabase(); // initialisiere Datenbank fuer Miner
				} catch (InitializationAlreadyDoneException e2) {
					e2.printStackTrace();
				}

//ErsterLoginErsterBenutzer
			} else if (args[0].equals("ErsterLoginErsterBenutzer")) {
				InitBlockchainManager initForUser = new InitBlockchainManager("FirstUser", firstUser, firstPassword);
				try {
					initForUser.initDatabase(); // initialisiere Datenbank fuer User
				} catch (InitializationAlreadyDoneException e2) {
					e2.printStackTrace();
				}

				Block[] blockArray = new Block[4];
				try {
					// Parameter "0" = prefix
					blockArray[0] = new Block((firstUser + "erste Corona-Impfung mit Astra-Seneca").getBytes(), 0);
					blockArray[1] = new Block((firstUser + "zweite Corona-Impfung mit Astra-Seneca").getBytes(), 0);
					blockArray[2] = new Block((firstUser + "erste Impfung gegen Tetanus").getBytes(), 0);
					blockArray[3] = new Block((firstUser + "dritte Corona-Impfung mit Moderna").getBytes(), 0);
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				List<Block> myBlockList = blockManagerMiner
						.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock());
				for (Block block : myBlockList) {
					System.out.println("block = " + block);
				}

				// Speicherung der Bloecke fuer Miner und Benutzer
				try {
					for (int i = 0; i < blockArray.length; i++) {
						blockList.add(blockArray[i]); // Kopiere in Liste fuer Validierung (s.u.)
						blockArray[i].setId(blockManagerMiner.calculateNextId());
						// je nach Anwendungsfalls ggf. vor Speicherung einzubauen!?
						// blockManagerMiner.doSomethingWithTheBlock(blockArray[i], user);

						blockManagerMiner.append(blockArray[i]); // Speichern des Blocks auf DB des Miners
						blockManagerFirstUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));
					}
				} catch (SaveException e) {
					e.printStackTrace();
				} catch (NoEntityFoundException e) {
					e.printStackTrace();
				} catch (TargetListNotEmptyException e) {
					e.printStackTrace();
				}

//ZweiterLoginErsterBenutzer
			} else if (args[0].equals("ZweiterLoginErsterBenutzer")) {
				InitBlockchainManager initForUser = new InitBlockchainManager("FirstUser", firstUser, firstPassword);
				try {
					initForUser.initDatabase(); // initialisiere Datenbank fuer User
				} catch (InitializationAlreadyDoneException e2) {
					e2.printStackTrace();
				}

				Block[] blockArray = new Block[3];
				try {
					// Parameter "0" = prefix
					blockArray[0] = new Block((firstUser + "erste Impfung gegen FSME").getBytes(), 0);
					blockArray[1] = new Block((firstUser + "zweite Impfung gegen Tetanus").getBytes(), 0);
					blockArray[2] = new Block((firstUser + "vierte Corona-Impfung mit Biontech").getBytes(), 0);
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				System.out.println("blockManagerFirstUser.getIdFromLastBlock() = " + blockManagerFirstUser.getIdFromLastBlock());

				// Kopiere alle Bloecke vom Miner, die nach dem letzten Block auf der DB des ersten Benutzes gespeichert wurden
				try {
					blockManagerFirstUser
							.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));
				} catch (TargetListNotEmptyException e1) {
					e1.printStackTrace();
				}
				// Speicherung der neuen Bloecke fuer Miner und Benutzer
				try {
					for (int i = 0; i < blockArray.length; i++) {
						blockList.add(blockArray[i]); // Kopiere in Liste fuer Validierung (s.u.)
						blockArray[i].setId(blockManagerMiner.calculateNextId());
						// je nach Anwendungsfalls ggf. vor Speicherung einzubauen!?
						// blockManagerMiner.doSomethingWithTheBlock(blockArray[i], user);

						blockManagerMiner.append(blockArray[i]); // Speichern des Blocks auf DB des Miners
						blockManagerFirstUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));
					}
				} catch (SaveException e) {
					e.printStackTrace();
				} catch (NoEntityFoundException e) {
					e.printStackTrace();
				} catch (TargetListNotEmptyException e) {
					e.printStackTrace();
				}

//LoginZweiterBenutzer
			} else if (args[0].equals("LoginZweiterBenutzer")) {
				InitBlockchainManager initForSecondUser = new InitBlockchainManager("SecondUser", secondUser, secondPassword);
				try {
					initForSecondUser.initDatabase(); // initialisiere Datenbank fuer User
				} catch (InitializationAlreadyDoneException e2) {
					e2.printStackTrace();
				}
				Block[] blockArray = new Block[2];
				try {
					// Parameter "0" = prefix
					blockArray[0] = new Block((secondUser + "Guertelrose-Impfung").getBytes(), 0); // 0
					blockArray[1] = new Block((secondUser + "erste Corona Impfung").getBytes(), 0); // 0
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// Kopiere zuerst Liste von Miner
				List<Block> newList = blockManagerMiner.list();
				for (Block block : newList) {
					System.out.println("block = " + block);
					try {
						Block newBlock = block.copy();
						blockManagerSecondUser.append(newBlock);
					} catch (SaveException e) {
						e.printStackTrace();
					} catch (NoEntityFoundException e) {
						e.printStackTrace();
					}
				}

				// Speicherung der Bloecke fuer Miner und Benutzer (secondUser)
				try {
					for (int i = 0; i < blockArray.length; i++) {
						blockList.add(blockArray[i]); // Kopiere in Liste fuer Validierung (s.u.)
						blockArray[i].setId(blockManagerMiner.calculateNextId());
						// je nach Anwendungsfalls ggf. vor Speicherung einzubauen!?
						// blockManagerMiner.doSomethingWithTheBlock(blockArray[i], user);

						blockManagerMiner.append(blockArray[i]); // Speichern des Blocks auf DB des Miners
						blockManagerSecondUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock()));
					}
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
}
