package com.abde.aji_n9raw_kitab_api;

import com.abde.aji_n9raw_kitab_api.role.Role;
import com.abde.aji_n9raw_kitab_api.role.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class AjiN9rawKitabApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AjiN9rawKitabApiApplication.class, args);
	}


	@Bean
	CommandLineRunner runner(RoleRepo roleRepo){
		return args -> {
			if(roleRepo.findByName("USER").isEmpty()){
				roleRepo.save(
						Role.builder()
								.name("USER")
								.build()
				);
			}
		};
	}
}
