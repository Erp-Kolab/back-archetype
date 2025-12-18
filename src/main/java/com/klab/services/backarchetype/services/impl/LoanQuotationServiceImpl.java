package com.klab.services.backarchetype.services.impl;

import com.klab.core.starter.audit.model.avro.AvroAudit;
import com.klab.services.backarchetype.domain.builder.LoanQuotationBuilder;
import com.klab.services.backarchetype.mapper.LoanQuotationMapper;
import com.klab.services.backarchetype.messaging.AuditProducer;
import com.klab.services.backarchetype.proxy.CurrencyExchangeProxy;
import com.klab.services.backarchetype.repository.LoanQuotationTraceRepository;
import com.klab.services.backarchetype.services.LoanQuotationService;
import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import java.util.Map;
import java.util.UUID;
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
  private final AuditProducer auditProducer;

  /**
   * Constructor for LoanQuotationServiceImpl.
   *
   * @param currencyExchangeProxy the {@link CurrencyExchangeProxy}
   * @param traceRepository       the {@link LoanQuotationTraceRepository}
   * @param builder               the {@link LoanQuotationBuilder}
   * @param mapper                the {@link LoanQuotationMapper}
   * @param auditProducer         the {@link AuditProducer}
   */
  public LoanQuotationServiceImpl(CurrencyExchangeProxy currencyExchangeProxy,
                                  LoanQuotationTraceRepository traceRepository,
                                  LoanQuotationBuilder builder,
                                  LoanQuotationMapper mapper,
                                  AuditProducer auditProducer) {
    this.currencyExchangeProxy = currencyExchangeProxy;
    this.traceRepository = traceRepository;
    this.builder = builder;
    this.mapper = mapper;
    this.auditProducer = auditProducer;
  }

  @Override
  public Mono<LoanQuotationResponse> quoteLoan(Mono<LoanQuotationRequest> request,
                                               ServerWebExchange exchange) {
    return request
        .doOnNext(req ->
            LOGGER.infof("Processing loan quotation for DNI: %s", req.getDni()))
        .flatMap(this::processQuotation)
        .flatMap(this::saveTrace)
        .flatMap(this::sendAudit)
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

  private Mono<LoanQuotationResponse> sendAudit(LoanQuotationResponse response) {
    String eventId = UUID.randomUUID().toString();
    String requestDate = response.getQuotationDate().toString();
    Map<String, String> data = Map.of(
        "customerDni", response.getCustomerDni(),
        "sellRate", String.valueOf(response.getExchangeRate().getSellRate()),
        "buyRate", String.valueOf(response.getExchangeRate().getSellRate())
    );
    AvroAudit auditEvent = new AvroAudit(eventId, requestDate, data);

    return auditProducer.send(auditEvent)
        .doOnSuccess(v -> LOGGER.infof("Audit event sent for DNI: %s", response.getCustomerDni()))
        .thenReturn(response);
  }

}
