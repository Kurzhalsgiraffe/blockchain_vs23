package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import model.entity.Blockchainuser;
import model.entity.MyBlockchainuserKeys;

public class MyBlockchainuserKeysDao {
private EntityManager em;
	
	public MyBlockchainuserKeysDao() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("BlockchainMiner");
		em = emf.createEntityManager();
	}
	public MyBlockchainuserKeysDao(EntityManager em) {
		this.em = em;
	}
	
	public MyBlockchainuserKeys getMyKeys() throws NoSuchRowException{
		//zu erzeugendes Statement
		// select * from myblockchainuserkeys where publickey != null;
		MyBlockchainuserKeys keys = em.find(MyBlockchainuserKeys.class, 1);
		if (keys == null) {
			throw new NoSuchRowException();
		}
		return keys;
	}
	
	public void save(MyBlockchainuserKeys arg) {
		EntityTransaction ta = em.getTransaction();
		ta.begin();
		MyBlockchainuserKeys keys = em.find(MyBlockchainuserKeys.class, arg.getId());
		if (keys == null) {
			em.persist(arg); // insert
		}
		else {
			em.merge(arg); // update
		}
		ta.commit();
	}
	public void close() {
		// entspricht (im weitesten Sinne) conn.close()
		if (em != null)
			em.close();
	}
}
