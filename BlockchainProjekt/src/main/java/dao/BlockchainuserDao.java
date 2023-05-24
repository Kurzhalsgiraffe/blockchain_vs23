package dao;

import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import model.entity.Blockchainuser;


public class BlockchainuserDao {
	
	private EntityManager em;
	
	public BlockchainuserDao() {
		Map<String, String> addedOrOverridenProperties = new HashMap<String, String>();
		addedOrOverridenProperties.put("hibernate.connection.username", "minerProjektVS_SS23");
		addedOrOverridenProperties.put("hibernate.connection.password", "minerProjektVS_SS23");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("BlockchainMiner", addedOrOverridenProperties);
		em = emf.createEntityManager();
	}
	public BlockchainuserDao(EntityManager em) {
		this.em = em;
	}
	
	public Blockchainuser getById(int primaryKey) throws NoSuchRowException{
		//zu erzeugendes Statement
		// select * from blockchainuser where username = ? (Prepared Statement)
		Blockchainuser user = em.find(Blockchainuser.class, primaryKey);
		if (user == null) {
			throw new NoSuchRowException();
		}
		return user;
	}
	public Collection<Blockchainuser> list() {
		return em.createQuery("SELECT obj FROM Blockchainuser obj", Blockchainuser.class).getResultList();
	}
	
	public void save(Blockchainuser arg) throws NoSuchRowException {
		EntityTransaction ta = em.getTransaction();
		ta.begin();
		// select * from blockchainuser where userid = ? 
		Collection<Blockchainuser> users = list();
		for(Blockchainuser user: users) {
			if (user.getUsername().equals(arg.getUsername())) {
				em.merge(arg); // update
				ta.commit();
				return;
			}
		}
		System.out.println("Sie sind nicht wahlberechtigt");
		
		ta.commit();
	}
	
	public void close() {
		// entspricht (im weitesten Sinne) conn.close()
		if (em != null)
			em.close();
	}
	
	public boolean userExists(String username, String password) {
		Collection<Blockchainuser> users = list();
		for(Blockchainuser user: users) {
			if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}
	
	public InitBlockchainManager getUser(String persistanceUnit, String username, String password) {
		InitBlockchainManager manager = new InitBlockchainManager(persistanceUnit, username, password);
		return manager;
	}
	

}
