package com.tibber.dev.revolutintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.TimeZone;

/**
 * An application class to start this Spring Boot application.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
@SpringBootApplication
public class RevolutIntegrationApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(RevolutIntegrationApplication.class, args);
        System.exit(SpringApplication.exit(applicationContext));
    }

}
