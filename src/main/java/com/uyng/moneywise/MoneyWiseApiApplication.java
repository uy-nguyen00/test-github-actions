package com.uyng.moneywise;

import com.uyng.moneywise.role.Role;
import com.uyng.moneywise.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class MoneyWiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyWiseApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				Role userRole = Role
						.builder()
						.name("USER")
						.build();

				roleRepository.save(userRole);
			}
		};
	}
}
