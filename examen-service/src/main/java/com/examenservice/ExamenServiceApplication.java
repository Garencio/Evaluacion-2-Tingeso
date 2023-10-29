package com.examenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ExamenServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamenServiceApplication.class, args);
	}

}
