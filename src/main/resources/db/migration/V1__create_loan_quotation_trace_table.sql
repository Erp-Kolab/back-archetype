-- Create loan_quotation_trace table for storing quotation history
CREATE TABLE IF NOT EXISTS loan_quotation_trace (
    id BIGSERIAL PRIMARY KEY,
    dni VARCHAR(8) NOT NULL,
    amount_usd DECIMAL(12, 2) NOT NULL,
    amount_pen DECIMAL(12, 2) NOT NULL,
    term_months INTEGER NOT NULL,
    annual_interest_rate DECIMAL(5, 2) NOT NULL,
    monthly_interest_rate DECIMAL(10, 8) NOT NULL,
    exchange_rate_buy DECIMAL(6, 4) NOT NULL,
    exchange_rate_sell DECIMAL(6, 4) NOT NULL,
    exchange_rate_source VARCHAR(50) NOT NULL,
    total_payment_usd DECIMAL(12, 2) NOT NULL,
    total_payment_pen DECIMAL(12, 2) NOT NULL,
    monthly_payment_usd DECIMAL(12, 2) NOT NULL,
    monthly_payment_pen DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster queries by DNI
CREATE INDEX idx_loan_quotation_trace_dni ON loan_quotation_trace(dni);

-- Index for queries by date
CREATE INDEX idx_loan_quotation_trace_created_at ON loan_quotation_trace(created_at);
