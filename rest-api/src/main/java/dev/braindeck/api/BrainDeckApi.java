package dev.braindeck.api;

import dev.braindeck.api.config.DBConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories
@SpringBootApplication
public class BrainDeckApi {
    public static void main(String[] args) {
        SpringApplication.run(BrainDeckApi.class, args);
        //DBConfig.languagesFill();


    }
}