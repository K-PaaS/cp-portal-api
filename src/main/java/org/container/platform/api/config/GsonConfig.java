package org.container.platform.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gson Config 클래스 (GsonBuilder, Converter for GsonBuilder)
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.24
 */
@Configuration
public class GsonConfig {

    @Bean
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }

    /**
     * Gson Builder
     *
     * @param builder the another gson builder
     * @return the gson builder
     */
    @Bean
    public Gson gson(GsonBuilder builder) {
        return builder.create();
    }
}
