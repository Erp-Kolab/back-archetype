package com.klab.services.backarchetype.repository;

import com.klab.services.backarchetype.repository.entity.LoanQuotationTrace;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Reactive repository for LoanQuotationTrace entity.
 * <b>Interface</b>: LoanQuotationTraceRepository
 * <b>Company</b>: Klab
 *
 * @author Klab Dev Team
 */
@Repository
public interface LoanQuotationTraceRepository
    extends ReactiveCrudRepository<LoanQuotationTrace, Long> {
}
