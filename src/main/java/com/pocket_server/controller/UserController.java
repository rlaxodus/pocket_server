package com.pocket_server.controller;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pocket_server.service.TokenService;
import com.pocket_server.service.UserService;

@RestController
@RequestMapping("/users/{id}")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenService tokenService;
	
	private SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:a ZZZZ");

	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> retrieveUserDetails (@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try { 
				return new ResponseEntity<Map<String, Object>>(userService.getUserDetails(id), HttpStatus.OK);
			}
			catch (Exception e) {
				
			}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	} 
	
	@RequestMapping(value="/balance", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getBalance(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {

		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try {
				double balance = userService.retrieveWalletBalance(id);
				localDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
				Map<String, Object> response = new HashMap<>();
				response.put("user_id", id);
				response.put("balance", balance);
				response.put("AS_OF", localDateFormat.format(new Date()));
				
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} catch (Exception e) {}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/auth-code", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAuthCode(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<>();
		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try { 
				String authCode = userService.retrieveAuthCode(id);
				response.put("auth_code", authCode);
				
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);

	}
	
	@RequestMapping(value="/limit", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> setLimit(@PathVariable("id") String id, @RequestBody Map<String, Object> obj, @RequestHeader HttpHeaders headers) throws NoSuchAlgorithmException {
		Map<String, Object> response = new LinkedHashMap<>();
		if (tokenService.checkTokenValid(id, headers.get("Authorization").get(0))) {
			try { 

				response = userService.changeWalletLimit(id, 
						Double.parseDouble(obj.get("limit").toString()), Integer.parseInt(obj.get("option").toString()));
				
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);

	}
}
