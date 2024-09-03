package net.datasa.nanum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NanumApplication {

	public static void main(String[] args) {
		SpringApplication.run(NanumApplication.class, args);
	}

}
