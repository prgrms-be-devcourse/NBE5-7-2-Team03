package com.team573.gongguri.domain.groupPurchase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스 요청 "/uploads/**" → 실제 경로 "file:///C:/Users/Lucky/gongguri-uploads/"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + System.getProperty("user.home") + "/gongguri-uploads/");
    }
}
