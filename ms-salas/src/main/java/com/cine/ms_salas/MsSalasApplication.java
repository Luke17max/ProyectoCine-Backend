package com.cine.ms_salas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients//habilita la comunicación HTTP declarativa
@SpringBootApplication
public class MsSalasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSalasApplication.class, args);
	}

}
