package io.shrouded;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan
@EnableReactiveMongoRepositories(basePackages = "io.shrouded.data")
@SpringBootApplication
public class UdpServerApp {
    public static void main(String[] args) {
        SpringApplication.run(UdpServerApp.class, args);
    }
}