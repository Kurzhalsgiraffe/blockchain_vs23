package dao;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import client.RSA;
import model.entity.MyBlockchainuserKeys;

public class InitBlockchainManagerMiner {
//	 "increment" muss identisch sein zu "allocationSize" in Entity, ansonsten
//	 werden identische Primaerschluesselwerte erzeugt!!

// 
// create table Block (
//    id integer primary key,              
//    dataAsobject blob,
//    previousHash varchar2(255) not null,
//    hash varchar2(255) not null, 
//    insertDate date not null,             
//    userId varchar2(255) not null,       
//    instanceId varchar2(32) not null,    
//    nonce integer not null,
//    hashAlgorithm varchar2(20) not null,
//    codeBase varchar2(20)not null,
//    prefix integer not null
// );
// 
	private final static String CREATE_STATEMENTS[] = { 
			"drop sequence block_seq", 
			"drop table Block",
			"create sequence block_seq start with 1 increment by 1 minvalue 1",
			"create table Block (" + " id integer primary key, " + " dataAsobject blob,"
					+ " previousHash varchar2(255) not null, " + " hash varchar2(255) not null,"
					+ " insertDate date not null, " + " userId varchar2(255) not null,"
					+ " instanceId varchar2(32) not null," + " nonce integer not null, "
					+ " hashAlgorithm varchar2(20) not null," + " codeBase varchar2(20) not null,"
					+ " prefix integer not null ) ",
			"drop table myblockchainuserkeys",
			"create table myblockchainuserkeys(" + " id integer primary key" + ", publickey blob " + ", privatekey blob)",
			"drop table blockchainuser",
			"create table blockchainuser(" + " id integer primary key, " + " userId varchar2(255) not null," + " password varchar2(255) not null," + "publickey blob)"};

	private EntityManager em = null;

	public InitBlockchainManagerMiner(String persistenceUnit, String userId, String password) {
		Map<String, String> addedOrOverridenProperties = new HashMap<String, String>();
		addedOrOverridenProperties.put("hibernate.connection.username", userId);
		addedOrOverridenProperties.put("hibernate.connection.password", password);

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, addedOrOverridenProperties);
		em = emf.createEntityManager();
	}

	public void initDatabase() throws InitializationAlreadyDoneException {
		EntityTransaction ta = em.getTransaction();
		ta.begin();
		Query query = null;
		for (int i = 0; i < CREATE_STATEMENTS.length; i++) {
			query = em.createNativeQuery(CREATE_STATEMENTS[i]);
			try {
				query.executeUpdate();
			} catch (Exception e) {
				continue;  // falls aktuelles Statement nicht ausf�hrbar, nehme n�chstes
			}
		}
		ta.commit();
	}
	public void initKeys() throws NoSuchRowException{
		MyBlockchainuserKeysDao myKeys = new MyBlockchainuserKeysDao(em);
		KeyPair keys = RSA.gen();
		myKeys.save(new MyBlockchainuserKeys(keys.getPublic(), keys.getPrivate()) );
	}

	public void close() {
		if (em != null)
			em.close();
	}
}
