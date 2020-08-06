package ru.otus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
public class AppConfig {

    @Bean
    EntityManagerFactory entityManager() {
        return Persistence.createEntityManagerFactory("otus-hibernate");
    }

}
