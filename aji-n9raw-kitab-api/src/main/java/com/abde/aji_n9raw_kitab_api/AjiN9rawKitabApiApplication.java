package com.abde.aji_n9raw_kitab_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AjiN9rawKitabApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AjiN9rawKitabApiApplication.class, args);
	}

}
