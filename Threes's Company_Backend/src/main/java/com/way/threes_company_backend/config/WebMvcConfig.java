package com.way.threes_company_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry reegistry) {
        reegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://192.168.0.108:3000", "http://localhost:5173")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
    }

}
