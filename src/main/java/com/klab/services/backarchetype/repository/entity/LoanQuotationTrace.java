package com.klab.services.backarchetype.repository.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity for storing loan quotation traces.
 * <b>Class</b>: LoanQuotationTrace
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Getter
@Setter
@Table("loan_quotation_trace")
public class LoanQuotationTrace {

  @Id
  private Long id;

  @Column("dni")
  private String dni;

  @Column("amount_usd")
  private Double amountUsd;

  @Column("amount_pen")
  private Double amountPen;

  @Column("term_months")
  private Integer termMonths;

  @Column("annual_interest_rate")
  private Double annualInterestRate;

  @Column("monthly_interest_rate")
  private Double monthlyInterestRate;

  @Column("exchange_rate_buy")
  private Double exchangeRateBuy;

  @Column("exchange_rate_sell")
  private Double exchangeRateSell;

  @Column("exchange_rate_source")
  private String exchangeRateSource;

  @Column("total_payment_usd")
  private Double totalPaymentUsd;

  @Column("total_payment_pen")
  private Double totalPaymentPen;

  @Column("monthly_payment_usd")
  private Double monthlyPaymentUsd;

  @Column("monthly_payment_pen")
  private Double monthlyPaymentPen;

  @Column("created_at")
  private LocalDateTime createdAt;

}

