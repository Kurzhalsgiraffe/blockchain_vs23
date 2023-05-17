package model.entity;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//create table blockchainuser (
//id    	smallint primary key,
//userid    VARCHAR2(50 BYTE),
//password  VARCHAR2(50 BYTE),
//publickey blob
//);

@Entity
@Table(name="blockchainuser")
public class Blockchainuser {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "userid")
	private String userid;

	@Column(name = "password")
	private String password;
	
	@Column(name= "publickey")
	private PublicKey publickey;
	
	public Blockchainuser() {};
	
	public Blockchainuser(String username, String password) {
		this.userid = username;
		this.password = password;
	}
	public PublicKey getPublicKey() {
		return publickey;
	}
	public void setPublicKey(PublicKey publicKey) {
		this.publickey = publicKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return userid;
	}

	public void setUsername(String username) {
		this.userid = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
