package com.zerobase.cms.user.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Qualifier(value = "mailgun")
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(){
        return new BasicAuthRequestInterceptor("api", "abd323f89b272c9e1cd0e24a6d5d45b2-d51642fa-0ea9dfb1");
    }
}
