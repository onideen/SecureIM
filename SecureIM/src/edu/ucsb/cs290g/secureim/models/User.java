package edu.ucsb.cs290g.secureim.models;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class User {

	private String username;
	private KeyPair keypair;
	
	public User (String username, KeyPair keypair){
		this.username = username;
		this.keypair= keypair;
	}

	public User(String username) {
		this.username = username;
	}
	

	public String getUsername() {
		return username;
	}

	public PublicKey getPublickey() {
		return keypair.getPublic();
	}
	public PrivateKey getPrivateKey(){
		return keypair.getPrivate();
	}
	
	public void setKeyPair(KeyPair keypair) {
		this.keypair= keypair;
	}
}
