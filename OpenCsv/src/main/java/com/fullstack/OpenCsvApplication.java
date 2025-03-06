package com.fullstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;

@SpringBootApplication
public class OpenCsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenCsvApplication.class, args);
	}

}
