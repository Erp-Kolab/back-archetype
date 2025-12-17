package com.klab.services.backarchetype.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.klab.services.backarchetype.domain.builder.LoanQuotationBuilder;
import com.klab.services.backarchetype.mapper.LoanQuotationMapper;
import com.klab.services.backarchetype.proxy.CurrencyExchangeProxy;
import com.klab.services.backarchetype.repository.LoanQuotationTraceRepository;
import com.klab.services.backarchetype.repository.entity.LoanQuotationTrace;
import com.klab.services.model.api.ExchangeRateInfo;
import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import com.klab.services.thirdparty.currencyexchange.model.CurrencyExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit tests for LoanQuotationServiceImpl.
 * <b>Class</b>: LoanQuotationServiceImplTest
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@ExtendWith(MockitoExtension.class)
class LoanQuotationServiceImplTest {

  private static final String TEST_DNI = "12345678";
  private static final Long TRACE_ID = 1L;

  @Mock
  private CurrencyExchangeProxy currencyExchangeProxy;

  @Mock
  private LoanQuotationTraceRepository traceRepository;

  @Mock
  private LoanQuotationBuilder builder;

  @Mock
  private LoanQuotationMapper mapper;

  @Mock
  private ServerWebExchange exchange;

  @InjectMocks
  private LoanQuotationServiceImpl loanQuotationService;

  private LoanQuotationRequest request;
  private LoanQuotationResponse response;
  private CurrencyExchangeResponse currencyExchangeResponse;
  private ExchangeRateInfo exchangeRateInfo;
  private LoanQuotationTrace trace;

  @BeforeEach
  void setUp() {
    request = createLoanQuotationRequest();
    response = createLoanQuotationResponse();
    currencyExchangeResponse = new CurrencyExchangeResponse();
    exchangeRateInfo = new ExchangeRateInfo();
    trace = createLoanQuotationTrace();
  }

  @Test
  @DisplayName("Should Return Loan Quotation Response When Processing Valid Request")
  void shouldReturnLoanQuotationResponseWhenProcessingValidRequest() {
    // Arrange
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenReturn(exchangeRateInfo);
    when(builder.build(request, exchangeRateInfo))
        .thenReturn(response);
    when(mapper.toTrace(response))
        .thenReturn(trace);
    when(traceRepository.save(trace))
        .thenReturn(Mono.just(trace));

    // Act
    Mono<LoanQuotationResponse> result =
        loanQuotationService.quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectNext(response)
        .verifyComplete();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verify(mapper).toExchangeRateInfo(currencyExchangeResponse);
    verify(builder).build(request, exchangeRateInfo);
    verify(mapper).toTrace(response);
    verify(traceRepository).save(trace);
  }

  @Test
  @DisplayName("Should Throw NullPointerException When Request Is Empty")
  void shouldThrowNullPointerExceptionWhenRequestIsEmpty() {
    // Arrange
    Mono<LoanQuotationRequest> emptyRequest = Mono.empty();

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService.quoteLoan(emptyRequest, exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(NullPointerException.class)
        .verify();

    verifyNoInteractions(currencyExchangeProxy, mapper, builder, traceRepository);
  }

  @Test
  @DisplayName("Should Propagate Error When Request Emits Error")
  void shouldPropagateErrorWhenRequestEmitsError() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Request error");

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.error(expectedException), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verifyNoInteractions(currencyExchangeProxy, mapper, builder, traceRepository);
  }

