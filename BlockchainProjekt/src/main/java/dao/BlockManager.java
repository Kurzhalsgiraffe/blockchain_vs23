package dao;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import client.RSA;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;



import model.block.Block;
import model.entity.MyBlockchainuserKeys;

public class BlockManager {
	private EntityManager em = null;

	public String getSelectedPartyFromBlock(Block block) throws NoSuchRowException {
		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = keysDao.getMyKeys();

		byte[] decryptedText = RSA.decrypt(block.getDataAsObject(), keys.getPrivatekey());
		
		int encryptedUsernameLength = decryptedText[0];
		int choiceLength = decryptedText[1];
		if (encryptedUsernameLength < 0) {
			encryptedUsernameLength += 255;
		}
		if (choiceLength < 0) {
			choiceLength += 255;
		}
		return new String(Arrays.copyOfRange(decryptedText, 3+encryptedUsernameLength, choiceLength + encryptedUsernameLength + 3));
	}

	public BigDecimal calculateNextId() {
		return (BigDecimal) getEntityManager().createNativeQuery("select block_seq.nextval from dual").getSingleResult();
	}

	public String getInstanceId() {
		return (String) getEntityManager().createNativeQuery("select value from v$parameter where upper(name) = 'INSTANCE_NAME'").getSingleResult();
	}

	public String getUserId() {
		return (String) getEntityManager().createNativeQuery("select user from dual").getSingleResult();
	}

	public Date getInsertDate() {
		return (Date) getEntityManager().createNativeQuery("select sysdate from dual").getSingleResult();
	}

	public BlockManager(String persistenceUnit, String userId, String password) {
		Map<String, String> addedOrOverridenProperties = new HashMap<String, String>();
		addedOrOverridenProperties.put("hibernate.connection.username", userId);
		addedOrOverridenProperties.put("hibernate.connection.password", password);

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, addedOrOverridenProperties);
		em = emf.createEntityManager();
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	public int getListSize() {
		return list().size();
	}

	public List<Block> list() {
		em.clear(); // Um zu vermeiden, dass Programm bereits zuvor eingelesene Liste (mit alten Referenzen) nutzt
		return em.createQuery("SELECT obj from Block obj ORDER BY obj.id asc", Block.class).getResultList();
	}

	public Block findByPrimaryKey(long primaryKey) throws NoSuchRowException {
		Block obj = em.find(Block.class, primaryKey);
		if (obj == null)
			throw new NoSuchRowException();
		else
			return obj;
	}

	public void copyList(List<Block> fromList) throws TargetListNotEmptyException, SaveException, NoEntityFoundException {
		if (fromList.size() == 0) {
			return;
		}
		for (Block blockObj : fromList) {
			Block newBlock = blockObj.copy();
			append(newBlock);
		}
	}

	public BigDecimal getIdFromFirstBlock() {
		return (BigDecimal) em.createQuery("SELECT coalesce(min(x.id), 0) FROM Block x").getSingleResult();
	}

	public BigDecimal getIdFromLastBlock() {
		return (BigDecimal) em.createQuery("SELECT coalesce(max(x.id), 0) FROM Block x").getSingleResult();
	}

	public String getHashFromLastBlock() {
		try {
			return (String) em
					.createQuery("SELECT obj.hash FROM Block obj WHERE obj.id IN (SELECT max(x.id) FROM Block x)").getSingleResult();
		} catch (NoResultException e) {
			return "0";
		}
	}

	public List<Block> getBlockListFromPreviousHash(String previousHash) {
		return em.createQuery("SELECT obj FROM Block obj "
				+ " WHERE obj.id >= ( SELECT x.id FROM Block x WHERE x.previousHash = ?1 ) ORDER BY obj.insertDate asc",
				Block.class).setParameter(1, previousHash).getResultList();
	}

	// Fuegt Block am Ende der Kette an. Falls Kette leer ist wird "previousHash" (einmalig) auf "0" gesetzt
	public void append(Block arg) throws SaveException, NoEntityFoundException {
		int size = getListSize();
		String lastHash = size > 0 ? getHashFromLastBlock() : "0";
		arg.setPreviousHash(lastHash);
		save(arg);
	}

	private void save(Block arg) throws SaveException {
		if (arg == null)
			throw new SaveException();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// ... mit simpler Datenkodierung (zur Demonstration)
		arg.setUserId(getUserId());
		arg.setInstanceId(getInstanceId());
		arg.setInsertDate(getInsertDate());
		em.persist(arg);
		try {
			tx.commit();
		} catch (javax.persistence.RollbackException e) {
			throw new SaveException();
		}
	}

	public List<Block> getBlockListFromId(BigDecimal id) {
		return em.createQuery("SELECT obj FROM Block obj WHERE obj.id > ?1 ORDER BY id asc", Block.class)
				.setParameter(1, id).getResultList();
	}

	public List<Block> getLastBlock() {
		return em.createQuery("SELECT obj FROM Block obj "
				+ " WHERE obj.id > ALL ( SELECT x.id FROM Block x WHERE x.hash = ?1 ) ORDER BY obj.insertDate asc",
				Block.class).getResultList();
	}

	public List<Block> getBlockListFromHash(String hash) {
		return em.createQuery("SELECT obj FROM Block obj WHERE obj.hash = ?1", Block.class).setParameter(1, hash)
				.getResultList();
	}

	public BlockValidator validateBlockChain(List<Block> blockList) {
		boolean flag = true;
		int i = 0;
		Block testBlock;
		for (i = 0; i < blockList.size(); i++) {
			String previousHash = i == 0 ? "0" : blockList.get(i - 1).getHash();
			try {
				testBlock = new Block(blockList.get(i).getDataAsObject(), blockList.get(i).getPrefix());
				flag = blockList.get(i).getHash().equals(testBlock.getHash())
						&& previousHash.equals(blockList.get(i).getPreviousHash());
				if (!flag)
					break;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!flag)
				break;
		}
		return new BlockValidator(flag, i);
	}

	public void close() {
		if (em != null)
			em.close();
	}
	
	public boolean checkIfUserHasVoted(byte[] encryptedUser) {
		MyBlockchainuserKeysDao keysDao = new MyBlockchainuserKeysDao();
		MyBlockchainuserKeys keys = null;
		try {
			keys = keysDao.getMyKeys();
		} catch (NoSuchRowException e) {
			e.printStackTrace();
		}

		List<Block> blockList = list();
		for (Block block : blockList){
			byte[] decryptedText = RSA.decrypt(block.getDataAsObject(), keys.getPrivatekey());
			
			int encryptedUsernameLength = decryptedText[0];
			if (encryptedUsernameLength < 0) {
				encryptedUsernameLength += 255;
			}

			byte[] encryptedUsername = Arrays.copyOfRange(decryptedText, 2, encryptedUsernameLength + 3);


			if(Arrays.equals(encryptedUser, encryptedUsername)) {
				return true;
			}
		}
		return false;
	}
}
