package com.pocket_server.controller;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pocket_server.model.Contract;
import com.pocket_server.model.Transaction;
import com.pocket_server.service.TokenService;
import com.pocket_server.service.TransactionalService;

@RestController
@RequestMapping("/transactional")
public class TransactionController {
	
	@Autowired
	private TransactionalService transactionalService;
	
	@Autowired
	private TokenService tokenService;
	
	@RequestMapping(value="/payment", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> paymentProcess (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();

		if (tokenService.checkTokenValid(obj.get("payee_id").toString(), headers.get("Authorization").get(0))) {
			try {
				response = transactionalService.processPayment(obj);
				
				if (response.get("result").toString().compareTo("success") == 0) {
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				}
				
			} catch (Exception e) {}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/payment/quickpay", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> quickpayProcess (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		if (tokenService.checkTokenValid(obj.get("merchant_id").toString(), headers.get("Authorization").get(0))) {
			try {
				response = transactionalService.processQuickPay(obj);
		
				if (response.get("result").toString().compareTo("success") == 0) {
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}	
	
	@RequestMapping(value="/payment/phone", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> phonePaymentProcess (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		if (tokenService.checkTokenValid(obj.get("payee_id").toString(), headers.get("Authorization").get(0))) {
			try {
				response = transactionalService.processPaymentPhone(obj);
				
				if (response.get("result").toString().compareTo("success") == 0) {
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				}
				
			} catch (Exception e) {}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/transactionhistory/{id}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> transactionHistory (@PathVariable("id") String id, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try {
				List<Transaction> transactionList = transactionalService.retrieveAllTransactionByUserId(id);
				response.put("result", "success");
				response.put("transactions", transactionList);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		response.put("result", "failed");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/transactiondetails", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> transactionDetailsById (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		if (tokenService.checkTokenValid(obj.get("transaction_id").toString(), headers.get("Authorization").get(0))) {
			try {
				Transaction transactionList = transactionalService.retrieveTransactionByTransactionId(obj.get("transaction_id").toString());
				response.put("result", "success");
				response.put("transactions", transactionList);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}
			catch (Exception e) {}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/contract", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createContract (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		if (tokenService.checkTokenValid(obj.get("user1_id").toString(), headers.get("Authorization").get(0))) {
			try {
				String contractID = transactionalService.createContract(obj);
				response.put("result", "success");
				response.put("contract_id", contractID);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(value="/contract/ack", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> ackContract (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		
		if (tokenService.checkTokenValid(obj.get("user_id").toString(), headers.get("Authorization").get(0))) {
			try {
				String contractStatus = transactionalService.acknowledgeContract(obj);
				
				if (contractStatus.compareTo("DOES_NOT_EXIST") == 0) {
					response.put("result", "failed");
					response.put("contract_id", (obj.get("contract_id").toString()));
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
				} else if (contractStatus.compareTo("failed") == 0) {
					response.put("result", "failed");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
				} else {
					response.put("result", "success");
					response.put("contract_id", (obj.get("contract_id").toString()));
					response.put("contract_status", contractStatus);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		
		response.put("result", "failed");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
	}
	
	@RequestMapping(value="/contract/terminate", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> terContract (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		if (tokenService.checkTokenValid(obj.get("user_id").toString(), headers.get("Authorization").get(0))) {
			try {
				String contractStatus = transactionalService.terminateContract(obj);
				if (contractStatus.compareTo("4") == 0) {
					response.put("result", "success");
					response.put("contract_id", (obj.get("contract_id").toString()));
					response.put("contract_status", contractStatus);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				} else if (contractStatus.compareTo("DOES_NOT_EXIST") == 0) {
					response.put("result", "failed");
					response.put("contract_id", (obj.get("contract_id").toString()));
					response.put("contract_status", contractStatus);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
	
	
	@RequestMapping(value="/contract/{id}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> allContractDetails (@PathVariable("id") String id, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try {
				List<Contract> contractList = transactionalService.retrieveAllContractByUserId(id);
				response.put("result", "success");
				response.put("contracts", contractList);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/topup", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> topUp (@RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		if (tokenService.checkTokenValid(obj.get("user_id").toString(), headers.get("Authorization").get(0))) {
			try {
				response = transactionalService.processTopUp(obj);
				
				if (response.get("result").toString().compareTo("success") == 0) {
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
				}
				
			} catch (Exception e) {}
		}else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}
	
}
