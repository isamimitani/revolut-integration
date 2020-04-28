package com.tibber.dev.revolutintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * An application class to start this Spring Boot application.
 *
 * @auther Isami Mitani
 * @version 1.0
 */
@SpringBootApplication
public class RevolutIntegrationApplication {

//    @Bean
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(RevolutIntegrationApplication.class, args);
        System.exit(SpringApplication.exit(applicationContext));
    }

}
