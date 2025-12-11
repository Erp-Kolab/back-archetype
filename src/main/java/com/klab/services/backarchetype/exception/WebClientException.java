package com.klab.services.backarchetype.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Generic exception for WebClient errors from external services.
 * Wraps HTTP status code and response body for proper error propagation.
 * <b>Class</b>: WebClientException
 * <b>Copyright</b>: 2025 Klab
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@Getter
public class WebClientException extends RuntimeException {

  private final HttpStatusCode statusCode;
  private final String responseBody;

  /**
   * Constructs a new WebClientException with the specified status code, message, and response body.
   *
   * @param statusCode   the HTTP status code from the external service
   * @param message      the detail message
   * @param responseBody the response body from the external service
   */

  public WebClientException(HttpStatusCode statusCode, String message, String responseBody) {
    super(message);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

}
