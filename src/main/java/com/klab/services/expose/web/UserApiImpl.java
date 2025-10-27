package com.klab.services.expose.web;

import com.klab.services.model.api.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserApi interface.
 * <b>Class</b>: UserApiImpl
 * <b>Copyright</b>: 2025 Klab
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 * @version 1.0
 */

public class UserApiImpl implements UserApi {

  @Override
  public Mono<ResponseEntity<User>> getUserByName(String username, ServerWebExchange exchange) {
    // Implement your logic here
    return Mono.just(ResponseEntity.ok(new com.klab.services.model.api.User()));
  }

}
