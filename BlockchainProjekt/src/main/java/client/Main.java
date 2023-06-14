package client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import dao.*;
import model.block.Block;

public class Main {
	private final static int INITBASE_MINER = 4096;
	private final static int INITBASE_USER = 1024;

	private static HashMap<String, Integer> electionResult = new HashMap<String, Integer>();

	public static void usage() {
		System.out.println("Usage: java Main EinrichtenMiner");			// Schritt 0
		System.out.println("Usage: java Main ZulassungUser");			// Schritt 1
		System.out.println("Usage: java Main LoginErsterBenutzer");		// Schritt 2
		System.out.println("Usage: java Main LoginZweiterBenutzer");	// Schritt 3
		System.out.println("Usage: java Main Read");					// beliebig nach Schritt 2
		System.out.println("Usage: java Main Validate");				// beliebig nach Schritt 2
		System.out.println("Usage: java Main EvaluateElection");		// beliebig nach Schritt 2
	}

	public static void main(String[] args) throws Exception {
		String miner = "minerProjektVS_SS23";          
		String minerPassword = "minerProjektVS_SS23";

		String firstUserName = "hantscma";
		String firstUserPassword = "hantscma";
		String firstUserChoice = "Gruene";

		String secondUserName = "rothnina";
		String secondUserPassword = "rothnina";
		String secondUserChoice = "Tierschutzpartei";

		String thirdUserName = "heinzelu";
		String thirdUserPassword = "heinzelu";
		String thirdUserChoice = "CDU";

		// new ElectionGui();

		if (args.length < 1) {
			usage();
		} else {
			MyBlockManager blockManagerMiner = new MyBlockManager("BlockchainMiner", miner, minerPassword);
			MyBlockManager blockManagerFirstUser = new MyBlockManager("FirstUser", firstUserName, firstUserPassword);
			MyBlockManager blockManagerSecondUser = new MyBlockManager("SecondUser", secondUserName, secondUserPassword);
			MyBlockManager blockManagerThirdUser = new MyBlockManager("ThirdUser", thirdUserName, thirdUserPassword);

			List<Block> blockList = new ArrayList<Block>();

//EinrichtenMiner
			if (args[0].equals("EinrichtenMiner")) {
			initMiner(miner, minerPassword);

//ZulassungUser
			} else if (args[0].equals("ZulassungUser")) {
				permitUser(firstUserName, firstUserPassword);
				permitUser(secondUserName, secondUserPassword);
				permitUser(thirdUserName, thirdUserPassword);

//LoginErsterBenutzer
			} else if (args[0].equals("LoginErsterBenutzer")) {			
				Block block = null;

				if (userEligible(firstUserName, firstUserPassword)) {
					System.out.println(firstUserName + "ist zur Wahl zugelassen");
					if (!userHasPublicKey(firstUserName)) {
						System.out.println(firstUserName + "hat keinen Public Key, versuche ihn anzulegen");
						initUser("FirstUser", firstUserName, firstUserPassword);
					}
					InitBlockchainManager bc1User = getUser("FirstUser", firstUserName, firstUserPassword);

					try {
						byte[] encryptedFirstUser = RSA.encrypt(firstUserName, bc1User.getMyKeys().getPublickey());
						byte[] data = RSA.encrypt(new String(encryptedFirstUser) + "Wahlergebnis: " + firstUserChoice, getMinerPublicKey());
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

						blockManagerMiner.append(block); // Speichern des Blocks auf DB des Miners
						blockManagerFirstUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));

					} catch (SaveException e) {
						e.printStackTrace();
					} catch (NoEntityFoundException e) {
						e.printStackTrace();
					} catch (TargetListNotEmptyException e) {
						e.printStackTrace();
					}
				}

//LoginZweiterBenutzer
			} else if (args[0].equals("LoginZweiterBenutzer")) {
				Block block = null;

				if (userEligible(firstUserName, firstUserPassword)) {
					if (!userHasPublicKey(secondUserName)) {
						initUser("SecondUser", secondUserName, secondUserPassword);
					}
					InitBlockchainManager bc2User = getUser("SecondUser", secondUserName, secondUserPassword);
					
					try {
						byte[] encryptedSecondUser = RSA.encrypt(secondUserName, bc2User.getMyKeys().getPublickey());
						byte[] data = RSA.encrypt(new String(encryptedSecondUser) + "Wahlergebnis: " + secondUserChoice, getMinerPublicKey());
						block = new Block(data, 0);
					} catch (NoSuchAlgorithmException e1) {
						e1.printStackTrace();
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
	
					// Kopiere zuerst Liste von Miner
					List<Block> myBlockList = blockManagerMiner
							.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock());
					for (Block b : myBlockList) {
						System.out.println("block = " + b);
					}
	
					// Speicherung der Bloecke fuer Miner und Benutzer (secondUser)
					try {
							blockList.add(block); // Kopiere in Liste fuer Validierung (s.u.)
							block.setId(blockManagerMiner.calculateNextId());
	
							blockManagerMiner.append(block); // Speichern des Blocks auf DB des Miners
							blockManagerSecondUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock()));
					} catch (SaveException e) {
						e.printStackTrace();
					} catch (NoEntityFoundException e) {
						e.printStackTrace();
					} catch (TargetListNotEmptyException e) {
						e.printStackTrace();
					}
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

//EvaluateElection
			} else if (args[0].equals("EvaluateElection")) {
				for (Block obj : blockManagerMiner.list()) {
					String res = blockManagerMiner.getSelectedPartyFromBlock(obj);
					electionResult.merge(res, 1, Integer::sum);
				}

				for (String party: electionResult.keySet()) {
				    System.out.println(party.toString() + " " + electionResult.get(party).toString());
				}

//PruefeAbgegebeneWahl ErsterBenutzer
			} else if (args[0].equals("PruefeAbgegebeneWahl ErsterBenutzer")) {
				for (Block obj : blockManagerMiner.list()) {
					// lies private Key von Erstem Benutzer
					// lies private Key von Miner
					// durchsuche Block  und dechiffriere mit sk von Miner
					// suche nach User-LKennung des betreffenden Wählers ...
					// Ergebnis:
					// 1. 1, falls Wähler Stimme abgegeben hat
					// 2. 0, falls Wähler nicht gewählt hat

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
			initForMiner.initKeys(INITBASE_MINER );
		} catch (InitializationAlreadyDoneException e) {
			e.printStackTrace();
		}
	}

	public static void initUser(String persistanceUnit, String username, String userPassword) throws NoSuchRowException {

		InitBlockchainManager initForUser = new InitBlockchainManager(persistanceUnit, username, userPassword);
		try {
			initForUser.initDatabase(); // initialisiere Datenbank fuer User
			initForUser.initKeys(INITBASE_USER);
		} catch (InitializationAlreadyDoneException e) {
			e.printStackTrace();
		}
	}

	public static void permitUser(String username, String userPassword) throws NoSuchRowException {
		BlockchainuserDao b1 = new BlockchainuserDao();
		b1.save(username, userPassword);
	}

	public static boolean userEligible(String username, String userPassword) throws NoSuchRowException {
		BlockchainuserDao b1 = new BlockchainuserDao();
		return b1.userEligible(username, userPassword);
	}

	public static boolean userHasPublicKey(String username) throws NoSuchRowException {
		BlockchainuserDao b1 = new BlockchainuserDao();
		return b1.userHasPublicKey(username);
	}

	public static InitBlockchainManager getUser(String persistanceUnit, String username, String userPassword) throws NoSuchRowException {
		InitBlockchainManager manager = new InitBlockchainManager(persistanceUnit, username, userPassword);
		return manager;
	}

	public static PublicKey getMinerPublicKey() throws NoSuchRowException {
		MyBlockchainuserKeysDao bcuK = new MyBlockchainuserKeysDao();
		PublicKey publicKeyMiner = bcuK.getMyKeys().getPublickey();
		return publicKeyMiner;
	}
}
