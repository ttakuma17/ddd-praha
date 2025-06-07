package com.ddd.praha;

import org.springframework.boot.SpringApplication;

public class TestPrahaApplication {

	public static void main(String[] args) {
		SpringApplication.from(PrahaApplication::main)
				.with(TestContainerLocalConfiguration.class)
				.run(args);
	}

}
