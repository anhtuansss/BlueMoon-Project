package com.bluemoon.repository;

import com.bluemoon.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    Optional<PaymentRequest> findByPaymentReference(String paymentReference);
    Optional<PaymentRequest> findByObligationIdAndStatus(Long obligationId, PaymentRequest.RequestStatus status);
}
