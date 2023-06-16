package client;

import dao.BlockManager;
import dao.BlockchainuserDao;
import dao.InitBlockchainManager;
import dao.InitializationAlreadyDoneException;
import dao.NoSuchRowException;

public class User {
	public String persistanceUnit;
	public String username;
	public String password;
	public String choice;
	public BlockManager blockManager;
	
	public User(String persistanceUnit, String username, String password, String choice) {
		this.username = username;
		this.password = password;
		this.persistanceUnit = persistanceUnit;
		this.choice = choice;
		this.blockManager = new BlockManager(persistanceUnit, username, password);
	}
	
	public void permit() throws NoSuchRowException {
		BlockchainuserDao b = new BlockchainuserDao();
		b.save(this.username, this.password);
	}
	
	public boolean isEligible() throws NoSuchRowException {
		BlockchainuserDao b = new BlockchainuserDao();
		return b.userEligible(this.username, this.password);
	}
	
	public boolean hasPublicKey() throws NoSuchRowException {
		BlockchainuserDao b = new BlockchainuserDao();
		return b.userHasPublicKey(this.username);
	}
	
	public void init() throws NoSuchRowException, InitializationAlreadyDoneException {
		InitBlockchainManager initForUser = new InitBlockchainManager(this.persistanceUnit, this.username, this.password);
		initForUser.initDatabase();
		initForUser.initKeys(Main.INITBASE_USER);
	}
}
