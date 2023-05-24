package dao;

import java.math.BigDecimal;
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
	public void save(String username, String password) throws NoSuchRowException {
		EntityTransaction ta = em.getTransaction();
		ta.begin();
		// select * from blockchainuser where userid = ? 
		Collection<Blockchainuser> users = list();
		for(Blockchainuser user: users) {
			if (user.getUsername().equals(username)) {
				System.out.println("User already Exists");
				return;
			}
		}
		BigDecimal nextId = getIdFromLastBlock();
		Blockchainuser bcUser = new Blockchainuser(username, password);
		bcUser.setId(nextId.add(new BigDecimal(1)));
		em.persist(bcUser);
		ta.commit();
	}
	
	public void close() {
		// entspricht (im weitesten Sinne) conn.close()
		if (em != null)
			em.close();
	}
	
	public boolean userEligible(String username, String password) {
		Collection<Blockchainuser> users = list();
		for(Blockchainuser user: users) {
			if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean userHasPublicKey(String username) {
		Collection<Blockchainuser> users = list();
		for(Blockchainuser user: users) {
			if (user.getUsername().equals(username) && user.getPublicKey() != null) {
				return true;
			}
		}
		return false;
	}
	
	public BigDecimal getIdFromLastBlock() {
		return (BigDecimal) em.createQuery("SELECT coalesce(max(x.id), 0) FROM Blockchainuser x").getSingleResult();
	}
}
