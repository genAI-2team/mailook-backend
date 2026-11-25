package com.mailook.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// 성향 정리를 비동기로 처리하기 위한 풀입니다. 호출은 @Async("preferenceTaskExecutor").
@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "preferenceTaskExecutor")
	public Executor preferenceTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("preference-async-");
		executor.initialize();
		return executor;
	}

}
