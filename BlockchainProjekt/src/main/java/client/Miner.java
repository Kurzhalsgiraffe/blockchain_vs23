package client;

import java.security.KeyPair;
import java.security.PublicKey;

import dao.BlockManager;
import dao.InitBlockchainManagerMiner;
import dao.InitializationAlreadyDoneException;
import dao.MyBlockchainuserKeysDao;
import dao.NoSuchRowException;

public class Miner {
	public String persistanceUnit;
	public String username;
	public String password;
	public BlockManager blockManager;
	public KeyPair keypair;
	
	public Miner(String persistanceUnit, String username, String password) {
		this.username = username;
		this.password = password;
		this.persistanceUnit = persistanceUnit;
		this.blockManager = new BlockManager(persistanceUnit, username, password);
	}
	
	public void init() throws NoSuchRowException, InitializationAlreadyDoneException {
		InitBlockchainManagerMiner initForMiner = new InitBlockchainManagerMiner("BlockchainMiner", this.username, this.password);
		initForMiner.initDatabase(); // initialisiere Datenbank fuer Miner
		initForMiner.initKeys(Main.INITBASE_MINER);
	}
	
	public PublicKey getPublicKey() throws NoSuchRowException {
		MyBlockchainuserKeysDao bcuK = new MyBlockchainuserKeysDao();
		PublicKey publicKeyMiner = bcuK.getMyKeys().getPublickey();
		return publicKeyMiner;
	}
}
