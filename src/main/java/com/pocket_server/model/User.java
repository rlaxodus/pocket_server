package com.pocket_server.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="users")
public class User {
	@Id
	private String uuid;
	private String name;
	private String password;
	private String auth_code;
	private String phoneNumber;
	private String fcm_token;
	private String bio_token;
	private String session_token;
	private double daily_limit;
	private double daily_spent;
	private double per_transaction_limit;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="session_expiry")
	private Date session_expiry;
	
	public User(String uuid, String name, String password, String phoneNumber) {
		this.uuid = uuid;
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.daily_limit = 999;
		this.per_transaction_limit = 999;
	}

	public User(String name, String password, String phoneNumber) {
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.daily_limit = 999;
		this.per_transaction_limit = 999;
	}
	
	public double getDaily_spent() {
		return daily_spent;
	}

	public void setDaily_spent(double daily_spent) {
		this.daily_spent = daily_spent;
	}

	public double getDaily_limit() {
		return daily_limit;
	}

	public void setDaily_limit(double daily_limit) {
		this.daily_limit = daily_limit;
	}

	public double getPer_transaction_limit() {
		return per_transaction_limit;
	}

	public void setPer_transaction_limit(double limit) {
		this.per_transaction_limit = limit;
	}

	public String getSession_token() {
		return session_token;
	}

	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}

	public Date getSession_expiry() {
		return session_expiry;
	}

	public void setSession_expiry(Date session_expiry) {
		this.session_expiry = session_expiry;
	}

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "wallet_id")
	public Wallet wallet;
			
	
	public User() {
		
	}

	public String getUuid() {
		return uuid;
	}

	public String getBio_token() {
		return bio_token;
	}

	public void setBio_token(String bio_token) {
		this.bio_token = bio_token;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getFcm_token() {
		return fcm_token;
	}

	public void setFcm_token(String fcm_token) {
		this.fcm_token = fcm_token;
	}
	
	public void topUp(double amount) {
		this.wallet.increaseBal(amount);
	}
}
