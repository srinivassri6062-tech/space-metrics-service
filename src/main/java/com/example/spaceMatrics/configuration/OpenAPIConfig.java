package com.example.spaceMatrics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
	@Bean
	public OpenAPI spaceOpenAPI() {
		return new OpenAPI().info(new Info().title("Space Metrics Service API").version("1.0.0")
				.description("High-throughput ingestion & metrics aggregation service"));
	}

}
