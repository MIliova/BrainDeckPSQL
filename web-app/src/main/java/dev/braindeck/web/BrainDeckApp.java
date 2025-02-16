package dev.braindeck.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
//@ComponentScan(basePackages = {"dev.braindeck.web", "dev.braindeck.api"})
public class BrainDeckApp {

    public static void main(String[] args) {
        SpringApplication.run(BrainDeckApp.class, args);
    }

}

