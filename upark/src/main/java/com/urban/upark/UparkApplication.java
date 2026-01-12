package com.urban.upark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UparkApplication {

	public static void main(String[] args) {
		SpringApplication.run(UparkApplication.class, args);
	}

}
