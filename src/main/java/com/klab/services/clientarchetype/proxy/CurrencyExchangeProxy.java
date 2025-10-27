package com.klab.services.clientarchetype.proxy;

import com.klab.services.thirdparty.currencyexchangev1.proxy.CurrencyExchangeApi;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Proxy interface for Currency Exchange API with Circuit Breaker.
 * <b>Class</b>: CurrencyExchangeProxy
 * <b>Copyright</b>: 2025 Klab
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 * @version 1.0
 */

@FeignClient(name = "currency-exchange-api-v1",
             url = "${spring.rest-client.currency-exchange-api-v1.url}")
@CircuitBreaker(name = "currency-exchange")
public interface CurrencyExchangeProxy extends CurrencyExchangeApi {
}
