package model.entity;

import java.math.BigDecimal;
import java.security.PublicKey;

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

	@Id
	@Column(name="id")
	private BigDecimal id;

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

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
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
