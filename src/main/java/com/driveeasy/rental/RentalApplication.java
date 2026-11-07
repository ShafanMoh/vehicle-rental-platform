package com.driveeasy.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Entry point of the DriveEasy Vehicle Rental Service Platform.
 * Run this class from IntelliJ IDEA and open http://localhost:8080
 */
@SpringBootApplication
public class RentalApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RentalApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RentalApplication.class);
    }
}
