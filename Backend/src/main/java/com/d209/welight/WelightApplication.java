package com.d209.welight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WelightApplication {

	public static void main(String[] args) {
		SpringApplication.run(WelightApplication.class, args);
	}

}
