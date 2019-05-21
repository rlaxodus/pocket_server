package com.pocket_server.model;

import java.security.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="transactions")
public class Transaction implements Comparable<Transaction>{
	
	@Id
	private String transactionID;
	
	@Column(name="type")
	private String type;
	
	@Column(name="origin")
	private String origin;
	
	@Column(name="destination")
	private String destination;
	
	@Column(name="amount")
	private double amount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="timestamp")
	private Date timestamp;
	
	public Transaction() {
	}

	
	public Transaction(String transactionRefID, String type, String senderID, String receiverID, double amount2, Date date) {
		// TODO Auto-generated constructor stub
		this.transactionID = transactionRefID;
		this.type = type;
		this.origin = senderID;
		this.destination = receiverID;
		this.amount = amount2;
		this.timestamp = date;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFrom() {
		return origin;
	}
	public void setFrom(String from) {
		this.origin = from;
	}
	public String getTo() {
		return destination;
	}
	public void setTo(String to) {
		this.destination = to;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	 @Override
	 public int compareTo(Transaction o) {
	   return getTimestamp().compareTo(o.getTimestamp());
	 }
	 
	 
}
