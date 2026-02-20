package com.communitybudget.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CommunityBudget API")
                        .version("1.0.0")
                        .description("Documentación de la API para CommunityBudget.")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("Tu Nombre")
                                .email("tuemail@alumno.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}