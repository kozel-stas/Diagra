package com.diagra.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = {"com.diagra.dao", "com.diagra.logic"})
@PropertySource("classpath:application-logic.properties")
public class LogicConfig {

    private final int corePoolSize;
    private final int maxPoolSize;

    public LogicConfig(
            @Value("${corePoolSize}") int corePoolSize,
            @Value("${maxPoolSize}") int maxPoolSize
    ) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }

    @Bean
    public AsyncListenableTaskExecutor asyncListenableTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.initialize();
        return executor;
    }

}
