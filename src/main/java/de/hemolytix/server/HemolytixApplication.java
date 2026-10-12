package de.hemolytix.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class HemolytixApplication {

	public static void main(String[] args) {
		SpringApplication.run(HemolytixApplication.class, args);
	}
}
