package de.telran.server_spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableAsync
public class Config {

    @Bean
    public AtomicInteger atomic(){
        return new AtomicInteger();
    }

    @Bean
    public Executor threadExecutor () {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Executor threadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

    @Bean
    public Socket socket (){
        return new Socket();
    }
}
