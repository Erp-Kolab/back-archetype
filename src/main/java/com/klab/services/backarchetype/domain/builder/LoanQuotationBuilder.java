package com.klab.services.backarchetype.domain.builder;

import static com.klab.services.backarchetype.util.Constants.DECIMAL_SCALE;
import static com.klab.services.backarchetype.util.Constants.MONTHS_PER_YEAR;
import static com.klab.services.backarchetype.util.Constants.PERCENTAGE_DIVISOR;

import com.klab.services.model.api.ExchangeRateInfo;
import com.klab.services.model.api.LoanQuotationRequest;
import com.klab.services.model.api.LoanQuotationResponse;
import com.klab.services.model.api.LoanQuotationResponseLoanDetails;
import com.klab.services.model.api.LoanQuotationResponseMonthlyPayment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

/**
 * Builder for loan quotation response objects.
 * Single Responsibility: Handles object construction and financial calculations.
 * <b>Class</b>: LoanQuotationBuilder
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Component
public class LoanQuotationBuilder {

  /**
   * Builds complete loan quotation response.
   *
   * @param request      the loan request
   * @param exchangeRate the exchange rate info
   * @return LoanQuotationResponse
   */
  public LoanQuotationResponse build(LoanQuotationRequest request, ExchangeRateInfo exchangeRate) {
    if (request == null || exchangeRate == null) {
      throw new IllegalArgumentException("Request and exchange rate must not be null");
    }

    Double sellRate = exchangeRate.getSellRate();
    if (sellRate == null) {
      throw new IllegalArgumentException("Sell rate must not be null");
    }

    double totalUsd = calculateTotalPaymentUsd(
        request.getAmountUsd(), request.getAnnualInterestRate(), request.getTermMonths());
    double totalPen = convertToPen(totalUsd, sellRate);

    LoanQuotationResponse response = new LoanQuotationResponse();
    response.setCustomerDni(request.getDni());
    response.setLoanDetails(buildLoanDetails(request, sellRate));
    response.setExchangeRate(exchangeRate);
    response.setMonthlyPayment(buildPayment(
        calculateMonthlyPayment(totalUsd, request.getTermMonths()),
        calculateMonthlyPayment(totalPen, request.getTermMonths())));
    response.setTotalPayment(buildPayment(totalUsd, totalPen));
    response.setQuotationDate(OffsetDateTime.now());
    response.setValidUntil(OffsetDateTime.now().plusDays(1));
    return response;
  }

  private LoanQuotationResponseLoanDetails buildLoanDetails(LoanQuotationRequest request,
                                                            double sellRate) {
    LoanQuotationResponseLoanDetails details = new LoanQuotationResponseLoanDetails();
    details.setAmountUsd(request.getAmountUsd());
    details.setAmountPen(convertToPen(request.getAmountUsd(), sellRate));
    details.setTermMonths(request.getTermMonths());
    details.setAnnualInterestRate(request.getAnnualInterestRate());
    details.setMonthlyInterestRate(calculateMonthlyInterestRate(request.getAnnualInterestRate()));
    return details;
  }

  private LoanQuotationResponseMonthlyPayment buildPayment(double usd, double pen) {
    LoanQuotationResponseMonthlyPayment payment = new LoanQuotationResponseMonthlyPayment();
    payment.setAmountUsd(usd);
    payment.setAmountPen(pen);
    return payment;
  }

  private double calculateTotalPaymentUsd(double amountUsd, double annualInterestRate,
                                          int termMonths) {
    BigDecimal interestFactor = BigDecimal.valueOf(
        1 + (annualInterestRate / PERCENTAGE_DIVISOR) * (termMonths / MONTHS_PER_YEAR)
    );
    return BigDecimal.valueOf(amountUsd)
        .multiply(interestFactor)
        .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)
        .doubleValue();
  }

  private double convertToPen(double amountUsd, double sellRate) {
    return BigDecimal.valueOf(amountUsd)
        .multiply(BigDecimal.valueOf(sellRate))
        .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP)
        .doubleValue();
  }

  private double calculateMonthlyInterestRate(double annualRate) {
    return BigDecimal.valueOf(annualRate)
        .divide(BigDecimal.valueOf(MONTHS_PER_YEAR), DECIMAL_SCALE, RoundingMode.HALF_UP)
        .doubleValue();
  }

  private double calculateMonthlyPayment(double totalAmount, int termMonths) {
    return BigDecimal.valueOf(totalAmount)
        .divide(BigDecimal.valueOf(termMonths), DECIMAL_SCALE, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
