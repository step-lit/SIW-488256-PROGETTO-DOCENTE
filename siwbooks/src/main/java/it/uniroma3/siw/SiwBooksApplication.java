package it.uniroma3.siw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SiwBooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiwBooksApplication.class, args);
	}

}
