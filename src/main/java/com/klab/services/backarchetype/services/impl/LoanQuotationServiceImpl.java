package com.klab.services.backarchetype.services.impl;

import com.klab.services.backarchetype.domain.builder.LoanQuotationBuilder;
import com.klab.services.backarchetype.mapper.LoanQuotationMapper;
import com.klab.services.backarchetype.proxy.CurrencyExchangeProxy;
import com.klab.services.backarchetype.repository.LoanQuotationTraceRepository;
import com.klab.services.backarchetype.services.LoanQuotationService;
import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Implementation class for LoanQuotationService.
 * Follows Single Responsibility: orchestrates the flow, delegates calculations to helper.
 * <b>Class</b>: LoanQuotationServiceImpl
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Service
public class LoanQuotationServiceImpl implements LoanQuotationService {

  private static final Logger LOGGER = Logger.getLogger(LoanQuotationServiceImpl.class);

  private final CurrencyExchangeProxy currencyExchangeProxy;
  private final LoanQuotationTraceRepository traceRepository;
  private final LoanQuotationBuilder builder;
  private final LoanQuotationMapper mapper;

  /**
   * Constructor for LoanQuotationServiceImpl.
   *
   * @param currencyExchangeProxy the {@link CurrencyExchangeProxy}
   * @param traceRepository       the {@link LoanQuotationTraceRepository}
   * @param builder               the {@link LoanQuotationBuilder}
   * @param mapper                the {@link LoanQuotationMapper}
   */
  public LoanQuotationServiceImpl(CurrencyExchangeProxy currencyExchangeProxy,
                                  LoanQuotationTraceRepository traceRepository,
                                  LoanQuotationBuilder builder,
                                  LoanQuotationMapper mapper) {
    this.currencyExchangeProxy = currencyExchangeProxy;
    this.traceRepository = traceRepository;
    this.builder = builder;
    this.mapper = mapper;
  }

  @Override
  public Mono<LoanQuotationResponse> quoteLoan(Mono<LoanQuotationRequest> request,
                                               ServerWebExchange exchange) {
    return request
        .doOnNext(req ->
            LOGGER.infof("Processing loan quotation for DNI: %s", req.getDni()))
        .flatMap(this::processQuotation)
        .flatMap(this::saveTrace)
        .doOnSuccess(res ->
            LOGGER.infof("Loan quotation completed for DNI: %s", res.getCustomerDni()))
        .doOnError(error ->
            LOGGER.errorf("Error processing loan quotation: %s", error.getMessage()));
  }

  private Mono<LoanQuotationResponse> processQuotation(LoanQuotationRequest request) {
    return currencyExchangeProxy.getCurrencyExchange(request.getDni())
        .map(mapper::toExchangeRateInfo)
        .map(exchangeRate -> builder.build(request, exchangeRate));
  }

  private Mono<LoanQuotationResponse> saveTrace(LoanQuotationResponse response) {
    return traceRepository.save(mapper.toTrace(response))
        .doOnSuccess(trace -> LOGGER.infof("Trace saved with id: %s", trace.getId()))
        .thenReturn(response);
  }
}
