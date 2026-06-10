package com.example.energymix;

import com.example.energymix.client.CarbonIntensityClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.ZonedDateTime;

@SpringBootApplication
public class EnergyMixApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergyMixApplication.class, args);
	}

	// Tymczasowy test w konsoli:
	//	@Bean
	//	CommandLineRunner testClient(CarbonIntensityClient client) {
	//		return args -> {
	//			var data = client.getGenerationMix(ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
	//			System.out.println("POBRANO DANYCH: " + data.data().size());
	//		};
	//	}
}
