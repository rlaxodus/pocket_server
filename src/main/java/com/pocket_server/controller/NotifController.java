package com.pocket_server.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pocket_server.service.PushNotificationService;

@RestController
public class NotifController {

	
	@Autowired
	PushNotificationService pushNotificationsService;
 
	@RequestMapping(value = "/notif", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Void> send() throws JSONException {
 
		CompletableFuture<String> pushNotification = pushNotificationsService.send("/topics/all", "Test", "Hello! This is from pocket server! This part is to test long text so that to ensure the notifications are expandable and working properly. OH HEY! ");
		CompletableFuture.allOf(pushNotification).join();
 
		try {
			String firebaseResponse = pushNotification.get();
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
 
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/notif/tokencheck", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Void> send2() throws JSONException {
 
		CompletableFuture<String> pushNotification = pushNotificationsService.send("/topics/all", "Test", "Hello! This is from pocket server! This part is to test long text so that to ensure the notifications are expandable and working properly. OH HEY! ");
		CompletableFuture.allOf(pushNotification).join();
 
		try {
			String firebaseResponse = pushNotification.get();
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
 
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
