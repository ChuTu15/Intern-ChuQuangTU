package com.example.springboot.demo.spring.boot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .info(new Info().title("Spring Boot Demo Application")
                        .description("This is a sample Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
    }

    @Bean
    public OpenApiCustomizer globalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses apiResponses = operation.getResponses();
            apiResponses.addApiResponse("200", createApiResponse("Successful operation"));
            apiResponses.addApiResponse("400", createApiResponse("Bad Request"));
            apiResponses.addApiResponse("401", createApiResponse("Unauthorized"));
            apiResponses.addApiResponse("403", createApiResponse("Forbidden"));
            apiResponses.addApiResponse("404", createApiResponse("Not Found"));
            apiResponses.addApiResponse("429", createApiResponse("Too Many Requests"));
            apiResponses.addApiResponse("500", createApiResponse("Internal Server Error"));
        }));
    }

    private ApiResponse createApiResponse(String message) {
        return new ApiResponse().description(message);
    }
}
