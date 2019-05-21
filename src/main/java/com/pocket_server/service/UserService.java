package com.pocket_server.service;


import com.pocket_server.model.User;
import com.pocket_server.repository.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class UserService {
		
		@Autowired
		private UserRepository userRepository;
		
		@Autowired
		ThreadPoolTaskScheduler scheduler;
		
		public double retrieveWalletBalance (String user_id) {
			return userRepository.getOne(user_id).getWallet().getBalance();
		}
		
		public Map<String, Object> getUserDetails(String user_id) {
			User user = userRepository.getOne(user_id);
			Map<String, Object> response = new HashMap<>();
			response.put("name", user.getName());
			response.put("phoneNumber", user.getPhoneNumber());
			response.put("daily_limit", user.getDaily_limit());
			response.put("per_transaction_limit", user.getPer_transaction_limit());
			return response;
		}

		public String retrieveAuthCode(String user_id) throws NoSuchAlgorithmException {
			RandomString gen = new RandomString();
			String authCode = gen.nextString();
			
			User user = userRepository.getOne(user_id);
			user.setAuth_code(authCode);
			userRepository.saveAndFlush(user);
			
			final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
			executor.schedule(new Runnable() {
			  @Override
			  public void run() {
					user.setAuth_code("EXPIRED");
					if (user.getAuth_code().equals(authCode)) {
						userRepository.saveAndFlush(user);
					}
			  }
			}, 1, TimeUnit.MINUTES);
			
			return authCode;
		}

		public Map<String, Object> changeWalletLimit (String user_id, double limit, int option) {
			Map<String, Object> response = new HashMap<>();
			
			try { 
				User user = userRepository.getOne(user_id);
				
				if (option == 1) {
					user.setDaily_limit(limit);
					response.put("daily_limit", user.getDaily_limit());
				} else if (option == 0) {
					user.setPer_transaction_limit(limit);
					response.put("per_transaction_limit", user.getPer_transaction_limit());
				}
				
				userRepository.saveAndFlush(user);
				
				response.put("result", "success");
				return response;
				
			} catch (Exception e) {
				response.put("result", "failed");
				response.put("message", "exception caught");
				return response;
			}

		}
		
}
