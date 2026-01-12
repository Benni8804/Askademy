package com.eduhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EduHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduHubApplication.class, args);
    }
}