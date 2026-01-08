package com.hive.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hive")
public class HiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(HiveApplication.class, args);
    }
}
