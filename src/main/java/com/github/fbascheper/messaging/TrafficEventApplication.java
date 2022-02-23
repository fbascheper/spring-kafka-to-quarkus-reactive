package com.github.fbascheper.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application for traffic event handling.
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@SpringBootApplication(scanBasePackages = {"com.github.fbascheper.messaging"})
@EnableConfigurationProperties
public class TrafficEventApplication {

    private final Logger logger = LoggerFactory.getLogger(TrafficEventApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TrafficEventApplication.class, args);
    }

}
