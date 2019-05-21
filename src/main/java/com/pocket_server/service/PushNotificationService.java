package com.pocket_server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PushNotificationService {

	private static final String FIREBASE_SERVER_KEY = "AAAAr9i4VT0:APA91bFt73c4TuNLIRgejJjeYjw6c_aQrTq9CTjcxc6sW0GwDBADTCpMPxzaepyYscXAppBJupzcvrErG70TEE4qcbheFGMCOFsHFbRW3Nhq_vdzL1w89kI91UQwAHEhZIWR-El-lD0X";
	private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
	
	@Async
	public CompletableFuture<String> send(String fcmToken, String title, String message) {
		
		//Construct notification
		JSONObject body = new JSONObject();
		body.put("to", fcmToken);
		
		/*
		JSONObject notification = new JSONObject();
		notification.put("title", title);
		notification.put("body", message);
 
		body.put("notification", notification);
		*/
		
		JSONObject data = new JSONObject();
		data.put("title", title);
		data.put("message", message);
		
		body.put("data", data);	
		//END of construct notification
		
		HttpEntity<String> request = new HttpEntity<>(body.toString());
 
		RestTemplate restTemplate = new RestTemplate();

		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		restTemplate.setInterceptors(interceptors);

		String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, request, String.class);

		return CompletableFuture.completedFuture(firebaseResponse);
	}
	
	@Async
	public static CompletableFuture<String> sendAll(String title, String message) {
		
		//Construct notification
		JSONObject body = new JSONObject();
		body.put("to", "/topics/all");
		
		JSONObject notification = new JSONObject();
		notification.put("title", title);
		notification.put("body", message);
 
		body.put("notification", notification);
		
		JSONObject data = new JSONObject();
		data.put("title", title);
		data.put("message", message);
		
		body.put("data", data);	
		//END of construct notification
		
		HttpEntity<String> request = new HttpEntity<>(body.toString());
 
		RestTemplate restTemplate = new RestTemplate();

		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		restTemplate.setInterceptors(interceptors);

		String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, request, String.class);

		return CompletableFuture.completedFuture(firebaseResponse);
		
	}

}
