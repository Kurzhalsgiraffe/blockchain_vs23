package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import dao.BlockValidator;
import dao.InitBlockchainManager;
import dao.InitializationAlreadyDoneException;
import dao.NoEntityFoundException;
import dao.NoSuchRowException;
import dao.SaveException;
import dao.TargetListNotEmptyException;
import model.block.Block;

public class Main {
	public final static int INITBASE_MINER = 4096;
	public final static int INITBASE_USER = 1024;

	private final static MinerManager miner = new MinerManager("BlockchainMiner", "minerProjektVS_SS23", "minerProjektVS_SS23");
	private final static UserManager firstUser = new UserManager("FirstUser", "hantscma", "hantscma", "Gruene");
	private final static UserManager secondUser = new UserManager("SecondUser", "rothnina", "rothnina", "FDP");
	private final static UserManager thirdUser = new UserManager("ThirdUser", "heinzelu", "heinzelu", "CDU");

	public static void usage() {
		System.out.println("Usage: java Main Einrichten");					// Schritt 0
		System.out.println("Usage: java Main Zulassung");					// Schritt 1
		System.out.println("Usage: java Main VoteErsterBenutzer");			// Schritt 2
		System.out.println("Usage: java Main VoteZweiterBenutzer");	    	// Schritt 3
		System.out.println("Usage: java Main VoteDritterBenutzer");	    	// Schritt 3
		System.out.println("Usage: java Main Read");						// beliebig nach Schritt 2
		System.out.println("Usage: java Main Validate");					// beliebig nach Schritt 2
		System.out.println("Usage: java Main WahlAuswerten");				// beliebig nach Schritt 2
		System.out.println("Usage: java Main PruefeWahlErsterBenutzer");	// beliebig nach Schritt 2
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			usage();
		} else {

//Einrichten
			if (args[0].equals("Einrichten")) {
			miner.init();
			firstUser.init();
			secondUser.init();
			thirdUser.init();

//Zulassung
			} else if (args[0].equals("Zulassung")) {
				firstUser.permit();
				secondUser.permit();
				thirdUser.permit();

//VoteErsterBenutzer
			} else if (args[0].equals("VoteErsterBenutzer")) {
				vote(firstUser);

//VoteZweiterBenutzer
			} else if (args[0].equals("VoteZweiterBenutzer")) {
				vote(secondUser);

//VoteDritterBenutzer
			} else if (args[0].equals("VoteDritterBenutzer")) {
				vote(thirdUser);

//Read
			} else if (args[0].equals("Read")) {
				System.out.println("---------------------------------");
				System.out.println("Ausgabe der Blockliste des Miners");
				for (Block obj : miner.blockManager.list())
					System.out.println("obj = " + obj);

				System.out.println("---------------------------------");
				System.out.println("Ausgabe der Blockliste des firstUser");
				for (Block obj : firstUser.blockManager.list()) {
					System.out.println("\nDatum als Bytearray = " + obj);
				}

//Validate
			} else if (args[0].equals("Validate")) {
				// Validierung kann einfach ueberprueft werden, wenn Block in Miner manipuliert wird
				BlockValidator blockvalidator = miner.blockManager.validateBlockChain(miner.blockManager.list());
				System.out.println("blockvalidator = " + blockvalidator);

//WahlAuswerten
			} else if (args[0].equals("WahlAuswerten")) {
				HashMap<String, Integer> electionResult = new HashMap<String, Integer>();
				int votes = 0;

				for (Block obj : miner.blockManager.list()) {
					String res = miner.blockManager.getChoiceFromBlock(obj);
					electionResult.merge(res, 1, Integer::sum);
					votes += 1;
				}
				
				for (String party: electionResult.keySet()) {
				    System.out.println(party + " " + electionResult.get(party) + " (" + ((float)electionResult.get(party) / votes) * 100 + "%)");
				}

//PruefeWahlErsterBenutzer
			} else if (args[0].equals("PruefeWahlErsterBenutzer")) {
				InitBlockchainManager bcUser = new InitBlockchainManager(firstUser.persistanceUnit, firstUser.username, firstUser.password);
				byte[] encryptedUser = RSA.encrypt(firstUser.username.getBytes(), bcUser.getMyKeys().getPublickey(), INITBASE_USER);
				String choiceOfEncryptedUser = miner.blockManager.getChoiceOfEncryptedUser(encryptedUser);
				
				if (choiceOfEncryptedUser != "") {
					System.out.println(firstUser.username + " hat die Partei \""+ miner.blockManager.getChoiceOfEncryptedUser(encryptedUser) + "\" gewählt");
				} else {
					System.out.println(firstUser.username + " hat noch nicht gewählt");
				}
				
			} else {
				usage();
			}
		}
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
	
	public static void vote(UserManager user) {
		// ueberpruefe ob der User zur Wahl zugelassen ist
		try {
			if (user.isEligible()) {
				System.out.println(user.username + " ist zur Wahl zugelassen");
				
				// ueberpruefe ob User bereits ein Keypair erzeugt hat
				if (!user.hasPublicKey()) {
					System.out.println(user.username + " hat keinen Public Key, initialisiere user");
					user.init();
				} else {
					System.out.println(user.username + ": Public Key gefunden");
				}

				List<Block> myBlockList = miner.blockManager.getBlockListFromId(user.blockManager.getIdFromLastBlock());
				for (Block b : myBlockList) {
					System.out.println("block = " + b);
				}

				InitBlockchainManager bcUser = new InitBlockchainManager(user.persistanceUnit, user.username, user.password);
				byte[] encryptedUser = RSA.encrypt(user.username.getBytes(), bcUser.getMyKeys().getPublickey(), INITBASE_USER);
				
				String choiceOfEncryptedUser = miner.blockManager.getChoiceOfEncryptedUser(encryptedUser);
				if (choiceOfEncryptedUser != "") {
					System.out.println(user.username + " hat bereits " + choiceOfEncryptedUser + " gewaehlt. Wahl wurde nicht uebernommen");
				} else {
					// Generiere Block aus dem verschluesselten Usernamen und dessen Wahl
					byte[] blockData = generateBlockData(encryptedUser, user.choice);
					byte[] encryptedBlockData = RSA.encrypt(blockData, miner.getPublicKey(), INITBASE_MINER);
					
					// Erzeuge Block und weise ihm eine ID zu
					Block block = new Block(encryptedBlockData, 0);
					block.setId(miner.blockManager.calculateNextId());

					// Speichere den Blocks auf der DB des Miners
					miner.blockManager.append(block);
					user.blockManager.copyList(miner.blockManager.getBlockListFromId(user.blockManager.getIdFromLastBlock()));
				}
			} else {
				System.out.println("Fehler: " + user.username + " ist nicht zur Wahl zugelassen!");
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchRowException | InitializationAlreadyDoneException | SaveException | NoEntityFoundException | TargetListNotEmptyException e) {
			e.printStackTrace();
		}
	}
}