  @Test
  @DisplayName("Should Propagate Error When Currency Exchange Proxy Fails")
  void shouldPropagateErrorWhenCurrencyExchangeProxyFails() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Proxy error");
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.error(expectedException));

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verifyNoInteractions(builder, traceRepository);
    verify(mapper, never()).toExchangeRateInfo(any());
  }

  @Test
  @DisplayName("Should Throw NullPointerException When Currency Exchange Proxy Returns Empty")
  void shouldThrowNullPointerExceptionWhenCurrencyExchangeProxyReturnsEmpty() {
    // Arrange
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.empty());

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(NullPointerException.class)
        .verify();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verify(mapper, never()).toExchangeRateInfo(any());
  }

  @Test
  @DisplayName("Should Propagate Error When Mapper To Exchange Rate Info Fails")
  void shouldPropagateErrorWhenMapperToExchangeRateInfoFails() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Mapper error");
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenThrow(expectedException);

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verify(mapper).toExchangeRateInfo(currencyExchangeResponse);
    verifyNoInteractions(builder, traceRepository);
  }

  @Test
  @DisplayName("Should Propagate Error When Builder Fails")
  void shouldPropagateErrorWhenBuilderFails() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Builder error");
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenReturn(exchangeRateInfo);
    when(builder.build(request, exchangeRateInfo))
        .thenThrow(expectedException);

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verify(mapper).toExchangeRateInfo(currencyExchangeResponse);
    verify(builder).build(request, exchangeRateInfo);
    verifyNoInteractions(traceRepository);
  }

  @Test
  @DisplayName("Should Propagate Error When Mapper To Trace Fails")
  void shouldPropagateErrorWhenMapperToTraceFails() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Trace mapper error");
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenReturn(exchangeRateInfo);
    when(builder.build(request, exchangeRateInfo))
        .thenReturn(response);
    when(mapper.toTrace(response))
        .thenThrow(expectedException);

    // Act
    Mono<LoanQuotationResponse> result = loanQuotationService
        .quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(currencyExchangeProxy).getCurrencyExchange(TEST_DNI);
    verify(mapper).toExchangeRateInfo(currencyExchangeResponse);
    verify(builder).build(request, exchangeRateInfo);
    verify(mapper).toTrace(response);
    verifyNoInteractions(traceRepository);
  }

  @Test
  @DisplayName("Should Propagate Error When Repository Save Fails")
  void shouldPropagateErrorWhenRepositorySaveFails() {
    // Arrange
    RuntimeException expectedException = new RuntimeException("Database error");
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenReturn(exchangeRateInfo);
    when(builder.build(request, exchangeRateInfo))
        .thenReturn(response);
    when(mapper.toTrace(response))
        .thenReturn(trace);
    when(traceRepository.save(trace))
        .thenReturn(Mono.error(expectedException));

    // Act
    Mono<LoanQuotationResponse> result =
        loanQuotationService.quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(RuntimeException.class)
        .verify();

    verify(traceRepository).save(trace);
  }

  @Test
  @DisplayName("Should Throw NullPointerException When Repository Save Returns Empty")
  void shouldThrowNullPointerExceptionWhenRepositorySaveReturnsEmpty() {
    // Arrange
    when(currencyExchangeProxy.getCurrencyExchange(TEST_DNI))
        .thenReturn(Mono.just(currencyExchangeResponse));
    when(mapper.toExchangeRateInfo(currencyExchangeResponse))
        .thenReturn(exchangeRateInfo);
    when(builder.build(request, exchangeRateInfo))
        .thenReturn(response);
    when(mapper.toTrace(response))
        .thenReturn(trace);
    when(traceRepository.save(trace))
        .thenReturn(Mono.empty());

    // Act
    Mono<LoanQuotationResponse> result =
        loanQuotationService.quoteLoan(Mono.just(request), exchange);

    // Assert
    StepVerifier.create(result)
        .expectError(NullPointerException.class)
        .verify();

    verify(traceRepository).save(trace);
  }

  private LoanQuotationRequest createLoanQuotationRequest() {
    LoanQuotationRequest loanRequest = new LoanQuotationRequest();
    loanRequest.setDni(TEST_DNI);
    return loanRequest;
  }

  private LoanQuotationResponse createLoanQuotationResponse() {
    LoanQuotationResponse loanResponse = new LoanQuotationResponse();
    loanResponse.setCustomerDni(TEST_DNI);
    return loanResponse;
  }

  private LoanQuotationTrace createLoanQuotationTrace() {
    LoanQuotationTrace loanTrace = new LoanQuotationTrace();
    loanTrace.setId(TRACE_ID);
    loanTrace.setDni(TEST_DNI);
    return loanTrace;
  }

}
