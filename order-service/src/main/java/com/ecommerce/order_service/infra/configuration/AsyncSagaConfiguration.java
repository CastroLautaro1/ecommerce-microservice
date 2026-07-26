package com.ecommerce.order_service.infra.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncSagaConfiguration {

    @Bean(name = "sagaTaskExecutor")
    public Executor sagaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Hilos base que siempre están vivos esperando eventos
        executor.setCorePoolSize(5);

        // Hilos máximos si hay un pico masivo de creación de órdenes
        executor.setMaxPoolSize(20);

        // Tamaño de la cola en memoria si los hilos están todos ocupados (Feign lento)
        executor.setQueueCapacity(100);

        // Prefijo para identificar los hilos en los Logs
        executor.setThreadNamePrefix("Saga-Async-");

        executor.initialize();
        return executor;
    }
}
