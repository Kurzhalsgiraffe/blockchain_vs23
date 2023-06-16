package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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

	private final static Miner miner = new Miner("BlockchainMiner", "minerProjektVS_SS23", "minerProjektVS_SS23");
	private final static User firstUser = new User("FirstUser", "hantscma", "hantscma", "Gruene");
	private final static User secondUser = new User("SecondUser", "rothnina", "rothnina", "FDP");
	private final static User thirdUser = new User("ThirdUser", "heinzelu", "heinzelu", "CDU");

	public static void usage() {
		System.out.println("Usage: java Main EinrichtenMiner");			// Schritt 0
		System.out.println("Usage: java Main ZulassungUser");			// Schritt 1
		System.out.println("Usage: java Main LoginErsterBenutzer");		// Schritt 2
		System.out.println("Usage: java Main VoteZweiterBenutzer");	    // Schritt 3
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
			miner.init();

//ZulassungUser
			} else if (args[0].equals("ZulassungUser")) {
				firstUser.permit();
				secondUser.permit();
				thirdUser.permit();

//VoteErsterBenutzer
			} else if (args[0].equals("VoteErsterBenutzer")) {
				vote(firstUser);

//VoteZweiterBenutzer
			} else if (args[0].equals("VoteZweiterBenutzer")) {
				vote(secondUser);

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

//EvaluateElection
			} else if (args[0].equals("EvaluateElection")) {
				HashMap<String, Integer> electionResult = new HashMap<String, Integer>();

				for (Block obj : miner.blockManager.list()) {
					String res = miner.blockManager.getSelectedPartyFromBlock(obj);
					electionResult.merge(res, 1, Integer::sum);
				}
				for (String party: electionResult.keySet()) {
				    System.out.println(party.toString() + " " + electionResult.get(party).toString());
				}

//PruefeAbgegebeneWahl ErsterBenutzer
			} else if (args[0].equals("PruefeAbgegebeneWahl ErsterBenutzer")) {
				for (Block obj : miner.blockManager.list()) {
					// lies private Key von Erstem Benutzer
					// lies private Key von Miner
					// durchsuche Block  und dechiffriere mit sk von Miner
					// suche nach User-LKennung des betreffenden Waehlers ...
					// Ergebnis:
					// 1. 1, falls Waehler Stimme abgegeben hat
					// 2. 0, falls Waehler nicht gewaehlt hat
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
	
	public static void vote(User user) {
		// ueberpruefe ob der User zur Wahl zugelassen ist
		try {
			if (user.isEligible()) {
				System.out.println(user.username + " ist zur Wahl zugelassen");
				
				// ueberpruefe ob User bereits ein Keypair erzeugt hat
				if (!user.hasPublicKey()) {
					System.out.println(user.username + " hat keinen Public Key, initialisiere user");
					user.init();
				}

				List<Block> myBlockList = miner.blockManager.getBlockListFromId(user.blockManager.getIdFromLastBlock());
				for (Block b : myBlockList) {
					System.out.println("block = " + b);
				}

				InitBlockchainManager bc2User = new InitBlockchainManager(user.persistanceUnit, user.username, user.password);
				byte[] encryptedSecondUser = RSA.encrypt(user.username.getBytes(), bc2User.getMyKeys().getPublickey(), INITBASE_USER);

				if (miner.blockManager.checkIfUserHasVoted(encryptedSecondUser)) {
					System.out.println(user.username + " hat schon gewaehlt. Wahl wurde nicht uebernommen");
				} else {
					// Generiere Block aus dem verschluesselten Usernamen und dessen Wahl
					byte[] blockData = generateBlockData(encryptedSecondUser, user.choice);
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
