package com.cxp.personaleureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PersonalEureka {

	public static void main(String[] args) {
		SpringApplication.run(PersonalEureka.class, args);
	}

}
