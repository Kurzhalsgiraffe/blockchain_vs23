package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dao.BlockValidator;
import dao.BlockchainuserDao;
import dao.InitBlockchainManager;
import dao.InitBlockchainManagerMiner;
import dao.InitializationAlreadyDoneException;
import dao.MyBlockchainuserKeysDao;
import dao.NoSuchRowException;
import model.block.Block;

public class Main {
	private final static int INITBASE_MINER = 4096;
	private final static int INITBASE_USER = 1024;
	
	private final static String miner = "minerProjektVS_SS23";          
	private final static String minerPassword = "minerProjektVS_SS23";

	private final static String firstUserName = "hantscma";
	private final static String firstUserPassword = "hantscma";
	private final static String firstUserChoice = "Gruene";

	private final static String secondUserName = "rothnina";
	private final static String secondUserPassword = "rothnina";
	private final static String secondUserChoice = "Tierschutzpartei";

	private final static String thirdUserName = "heinzelu";
	private final static String thirdUserPassword = "heinzelu";
	private final static String thirdUserChoice = "CDU";

	private final static MyBlockManager blockManagerMiner = new MyBlockManager("BlockchainMiner", miner, minerPassword);
	private final static MyBlockManager blockManagerFirstUser = new MyBlockManager("FirstUser", firstUserName, firstUserPassword);
	private final static MyBlockManager blockManagerSecondUser = new MyBlockManager("SecondUser", secondUserName, secondUserPassword);

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

		// new ElectionGui();

		if (args.length < 1) {
			usage();
		} else {

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

				// Überprüfe ob der User zur Wahl zugelassen ist
				if (userEligible(firstUserName, firstUserPassword)) {
					System.out.println(firstUserName + " ist zur Wahl zugelassen");
					
					// Überprüfe ob User bereits ein Keypair erzeugt hat
					if (!userHasPublicKey(firstUserName)) {
						System.out.println(firstUserName + " hat keinen Public Key, versuche ihn anzulegen");
						initUser("SecondUser", firstUserName, firstUserPassword);
					}

					List<Block> myBlockList = blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock());
					for (Block b : myBlockList) {
						System.out.println("block = " + b);
					}
					
					InitBlockchainManager bc2User = getUser("FirstUser", firstUserName, firstUserPassword);
					byte[] encryptedUser = RSA.encrypt(firstUserName.getBytes(), bc2User.getMyKeys().getPublickey(), INITBASE_USER);

					if (blockManagerMiner.checkIfUserHasVoted(encryptedUser)) {
						System.out.println(firstUserName + " hat schon gewählt. Wahl wurde nicht übernommen");
					} else {
						// Generiere Block aus dem verschlüsselten Usernamen und dessen Wahl
						byte[] blockData = generateBlockData(encryptedUser, firstUserChoice);
						byte[] encryptedBlockData = RSA.encrypt(blockData, getMinerPublicKey(), INITBASE_MINER);
						
						// Erzeuge Block und weise ihm eine ID zu
						Block block = new Block(encryptedBlockData, 0);
						block.setId(blockManagerMiner.calculateNextId());

						// Speichere den Blocks auf der DB des Miners
						blockManagerMiner.append(block);
						blockManagerFirstUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerFirstUser.getIdFromLastBlock()));
					}
				} else {
					System.out.println("Fehler: " + firstUserName + " ist nicht zur Wahl zugelassen!");
				}

//LoginZweiterBenutzer
			} else if (args[0].equals("LoginZweiterBenutzer")) {
				
				// Überprüfe ob der User zur Wahl zugelassen ist
				if (userEligible(secondUserName, secondUserPassword)) {
					System.out.println(secondUserName + " ist zur Wahl zugelassen");
					
					// Überprüfe ob User bereits ein Keypair erzeugt hat
					if (!userHasPublicKey(secondUserName)) {
						System.out.println(secondUserName + " hat keinen Public Key, versuche ihn anzulegen");
						initUser("SecondUser", secondUserName, secondUserPassword);
					}

					List<Block> myBlockList = blockManagerMiner.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock());
					for (Block b : myBlockList) {
						System.out.println("block = " + b);
					}
					
					InitBlockchainManager bc2User = getUser("SecondUser", secondUserName, secondUserPassword);
					byte[] encryptedSecondUser = RSA.encrypt(secondUserName.getBytes(), bc2User.getMyKeys().getPublickey(), INITBASE_USER);

					if (blockManagerMiner.checkIfUserHasVoted(encryptedSecondUser)) {
						System.out.println(secondUserName + " hat schon gewählt. Wahl wurde nicht übernommen");
					} else {
						// Generiere Block aus dem verschlüsselten Usernamen und dessen Wahl
						byte[] blockData = generateBlockData(encryptedSecondUser, secondUserChoice);
						byte[] encryptedBlockData = RSA.encrypt(blockData, getMinerPublicKey(), INITBASE_MINER);
						
						// Erzeuge Block und weise ihm eine ID zu
						Block block = new Block(encryptedBlockData, 0);
						block.setId(blockManagerMiner.calculateNextId());

						// Speichere den Blocks auf der DB des Miners
						blockManagerMiner.append(block);
						blockManagerSecondUser.copyList(blockManagerMiner.getBlockListFromId(blockManagerSecondUser.getIdFromLastBlock()));
					}
				} else {
					System.out.println("Fehler: " + secondUserName + " ist nicht zur Wahl zugelassen!");
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
				HashMap<String, Integer> electionResult = new HashMap<String, Integer>();

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
	
	public static byte[] generateBlockData(byte[] encryptedUser, String userChoice) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write(encryptedUser.length);
		outputStream.write(userChoice.getBytes().length);
		try {
			outputStream.write(encryptedUser);
			outputStream.write(userChoice.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] blockData = outputStream.toByteArray();
		return blockData;
	}

	public static PublicKey getMinerPublicKey() throws NoSuchRowException {
		MyBlockchainuserKeysDao bcuK = new MyBlockchainuserKeysDao();
		PublicKey publicKeyMiner = bcuK.getMyKeys().getPublickey();
		return publicKeyMiner;
	}
}
