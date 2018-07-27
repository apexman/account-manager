package com.maksimov.accountManager;

import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
public class AccountManager {

	public static void main(String[] args) {
		SpringApplication.run(AccountManager.class, args);
	}

	@Bean
	public SessionFactory sessionFactory(EntityManagerFactory emf) {
		return emf.unwrap(SessionFactory.class);
	}
}
