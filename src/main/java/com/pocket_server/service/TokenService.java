package com.pocket_server.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pocket_server.model.User;
import com.pocket_server.repository.UserRepository;
import com.pocket_server.utility.Utility;

@Service
public class TokenService {
	
	@Autowired
	private UserRepository userRepository;
	
	private Date currentDateTime;
	
	public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	// 2048 bit keys should be secure until 2030 - https://web.archive.org/web/20170417095741/https://www.emc.com/emc-plus/rsa-labs/historical/twirl-and-rsa-key-size.htm
	public static final int SECURE_TOKEN_LENGTH = 256;

	private static final SecureRandom random = new SecureRandom();

	private static final char[] symbols = CHARACTERS.toCharArray();

	private static final char[] buf = new char[SECURE_TOKEN_LENGTH];

	public TokenService() {
		currentDateTime = new Date();
	}
	
	public String nextRandom() {
	    for (int idx = 0; idx < buf.length; ++idx)
	        buf[idx] = symbols[random.nextInt(symbols.length)];
	    return new String(buf);
	}
	
	public Map<String, Object> generateToken(String username) throws NoSuchAlgorithmException {
   
		Map<String, Object> response = new HashMap<>();
		
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
		String signature = Utility.hashSHA256(username + "pocket|" + currentTime.toString() + "|"  + expiryTime.toString() + "|"  + nextRandom());
		
		String rawToken = username + "|" + "|" + signature;
		String token = Base64.getEncoder().encodeToString(signature.getBytes());

		response.put("token", token);
		response.put("expiry", expiryTime);
		return response;
		
	}
	
	public boolean checkTokenValid (String user_id, String token) {
		User user = userRepository.getOne(user_id);
		token = token.substring(7, token.length());

		if (user.getSession_token().equals(token)) {
			
			Date current = new Date();
			boolean expired = current.after(user.getSession_expiry());
			if (!expired) {
				System.out.println("Not expired");
				return true;
			} else {
				System.out.println("Expired");
				return false;
			}
		}
		
		
		return false;
		
	}
}
