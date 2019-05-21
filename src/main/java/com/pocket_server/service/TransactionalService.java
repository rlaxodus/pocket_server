package com.pocket_server.service;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pocket_server.model.Contract;
import com.pocket_server.model.Transaction;
import com.pocket_server.model.User;
import com.pocket_server.repository.ContractRepository;
import com.pocket_server.repository.TransactionRepository;
import com.pocket_server.repository.UserRepository;

@Service
public class TransactionalService {
	
	private SimpleDateFormat idDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	private SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private int count;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ContractRepository contractRepository;
	
	@Autowired
	private PushNotificationService pushNotificationService = new PushNotificationService();
	
	public TransactionalService () {
		idDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
	}
	
	public Map<String,Object> processPayment(Map<String, Object> obj) throws ParseException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		String senderID = obj.get("payee_id").toString();
		String receiverID = obj.get("merchant_id").toString();
		double amount = Double.parseDouble(obj.get("amount").toString());
		String transactionRefID;

		if (userRepository.existsById(senderID) && userRepository.existsById(receiverID)) {
			User user = userRepository.getOne(senderID);
			User merchant = userRepository.getOne(receiverID);
			
			if ((user.getWallet().getBalance() >= amount) && (amount <= user.getPer_transaction_limit())) {
				
				double totalSpent = user.getDaily_spent() + amount;
				
				if (totalSpent < user.getDaily_limit()) {
					
					user.getWallet().decreaseBal(amount);
					merchant.getWallet().increaseBal(amount);
					userRepository.saveAndFlush(user);
					userRepository.saveAndFlush(merchant);
					
					//Transaction Record
					transactionRefID = "P" + idDateFormat.format(new Date()) + count++;
					Date date = localDateFormat.parse(localDateFormat.format(new Date()));
					Transaction newTransaction = new Transaction(transactionRefID, "payment", senderID, receiverID, amount, date);
					transactionRepository.saveAndFlush(newTransaction);
					
					//Notification
					try {
						pushNotificationService.send(user.getFcm_token(), "Payment", "Paid $" + amount + " to " + merchant.getName());
						pushNotificationService.send(merchant.getFcm_token(), "Payment", "Received $" + amount + " from " + user.getName());
					} catch (Exception e) {}	
					
					//Response
					response.put("result", "success");
					response.put("transaction_id" , transactionRefID);
					return response;
					
				} else {
					response.put("message", "daily limit exceeded");
				}
				
			} else {
				response.put("message", "insufficient balance or per limit exceeded");
			}
		} else {
			response.put("message", "user/merchant do not exist");
		}
		
