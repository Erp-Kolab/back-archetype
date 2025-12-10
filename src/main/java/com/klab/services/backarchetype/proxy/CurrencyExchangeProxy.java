package com.klab.services.backarchetype.proxy;

import com.klab.services.thirdparty.currencyexchange.model.CurrencyExchangeResponse;
import reactor.core.publisher.Mono;

/**
 * Proxy interface for Currency Exchange operations.
 * <b>Class</b>: CurrencyExchangeProxy
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

public interface CurrencyExchangeProxy {

  /**
   * Retrieves currency exchange information based on the provided DNI.
   *
   * @param dni The DNI identifier for which to retrieve currency exchange information.
   * @return A Mono emitting the {@link CurrencyExchangeResponse}.
   */

  Mono<CurrencyExchangeResponse> getCurrencyExchange(String dni);

}
