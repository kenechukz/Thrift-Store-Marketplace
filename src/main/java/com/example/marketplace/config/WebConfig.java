package com.example.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/", "/home", "/catalogue", "/basket/**", "/orders/**", "/products/**", "/checkout", "/admin/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/h2-console/**"
                );
    }
}
