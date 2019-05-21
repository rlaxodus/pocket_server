package com.pocket_server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
@Table(name="contracts")
public class Contract {
	
	@Id
	private String contractID;

	@Column(name="contract_status", columnDefinition = "TINYINT(1)")
	private Integer contractStatus;
	
	@Column(name = "contract_name")
	private String contractName;
	
	//Users information
	private String user1_id;
	private String user2_id;
	
	private String user1_phone;
	private String user2_phone;
	
	@Column(name="user1_ack", columnDefinition = "TINYINT(1)")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean user1_ack;
	
	@Column(name="user2_ack", columnDefinition = "TINYINT(1)")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean user2_ack;
	
	//Information
	@Column(name = "description")
	private String description;
	
	private Double amount;
	@Column(name = "frequency")
	private Integer frequency;
	@Column(name = "penalty_amount")
	private Double penaltyAmount;
	
	//When contract was first created
	@Temporal(TemporalType.DATE)
	@Column(name="created_date")
	private Date createdDate;
	
	//Contract start/end date
	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="end_date")
	private Date endDate;

	
	public Contract(String contractID, int contractStatus, String contractName, String user1_id, String user2_id, String user1_phone, String user2_phone, String description,
			double amount, int frequency, double penaltyAmount, Date startDate, Date endDate) {
		this.contractID = contractID;
		this.contractStatus = 0;
		this.contractName = contractName;
		this.user1_id = user1_id;
		this.user2_id = user2_id;
		this.user1_ack = true;
		this.user2_ack = false;
		this.description = description;
		this.amount = amount;
		this.frequency = frequency;
		this.penaltyAmount = penaltyAmount;
		this.createdDate = new Date();
		this.startDate = startDate;
		this.endDate = endDate;
		this.user1_phone = user1_phone;
		this.user2_phone = user2_phone;
	}
	
	public Contract() {
		
	}
	
	public String getContractID() {
		return contractID;
	}

	public void setContractID(String contractID) {
		this.contractID = contractID;
	}

	public Integer getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(Integer contractStatus) {
		this.contractStatus = contractStatus;
	}
	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getUser1_phone() {
		return user1_phone;
	}

	public void setUser1_phone(String user1_phone) {
		this.user1_phone = user1_phone;
	}

	public String getUser2_phone() {
		return user2_phone;
	}

	public void setUser2_phone(String user2_phone) {
		this.user2_phone = user2_phone;
	}

	public String getUser1_id() {
		return user1_id;
	}

	public void setUser1_id(String user1_id) {
		this.user1_id = user1_id;
	}

	public String getUser2_id() {
		return user2_id;
	}

	public void setUser2_id(String user2_id) {
		this.user2_id = user2_id;
	}

	public Boolean getUser1_ack() {
		return user1_ack;
	}

	public void setUser1_ack(Boolean user1_ack) {
		this.user1_ack = user1_ack;
	}

	public Boolean getUser2_ack() {
		return user2_ack;
	}

	public void setUser2_ack(Boolean user2_ack) {
		this.user2_ack = user2_ack;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
