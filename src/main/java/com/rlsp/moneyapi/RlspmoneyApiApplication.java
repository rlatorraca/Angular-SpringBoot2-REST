package com.rlsp.moneyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

/**
 * @SpringBootApplication ==> APlicacao com Spring Boot
 * @author rlatorraca
 *
 */

@EnableConfigurationProperties(RlspMoneyApiProperty.class) // Controle o ambiente de PRoducao
@SpringBootApplication // Mostra que é uma aplicacao Spring Boot
public class RlspmoneyApiApplication {
	
	private static ApplicationContext APPLICATION_CONTEXT;

	public static void main(String[] args) {
		SpringApplication.run(RlspmoneyApiApplication.class, args);
	}
	
	public static <T> T getBean(Class<T> type) {
		return APPLICATION_CONTEXT.getBean(type);
	}

}
