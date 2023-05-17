package model.entity;

import java.security.*;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//create table myblockchainuserkeys (
//id    	smallint primary key,
//publickey blob,
//privatekey blob
//);

@Entity
@Table(name="myblockchainuserkeys")
public class MyBlockchainuserKeys {
	@Id
	@Column(name = "id")
	private int id = 1;
	
	@Column(name= "publickey")
	private PublicKey publickey;
	
	@Column(name= "privatekey")
	private PrivateKey privatekey;
	
	public MyBlockchainuserKeys() {}
	public MyBlockchainuserKeys(PublicKey publickey, PrivateKey privatekey) {
		this.id = 1;
		this.privatekey = privatekey;
		this.publickey = publickey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PublicKey getPublickey() {
		return publickey;
	}

	public void setPublickey(PublicKey publickey) {
		this.publickey = publickey;
	}

	public PrivateKey getPrivatekey() {
		return privatekey;
	}

	public void setPrivatekey(PrivateKey privatekey) {
		this.privatekey = privatekey;
	}

	
	
	


}
