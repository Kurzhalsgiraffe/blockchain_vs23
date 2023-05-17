package dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

public class InitBlockchainManager {
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
			"create table Block (" + " id integer primary key, " + " dataAsobject blob,"
					+ " previousHash varchar2(255) not null, " + " hash varchar2(255) not null,"
					+ " insertDate date not null, " + " userId varchar2(255) not null,"
					+ " instanceId varchar2(32) not null," + " nonce integer not null, "
					+ " hashAlgorithm varchar2(20) not null," + " codeBase varchar2(20) not null,"
					+ " prefix integer not null ) " };

	private EntityManager em = null;

	public InitBlockchainManager(String persistenceUnit, String userId, String password) {
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

	public void close() {
		if (em != null)
			em.close();
	}
}
