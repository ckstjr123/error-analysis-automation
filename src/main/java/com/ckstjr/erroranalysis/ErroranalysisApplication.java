package com.ckstjr.erroranalysis;

import com.ckstjr.erroranalysis.config.FlowiseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients
@EnableConfigurationProperties(FlowiseProperties.class)
@SpringBootApplication
public class ErroranalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErroranalysisApplication.class, args);
	}

}
