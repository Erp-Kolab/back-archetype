package com.klab.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * Application starter.
 * <b>Class</b>: ClientArchetypeApplication
 * <b>Copyright</b>: 2025 Klab
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 * @version 1.0
 */

@SpringBootApplication
@EnableFeignClients(basePackages = "com.klab.services.clientarchetype.proxy")
public class ClientArchetypeApplication {

  /**
   * The main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(ClientArchetypeApplication.class, args);
  }

}
