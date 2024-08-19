package com.andrea.reactive;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Satellites", version = "0.0.1 - SNAPSHOT", description = "This is the Back-End API Swagger for Satellites Project. This project is an example of WebFlux usage in SpringBoot."))
public class ReactiveSatellitesApp {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveSatellitesApp.class, args);
	}

}
