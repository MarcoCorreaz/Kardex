package com.estoque;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EstoqueApplication {

	public static void main(String[] args) {
		// Todas as datas do sistema sao tratadas em UTC.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(EstoqueApplication.class, args);
	}

}
