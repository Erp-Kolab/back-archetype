package com.klab.services.backarchetype.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Global exception handler for REST controllers.
 * <b>Class</b>: GlobalExceptionHandler
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles WebClientException and propagates the error to the client.
   *
   * @param ex the WebClientException
   * @return ResponseEntity with error details and original status code
   */

  @ExceptionHandler(WebClientException.class)
  public ResponseEntity<String> handleWebClientException(WebClientException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBody());
  }

  /**
   * Handles validation errors from request body.
   *
   * @param ex the WebExchangeBindException
   * @return ResponseEntity with validation error details
   */

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
      WebExchangeBindException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("error", "Validation failed");

    List<Map<String, String>> errors = ex.getFieldErrors().stream()
        .map(fieldError -> {
          Map<String, String> errorDetail = new HashMap<>();
          errorDetail.put("field", fieldError.getField());
          errorDetail.put("rejected_value", String.valueOf(fieldError.getRejectedValue()));
          errorDetail.put("message", fieldError.getDefaultMessage());
          return errorDetail;
        })
        .toList();

    body.put("details", errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }
}
