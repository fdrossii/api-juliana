package com.juliana.api_juliana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiJulianaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiJulianaApplication.class, args);
	}

}
