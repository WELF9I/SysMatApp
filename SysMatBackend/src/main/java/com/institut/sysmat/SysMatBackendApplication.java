package com.institut.sysmat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.institut.sysmat.model")
@EnableJpaRepositories(basePackages = "com.institut.sysmat.repository")
public class SysMatBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SysMatBackendApplication.class, args);
	}

}
