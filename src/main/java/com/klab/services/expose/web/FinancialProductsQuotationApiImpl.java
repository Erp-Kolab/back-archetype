package com.klab.services.expose.web;

import com.klab.services.backarchetype.services.LoanQuotationService;
import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.jboss.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserApi interface.
 * <b>Class</b>: UserApiImpl
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@RestController
@RequestMapping("/v1")
public class FinancialProductsQuotationApiImpl implements QuotationsApi {

  private static final Logger LOGGER = Logger.getLogger(FinancialProductsQuotationApiImpl.class);

  private final LoanQuotationService loanQuotationService;

  /**
   * Constructor for FinancialProductsQuotationApiImpl.
   *
   * @param loanQuotationService {@link LoanQuotationService} service for loan quotations
   */

  public FinancialProductsQuotationApiImpl(LoanQuotationService loanQuotationService) {
    this.loanQuotationService = loanQuotationService;
  }

  /**
   * Endpoint to quote a loan.
   *
   * @param loanQuotationRequest the loan quotation request
   * @param exchange             the server web exchange
   * @return a Mono of ResponseEntity containing LoanQuotationResponse
   */

  @Override
  public Mono<ResponseEntity<LoanQuotationResponse>> quoteLoan(
      @Parameter(name = "LoanQuotationRequest", required = true)
      @Valid @RequestBody Mono<LoanQuotationRequest> loanQuotationRequest,
      @Parameter(hidden = true) final ServerWebExchange exchange) {
    return loanQuotationService.quoteLoan(loanQuotationRequest, exchange)
        .doOnSubscribe(response -> LOGGER.info("Received loan quotation request"))
        .map(ResponseEntity::ok);
  }

}
