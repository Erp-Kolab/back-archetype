package com.klab.services.config;

import com.klab.services.thirdparty.currencyexchange.client.ApiClient;
import com.klab.services.thirdparty.currencyexchange.proxy.ExchangeRateApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Currency Exchange API client beans.
 * <b>Class</b>: CurrencyExchangeClientConfig
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */

@Configuration
public class CurrencyExchangeClientConfiguration {

  /**
   * Creates an ExchangeRateApi bean configured with the specified base URL.
   *
   * @param baseUrl the base URL for the currency exchange API
   * @return an instance of {@link ExchangeRateApi}
   */

  @Bean
  public ExchangeRateApi exchangeRateApi(
      @Value("${spring.rest-client.currency-exchange-api-v1.url}") String baseUrl) {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(baseUrl);
    return new ExchangeRateApi(apiClient);
  }

}
