package com.back.zapateria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI footStyleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FootStyle API")
                        .version("1.0.0")
                        .description("Documentación de los servicios REST del backend de Zapatería")
                        .contact(new Contact()
                                .name("FootStyle Team")
                                .email("info@footstyle.com"))
                        .license(new License().name("Internal use")));
    }
}
