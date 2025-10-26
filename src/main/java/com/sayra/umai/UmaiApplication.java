package com.sayra.umai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UmaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmaiApplication.class, args);
	}

}
