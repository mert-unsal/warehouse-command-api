package com.ikea.warehouse_command_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableMongoAuditing
@EnableMongoRepositories
@SpringBootApplication
public class WarehouseCommandApiApplication {

	static void main(String[] args) {
		SpringApplication.run(WarehouseCommandApiApplication.class, args);
	}

}
