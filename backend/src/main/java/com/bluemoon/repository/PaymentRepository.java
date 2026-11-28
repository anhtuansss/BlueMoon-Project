package com.bluemoon.repository;

import com.bluemoon.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByObligationId(Long obligationId);
    List<Payment> findByApartmentId(Long apartmentId);
    List<Payment> findByDonationCampaignId(Long donationCampaignId);
    List<Payment> findAllByOrderByPaidAtDesc();
    List<Payment> findByApartmentIdOrderByPaidAtDesc(Long apartmentId);
    Optional<Payment> findFirstByObligationIdAndPaymentStatus(Long obligationId, Payment.PaymentStatus paymentStatus);
}
