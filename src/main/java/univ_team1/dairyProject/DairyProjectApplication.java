package univ_team1.dairyProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DairyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DairyProjectApplication.class, args);
	}

}