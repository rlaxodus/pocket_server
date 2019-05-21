package com.pocket_server.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledService {
	
	@Scheduled(cron="0 0 0 * * *", zone="Asia/Singapore")
    public void sendTestNotif() {
		//PushNotificationService.sendAll("Scheduled Task", "This is a Scheduler test ");
    }
}
