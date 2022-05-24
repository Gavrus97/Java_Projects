package de.telran.gateway_spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Socket;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class Config {

    @Bean
    public Socket socket(){
        return new Socket();
    }

    @Bean
    public Executor threadExecutor(){
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Executor threadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(200);
        executor.setCorePoolSize(200);
        return executor;
    }
}
