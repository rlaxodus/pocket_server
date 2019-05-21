package com.pocket_server.model;

import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="wallets")
public class Wallet {
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="wallet_id")
	private long wallet_id;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="owner_id")
	public User user;
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	private double balance;
	
	public long getWallet_id() {
		return wallet_id;
	}

	public void setWallet_id(long wallet_id) {
		this.wallet_id = wallet_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void decreaseBal(double amount) {
		this.balance -= amount;
	}
	
	public void increaseBal(double amount) {
		this.balance += amount;
	}

}
