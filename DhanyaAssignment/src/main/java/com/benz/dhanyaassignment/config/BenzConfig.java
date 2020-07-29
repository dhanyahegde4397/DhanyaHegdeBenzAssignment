package com.benz.dhanyaassignment.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BenzConfig {

	@Bean
	public RestTemplate restTemplate() {

		return new RestTemplate();
	}

	@Bean(name = "taskExecutor")
	 public Executor getAsyncExecutor() {
	 ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	 executor.setCorePoolSize(1000); 
	 executor.setMaxPoolSize(2000); 
	 executor.setQueueCapacity(100); 
	 executor.setWaitForTasksToCompleteOnShutdown(true);
	 executor.initialize();
	 return executor;
	 }
}
