package com.pokemondex.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.annotation.PreDestroy;

@Configuration
public class PokeApiConfig {

    private ExecutorService pool;

    @Bean
    RestClient pokeApiRestClient(PokeApiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }

    @Bean(name = "pokeApiExecutor")
    Executor pokeApiExecutor(PokeApiProperties properties) {
        if (properties.parallelism() <= 1) {
            return Runnable::run;
        }
        pool = Executors.newFixedThreadPool(properties.parallelism(), runnable -> {
            Thread thread = new Thread(runnable, "pokeapi");
            thread.setDaemon(true);
            return thread;
        });
        return pool;
    }

    @PreDestroy
    void shutdown() {
        if (pool != null) {
            pool.shutdown();
        }
    }
}
