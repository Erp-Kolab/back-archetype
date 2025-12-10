package com.klab.services.backarchetype.proxy.impl;

import com.klab.services.backarchetype.exception.WebClientException;
import com.klab.services.backarchetype.proxy.CurrencyExchangeProxy;
import com.klab.services.thirdparty.currencyexchange.model.CurrencyExchangeResponse;
import com.klab.services.thirdparty.currencyexchange.proxy.ExchangeRateApi;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Implementation of Currency Exchange Proxy.
 * <b>Class</b>: CurrencyExchangeProxyImpl
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@Component
public class CurrencyExchangeProxyImpl implements CurrencyExchangeProxy {

  private static final Logger LOGGER = Logger.getLogger(CurrencyExchangeProxyImpl.class);

  private final ExchangeRateApi exchangeRateApi;

  /**
   * Constructor for CurrencyExchangeProxyImpl.
   *
   * @param exchangeRateApi the exchange rate API client
   */

  public CurrencyExchangeProxyImpl(ExchangeRateApi exchangeRateApi) {
    this.exchangeRateApi = exchangeRateApi;
  }

  @Override
  @CircuitBreaker(name = "currency-exchange")
  public Mono<CurrencyExchangeResponse> getCurrencyExchange(String dni) {
    LOGGER.infof("Calling currency exchange API for DNI: %s", dni);
    return exchangeRateApi.getCurrencyExchange(dni)
        .onErrorMap(WebClientResponseException.class, ex ->
            new WebClientException(
                ex.getStatusCode(),
                ex.getStatusText(),
                ex.getResponseBodyAsString()
            )
        );
  }

}
