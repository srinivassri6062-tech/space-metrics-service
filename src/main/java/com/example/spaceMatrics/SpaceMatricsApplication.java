package com.example.spaceMatrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpaceMatricsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpaceMatricsApplication.class, args);
	}

}
