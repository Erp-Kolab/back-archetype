package com.klab.services.backarchetype.mapper;

import com.klab.services.backarchetype.repository.entity.LoanQuotationTrace;
import com.klab.services.model.api.ExchangeRateInfo;
import com.klab.services.model.api.LoanQuotationResponse;
import com.klab.services.thirdparty.currencyexchange.model.CurrencyExchangeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

/**
 * MapStruct mapper for loan quotation mappings.
 * <b>Interface</b>: LoanQuotationTraceMapper
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface LoanQuotationMapper {

  /**
   * Converts CurrencyExchangeResponse to ExchangeRateInfo.
   *
   * @param response the currency exchange response
   * @return ExchangeRateInfo
   */
  ExchangeRateInfo toExchangeRateInfo(CurrencyExchangeResponse response);

  /**
   * Converts LoanQuotationResponse to LoanQuotationTrace entity.
   *
   * @param response the loan quotation response
   * @return LoanQuotationTrace entity
   */
  @Mapping(source = "customerDni", target = "dni")
  @Mapping(source = "loanDetails.amountUsd", target = "amountUsd")
  @Mapping(source = "loanDetails.amountPen", target = "amountPen")
  @Mapping(source = "loanDetails.termMonths", target = "termMonths")
  @Mapping(source = "loanDetails.annualInterestRate", target = "annualInterestRate")
  @Mapping(source = "loanDetails.monthlyInterestRate", target = "monthlyInterestRate")
  @Mapping(source = "exchangeRate.buyRate", target = "exchangeRateBuy")
  @Mapping(source = "exchangeRate.sellRate", target = "exchangeRateSell")
  @Mapping(source = "exchangeRate.source", target = "exchangeRateSource")
  @Mapping(source = "totalPayment.amountUsd", target = "totalPaymentUsd")
  @Mapping(source = "totalPayment.amountPen", target = "totalPaymentPen")
  @Mapping(source = "monthlyPayment.amountUsd", target = "monthlyPaymentUsd")
  @Mapping(source = "monthlyPayment.amountPen", target = "monthlyPaymentPen")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "id", ignore = true)
  LoanQuotationTrace toTrace(LoanQuotationResponse response);
}

