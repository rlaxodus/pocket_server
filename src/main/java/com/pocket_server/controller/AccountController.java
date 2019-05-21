package com.pocket_server.controller;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pocket_server.service.AccountService;

@RestController
@RequestMapping("/users")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> registerNewUser(@RequestBody Map<String,Object> body) {
		String name = body.get("name").toString();
		String phoneNumber = body.get("phoneNumber").toString();
		String password = body.get("password").toString();
		String result = accountService.registerUser(name,password,phoneNumber);
		
		Map<String, Object> response = new HashMap<>();
		if (!result.equals("failed") && !result.equals("used")) {
			response.put("result", "success");
			response.put("user_id", result);
			response.put("message", "User Registered");	
		
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		} else if (result.equals("used")) {
			response.put("result", "failed");
			response.put("message", "phonenumber already in use");
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
		}
		
		response.put("result", "failed");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String,Object> body) throws NoSuchAlgorithmException, ParseException {
		String user_id;
		Map<String, Object> response = new HashMap<>();
	
		if (Integer.parseInt(body.get("mode").toString()) == 1) {
			String phoneNumber = body.get("phoneNumber").toString();
			String biometric_token = body.get("token").toString();
			response = accountService.loginUser2(biometric_token, phoneNumber);
		} else {
			String phoneNumber = body.get("phoneNumber").toString();
			String password = body.get("password").toString();
			response = accountService.loginUser(password, phoneNumber);
		}

		if ("success".compareTo(response.get("result").toString()) == 0) {
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);	
	}
	
	@RequestMapping(value = "/fcmtoken", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> updateFCMToken(@RequestBody Map<String,Object> body) {
		String user_id = body.get("user_id").toString();
		String FCM_TOKEN = body.get("fcm_token").toString();
		accountService.addFCMTokenToUser(user_id, FCM_TOKEN);
		Map<String, Object> response = new HashMap<>();
		if (!user_id.equals("failed")) {
			response.put("result", "success");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		response.put("result", "failed");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);	
	}
	
	@RequestMapping(value = "/exist/{phone}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> loginUser(@PathVariable("phone") String phone) {
		String name = accountService.checkExist(phone);
		
		Map<String, Object> response = new HashMap<>();
		if (!name.equals("DO_NOT_EXIST")) {
			response.put("exist", "true");
			response.put("name", name);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		} else if (name.equals("DO_NOT_EXIST")){
			response.put("exist", "false");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);	
	}
}
