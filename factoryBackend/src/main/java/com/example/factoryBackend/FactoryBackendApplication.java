package com.example.factoryBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FactoryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FactoryBackendApplication.class, args);
	}

}
