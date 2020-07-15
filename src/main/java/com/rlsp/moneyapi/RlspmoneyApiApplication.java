package com.rlsp.moneyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

/**
 * @SpringBootApplication ==> APlicacao com Spring Boot
 * @author rlatorraca
 *
 */

@EnableConfigurationProperties(RlspMoneyApiProperty.class) // Controle o ambiente de PRoducao
@SpringBootApplication // Mostra que é uma aplicacao Spring Boot
public class RlspmoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RlspmoneyApiApplication.class, args);
	}

}
