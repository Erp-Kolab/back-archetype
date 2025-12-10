package com.klab.services.backarchetype.services;

import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Service interface for handling loan quotations.
 * <b>Interface</b>: LoanQuotationService
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

public interface LoanQuotationService {

  /**
   * Method to quote a loan based on the provided request.
   *
   * @param request  Mono of {@link LoanQuotationRequest} containing loan details
   * @param exchange the server web exchange
   * @return Mono of {@link LoanQuotationResponse} with the quotation result
   */

  Mono<LoanQuotationResponse> quoteLoan(Mono<LoanQuotationRequest> request,
                                        ServerWebExchange exchange);

}
