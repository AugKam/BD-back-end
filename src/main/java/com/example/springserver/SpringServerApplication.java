package com.example.springserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "com.example.springserver")
@EnableScheduling

public class SpringServerApplication {


	public static void main(String[] args) {
		SpringApplication.run(SpringServerApplication.class, args);



	}

}
