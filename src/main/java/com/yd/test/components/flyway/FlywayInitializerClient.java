package com.yd.test.components.flyway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
/***
 * 
 * FlywayInitializerClient
 */
@Conditional({ FlywayEnabledCondition.class })
@Configuration
public class FlywayInitializerClient {

    @Bean("flywayInitializer")
    public FlywayInitializerBean restTemplate() {
        return new FlywayInitializerBean();
    }
}
