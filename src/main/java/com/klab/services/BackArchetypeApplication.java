package com.klab.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application starter.
 * <b>Class</b>: BackendArchetypeApplication
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@SpringBootApplication
public class BackArchetypeApplication {

  /**
   * The main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    loadEnvVariables();
    SpringApplication.run(BackArchetypeApplication.class, args);
  }

  private static void loadEnvVariables() {
    Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load();

    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );
  }

}
