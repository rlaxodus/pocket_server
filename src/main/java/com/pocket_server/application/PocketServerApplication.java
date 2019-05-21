package com.pocket_server.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.pocket_server.repository.UserRepository;

@SpringBootApplication
@ComponentScan({"com.pocket_server.controller", "com.pocket_server.service", "com.pocket_server.config"})
@EnableJpaRepositories(basePackages = "com.pocket_server.repository")
@EntityScan(basePackages = "com.pocket_server")
@EnableScheduling
public class PocketServerApplication extends SpringBootServletInitializer {
    
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PocketServerApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(PocketServerApplication.class, args);
	}

}

