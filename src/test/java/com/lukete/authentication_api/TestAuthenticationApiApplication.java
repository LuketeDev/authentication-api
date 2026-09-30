package com.lukete.authentication_api;

import org.springframework.boot.SpringApplication;

public class TestAuthenticationApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(AuthenticationApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
