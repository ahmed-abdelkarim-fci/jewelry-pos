package com.jewelry.pos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jewelryShopOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Jewelry POS API (Enterprise)")
                .description("Backend for Gold/Jewelry Point of Sale System")
                .version("1.0.0"));
    }
}