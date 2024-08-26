package com.moinul.LostAndFound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LostAndFoundApplication {

	public static void main(String[] args) {

		String currentDirectory = System.getProperty("user.dir");
		System.out.println("*******The current working directory is " + currentDirectory);
		SpringApplication.run(LostAndFoundApplication.class, args);
	}

}
