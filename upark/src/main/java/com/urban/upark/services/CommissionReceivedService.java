package com.urban.upark.services;

import com.urban.upark.models.*;
import com.urban.upark.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommissionReceivedService {

    private final CommissionReceivedRepository commissionReceivedRepository;
    private final GlobalCommissionRepository globalCommissionRepository;
    private final CommissionTypesRepository commissionTypesRepository;
    private final PaymentStatusRepository paymentStatusRepository;

    public List<CommissionReceived> findAll() {
        return commissionReceivedRepository.findAll();
    }

    public Optional<CommissionReceived> findById(int id) {
        return commissionReceivedRepository.findById(id);
    }

    public CommissionReceived save(CommissionReceived commissionReceived) {
        return commissionReceivedRepository.save(commissionReceived);
    }

    public void deleteById(int id) {
        commissionReceivedRepository.deleteById(id);
    }

    /**
     * Créer une commission pour une réservation
     * Utilise le taux de commission globale
     */
    public CommissionReceived createCommissionForReservation(Reservation reservation) {
        try {
            // Récupérer le taux de commission globale (le plus récent)
            List<GlobalCommission> globalCommissions = globalCommissionRepository.findAll();
            if (globalCommissions.isEmpty()) {
                System.err.println(" WARNING: No global commission rate configured!");
                return null;
            }

            // Prendre la commission la plus récente
            GlobalCommission globalCommission = globalCommissions.get(globalCommissions.size() - 1);
            BigDecimal commissionRate = globalCommission.getRate();

            // Calculer le montant de la commission
            BigDecimal commissionAmount = reservation.getTotalPrice()
                    .multiply(commissionRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            System.out.println("💰 Calculating commission:");
            System.out.println("  - Reservation price: " + reservation.getTotalPrice());
            System.out.println("  - Commission rate: " + commissionRate + "%");
            System.out.println("  - Commission amount: " + commissionAmount);

            // Récupérer le type de commission par défaut (normalement ID 1 pour "globale")
            Optional<CommissionTypes> commissionTypeOpt = commissionTypesRepository.findById(1);
            if (!commissionTypeOpt.isPresent()) {
                System.err.println(" WARNING: Commission type not found!");
                return null;
            }

            // Récupérer le statut de paiement "En attente" (normalement ID 1)
            Optional<PaymentStatus> paymentStatusOpt = paymentStatusRepository.findById(1);
            if (!paymentStatusOpt.isPresent()) {
                System.err.println(" WARNING: Payment status not found!");
                return null;
            }

            // Créer la commission
            CommissionReceived commission = CommissionReceived.builder()
                    .price(commissionAmount)
                    .paymentDate(LocalDateTime.now())
                    .commissionType(commissionTypeOpt.get())
                    .reservation(reservation)
                    .paymentStatus(paymentStatusOpt.get())
                    .build();

            CommissionReceived savedCommission = commissionReceivedRepository.save(commission);
            System.out.println(" Commission created with ID: " + savedCommission.getId_Commission_received());

            return savedCommission;

        } catch (Exception e) {
            System.err.println("Erreur: ERROR creating commission: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Récupère le montant total des commissions pour une réservation donnée
     */
    public BigDecimal getTotalCommissionByReservation(int reservationId) {
        List<CommissionReceived> commissions = commissionReceivedRepository
                .findByReservationId(reservationId);
        
        return commissions.stream()
                .map(CommissionReceived::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}