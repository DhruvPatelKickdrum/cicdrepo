package com.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class GeocodingApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GeocodingApplication.class, args);
        log.info("Application Start");
    }
}