		response.put("result", "failed");
		return response;
	}
	
	public Map<String,Object> processQuickPay(Map<String, Object> obj) throws NoSuchAlgorithmException, ParseException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		String senderID = obj.get("payee_id").toString();
		User user = userRepository.getOne(senderID);
		String authCode = obj.get("auth_code").toString();
		
		//Check payer's auth code
		if (authCode.equals(user.getAuth_code())) {
			String receiverID = obj.get("merchant_id").toString();
			double amount = Double.parseDouble(obj.get("amount").toString());
			
			//Check if both users exist
			if (userRepository.existsById(senderID) && userRepository.existsById(receiverID)) {
				
				User merchant = userRepository.getOne(receiverID);
				
				//Check payer has enough money
				if ((user.getWallet().getBalance() >= amount) && (amount <= user.getPer_transaction_limit())) {
					
					double totalSpent = user.getDaily_spent() + amount;
					
					if (totalSpent < user.getDaily_limit()) {
						
						//Update both wallet
						user.getWallet().decreaseBal(amount);
						merchant.getWallet().increaseBal(amount);
						user.setAuth_code("EXPIRED");
						user.setDaily_spent(totalSpent);
						userRepository.saveAndFlush(user);
						userRepository.saveAndFlush(merchant);
						
						//Create and save transaction
						String transactionRefID = "Q" + idDateFormat.format(new Date()) + count++;
						Date date = localDateFormat.parse(localDateFormat.format(new Date()));
						Transaction newTransaction = new Transaction(transactionRefID, "QuickPay", senderID , receiverID, amount, date);
						transactionRepository.saveAndFlush(newTransaction);
						
						//Send notification to payer
						try {
								pushNotificationService.send(user.getFcm_token(), "Payment", "Paid $" + amount + " to " + merchant.getName());
								pushNotificationService.send(merchant.getFcm_token(), "Payment", "Received $" + amount + " from " + user.getName());
						} catch (Exception e) {}					
					
						//Response
						response.put("result", "success");
						response.put("transaction_id" , transactionRefID);
	
						return response;
						
					} else {
						response.put("message", "daily limit exceeded");
					}
				} else {
					response.put("message", "insufficient balance or per limit exceeded");
				}
			} 			
		} else if (!authCode.equals(user.getAuth_code())) {
			
			response.put("message", "invalid auth code");
			
		}

		response.put("result", "failed");
		
		return response;
	}
	
	public Map<String,Object> processPaymentPhone(Map<String, Object> obj) throws ParseException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		String senderID = obj.get("payee_id").toString();
		String receiverPhone = obj.get("merchant_phone").toString();
		double amount = Double.parseDouble(obj.get("amount").toString());
		String transactionRefID;

		if (userRepository.existsById(senderID) && userRepository.existsByphoneNumber(receiverPhone)) {
			
			User user = userRepository.getOne(senderID);
			User merchant = userRepository.getByphoneNumber(receiverPhone);
			
			if ((user.getWallet().getBalance() >= amount) && (amount <= user.getPer_transaction_limit())) {
				
				double totalSpent = user.getDaily_spent() + amount;
				
				if (totalSpent < user.getDaily_limit()) {
				
					user.getWallet().decreaseBal(amount);
					merchant.getWallet().increaseBal(amount);
					userRepository.saveAndFlush(user);
					userRepository.saveAndFlush(merchant);
					
					//Transaction Record
					transactionRefID = "P" + idDateFormat.format(new Date()) + count++;
					Date date = localDateFormat.parse(localDateFormat.format(new Date()));
					Transaction newTransaction = new Transaction(transactionRefID, "payment_phone", user.getUuid(), merchant.getUuid(), amount, date);
					transactionRepository.saveAndFlush(newTransaction);
					
					//Notification
					try {
						pushNotificationService.send(user.getFcm_token(), "Payment", "Paid $" + amount + " to " + merchant.getName());
						pushNotificationService.send(merchant.getFcm_token(), "Payment", "Received $" + amount + " from " + user.getName());
					} catch (Exception e) {}	
					
					//Response
					response.put("result", "success");
					response.put("transaction_id" , transactionRefID);
					return response;
				} else {
					response.put("message", "daily limit exceeded");
				}
			} else {
				response.put("message", "insufficient balance or per limit exceeded");
			}
		} else {
			response.put("message", "user/merchant do not exist");
		}
		
		response.put("result", "failed");
		return response;
	}
	
	//Transaction History
	public List<Transaction> retrieveAllTransactionByUserId(String user_id) throws ParseException {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		
		transactionList.addAll(transactionRepository.findAllByorigin(user_id));
		transactionList.addAll(transactionRepository.findAllBydestination(user_id));
		Collections.sort(transactionList, Collections.reverseOrder());
		transactionList = processTransactionDetails(transactionList, user_id);
		
		return transactionList;
	}
	
	
	public Transaction retrieveTransactionByTransactionId(String transaction_id) {
		Transaction t = transactionRepository.findById(transaction_id).get();
		return t;
	}
	
	public List<Transaction> processTransactionDetails(List<Transaction> transactionList, String user_id) throws ParseException {
		
		for (Transaction t: transactionList) {
			if (t.getType().compareTo("topup") == 0) {
				t.setFrom("Top Up");
				t.setTo("-");
			} else {
				if (t.getFrom().compareTo(user_id) == 0) {
					String name = userRepository.getOne(t.getTo()).getName();
					t.setTo(name);
					t.setFrom("-");
				} else if (t.getTo().compareTo(user_id) == 0) {
					String name = userRepository.getOne(t.getFrom()).getName();
					t.setFrom(name);
					t.setTo("-");
				}
			}
			
		}
			
		return transactionList;
	}
	
	//Contract
	public String createContract(Map<String, Object> obj) throws ParseException {
		
		String contractName = obj.get("contractName").toString();
		String user1_id = obj.get("user1_id").toString();
		String user2_phone = obj.get("user2_phone").toString();
		String description = obj.get("description").toString();
		double amount = Double.parseDouble(obj.get("amount").toString());
		int frequency = Integer.parseInt(obj.get("frequency").toString());
		double penaltyAmount = Double.parseDouble(obj.get("penaltyAmount").toString());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = dateFormat.parse(obj.get("startDate").toString());
		Date endDate = dateFormat.parse(obj.get("endDate").toString());
		
		User user = userRepository.getOne(user1_id);
		User user2 = userRepository.getByphoneNumber(user2_phone);
	
		String contractID = "C" + Character.toUpperCase(user.getName().charAt(0)) + Character.toUpperCase(user.getName().charAt(0)) + localDateFormat.format(new Date()) + count++;		
		
		Contract c = new Contract (contractID, 0, contractName,  user1_id , user2.getUuid(), user.getPhoneNumber(), user2.getPhoneNumber(), description, amount, frequency, penaltyAmount, startDate, endDate);
		contractRepository.saveAndFlush(c);
		
		//send notif to 2nd user
		pushNotificationService.send(user2.getFcm_token(), "New Contract", user.getName() + " created a new contract with you.\nAcknowledge now to start contract");
		
		return contractID;
	}
	
	public String acknowledgeContract(Map<String, Object> obj) {
		boolean exist = false;
		
		try {
			exist = contractRepository.existsById(obj.get("contract_id").toString());
			
			if (exist) {
				Contract c = contractRepository.getOne(obj.get("contract_id").toString());
				User user = userRepository.getOne(c.getUser1_id());
				User user2 = userRepository.getOne(c.getUser2_id());
				int decision = 0;
				
				//DECISION 0 = decline , 1 = accept
				if (c.getUser2_id().compareTo(obj.get("user_id").toString()) == 0) {
					decision = Integer.parseInt(obj.get("decision").toString());
					
					if (decision == 1) {
						c.setUser2_ack(true);
						c.setContractStatus(1);
						pushNotificationService.send(user.getFcm_token(), "Contract Accepted", user2.getName() + " has accepted your contract (" +c.getContractID()+")");
					} else if (decision == 0) {
						//0 = pending , 1 = accepted , 2 = active , 3 = declined, 4 = terminated
						c.setUser2_ack(true);
						c.setContractStatus(3);
						pushNotificationService.send(user.getFcm_token(), "Contract Declined", user2.getName() + " has declined your contract (" +c.getContractID()+")");
					}
				}
				
				if (c.getUser1_ack() && c.getUser2_ack()) {
					int status = c.getContractStatus();
					
					if (status == 1) {
						pushNotificationService.send(user.getFcm_token(), "Contract", "Contract (" + c.getContractID() + ")" + " with " + user2.getName() + " will start on " + c.getStartDate());
						pushNotificationService.send(user2.getFcm_token(), "Contract", "Contract (" + c.getContractID() + ")" + " with " + user.getName() + " will start on " + c.getStartDate());
					} else if (status == 3) {
					
					}
				}
				
				contractRepository.saveAndFlush(c);
				return String.valueOf(c.getContractStatus());
			} else if (!exist) {
				return "DOES_NOT_EXIST";
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "failed";
	}
	
	public String terminateContract(Map<String, Object> obj) {
		boolean exist = contractRepository.existsById(obj.get("contract_id").toString());
		
		if (exist) {
			Contract c = contractRepository.getOne(obj.get("contract_id").toString());
			User user = userRepository.getOne(c.getUser1_id());
			User user2 = userRepository.getOne(c.getUser2_id());
			
			c.setContractStatus(4);
			
			if (c.getUser1_id().compareTo(obj.get("user_id").toString()) == 0) {
				//if terminator is user 1
				pushNotificationService.send(user2.getFcm_token(), "Contract Terminated", user.getName() + " has terminated the contract (" +c.getContractID()+")");
			} else if (c.getUser2_id().compareTo(obj.get("user_id").toString()) == 0) {
				//if terminator is user 2
				pushNotificationService.send(user.getFcm_token(), "Contract Terminated", user2.getName() + " has terminated the contract (" +c.getContractID()+")");
			}
			
			contractRepository.saveAndFlush(c);
			return String.valueOf(c.getContractStatus());
		} else if (!exist) {
			return "DOES_NOT_EXIST";
		}
		
		return "failed";
	}
	public List<Contract> retrieveAllContractByUserId(String user_id) {
		List<Contract> contractList = new ArrayList<Contract>();
		
		contractList.addAll(contractRepository.findAllByuser1_id(user_id));
		contractList.addAll(contractRepository.findAllByuser2_id(user_id));
		contractList = processNameContract(contractList, user_id);
		return contractList;
	}
	
	
	public Contract retrieveTransactionByContractId(String contract_id) {
		Contract t = contractRepository.findById(contract_id).get();
		return t;
	}
	
	public List<Contract> processNameContract(List<Contract> contractList, String user_id) {

		for (Contract c: contractList) {
				
			if (c.getUser1_id().compareTo(user_id) == 0) {
				String otherId = c.getUser2_id();
				String name = userRepository.getOne(otherId).getName();
				c.setUser2_id(name);
			} else if (c.getUser2_id().compareTo(user_id) == 0) {
				String otherId = c.getUser1_id();
				String name = userRepository.getOne(otherId).getName();
				c.setUser1_id(name);
			}
				
		}
			
		return contractList;
	}
	
	public Map<String,Object> processTopUp(Map<String, Object> obj) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		String user_id = obj.get("user_id").toString();
		double amount = Double.parseDouble(obj.get("amount").toString());
		
		try {
			 User user = userRepository.getOne(user_id);
			 user.getWallet().increaseBal(amount);
			 userRepository.saveAndFlush(user);
			 
			 String transactionRefID = "Q" + idDateFormat.format(new Date()) + count++;
			 Date date = localDateFormat.parse(localDateFormat.format(new Date()));
			 Transaction newTransaction = new Transaction(transactionRefID, "topup", "Top Up"  , user.getUuid(), amount, date);
			 transactionRepository.saveAndFlush(newTransaction);
			 response.put("result", "success");
			 return response;
		} catch (Exception e) {}
		
		response.put("result", "failed");
		return response;
	}

}
