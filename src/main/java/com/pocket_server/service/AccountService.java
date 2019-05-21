package com.pocket_server.service;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pocket_server.model.User;
import com.pocket_server.model.Wallet;
import com.pocket_server.repository.UserRepository;
import com.pocket_server.repository.WalletRepository;
import com.pocket_server.utility.Utility;

@Service
public class AccountService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	private TokenService tokenService = new TokenService();
	
	private SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public String registerUser (String name, String password, String phoneNumber) {
		UUID uuid = UUID.randomUUID();
	    String randomUUIDString = uuid.toString();
	    boolean exist = userRepository.existsByphoneNumber(phoneNumber);

		if (!exist) {
		    //Create User
		    User newUser = new User(randomUUIDString, name, password, phoneNumber);
			//Create Wallet
			Wallet newWallet = new Wallet();
			
			//Set Wallet to user
			newWallet.setUser(newUser);
			
			//Set Wallet to user
			newUser.setWallet(newWallet);
			
			//Save to db
			walletRepository.save(newWallet);
			userRepository.save(newUser);
			
			return randomUUIDString;
		} else if (exist) {
			return "used";
		} else {
			return "failed";
		}
		
	}
	
	public Map<String, Object> loginUser (String password, String phoneNumber) throws NoSuchAlgorithmException, ParseException {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> token = new HashMap<>();
		localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		
		//Find User
	    User user = userRepository.getByphoneNumber(phoneNumber);
		if (password.equals(user.getPassword())) {
			response.put("result", "success");
			response.put("name", user.getName());
			response.put("user_id", user.getUuid());
			response.put("daily_limit", user.getDaily_limit());
			response.put("per_transaction_limit", user.getPer_transaction_limit());
			
			//Generate session token
			token = tokenService.generateToken(user.getUuid());
			
			//save token
			Date expiryDate = localDateFormat.parse(token.get("expiry").toString());
			user.setSession_token(token.get("token").toString());
			user.setSession_expiry(expiryDate);

			userRepository.saveAndFlush(user);
			
			//put token in response
			response.put("session_token", token);
			return response;
		}
		
		response.put("result", "failed");
		return response;
	}
	
	public Map<String, Object> loginUser2 (String token, String phoneNumber) throws NoSuchAlgorithmException, ParseException {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> token2 = new HashMap<>();
		localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		
	    //Find User
	    User user = userRepository.getByphoneNumber(phoneNumber);
		String check_token = Utility.hashSHA256(user.getPhoneNumber() + "|" + user.getPassword());
		
		if (token.equals(check_token)) {
			response.put("result", "success");
			response.put("name", user.getName());
			response.put("user_id", user.getUuid());
			response.put("daily_limit", user.getDaily_limit());
			response.put("per_transaction_limit", user.getPer_transaction_limit());
			
			//Generate session token
			token2 = tokenService.generateToken(user.getUuid());
			
			//Save token
			Date expiryDate = localDateFormat.parse(token2.get("expiry").toString());
			user.setSession_token(token2.get("token").toString());
			user.setSession_expiry(expiryDate);
			
			userRepository.saveAndFlush(user);
			
			///put token in response
			response.put("session_token", token2);
			return response;
		}
		
		response.put("result", "failed");
		return response;
		
	}
	public String addFCMTokenToUser (String user_id, String token) {
		
	    //Find User
	    try {
			User user = userRepository.getOne(user_id);
			user.setFcm_token(token);
			userRepository.saveAndFlush(user);
			return "success";
	    } catch (Exception e) {
	    	return "failed";
	    }
	}

	public String checkExist(String phone) {
		userRepository.existsByphoneNumber(phone);
		
		if (userRepository.existsByphoneNumber(phone)) {
			User user = userRepository.getByphoneNumber(phone);
			return user.getName();
		}
		
		return "DO_NOT_EXIST";
	}

}
