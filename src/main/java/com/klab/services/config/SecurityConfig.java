package com.klab.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for the application.
 * <b>Class</b>: SecurityConfig
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  /**
   * Configures the security web filter chain.
   *
   * @param http the {@link ServerHttpSecurity} to configure
   * @return the configured {@link SecurityWebFilterChain}
   */

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .logout(ServerHttpSecurity.LogoutSpec::disable)
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
            .anyExchange().permitAll()
        )
        .build();
  }

}
