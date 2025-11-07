package com.urban.upark.services;

import com.urban.upark.models.*;
import com.urban.upark.repositories.*;
import com.urban.upark.dto.reservation.ReservationRequest;
import com.urban.upark.dto.reservation.PriceCalculationRequest;
import com.urban.upark.dto.reservation.VehicleSelection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingRepository parkingRepository;
    private final UsersRepository usersRepository;
    private final ReservationStatusRepository reservationStatusRepository;
    private final ReservationVehiclesRepository reservationVehiclesRepository;
    private final AnnouncementsVehiclesRepository announcementsVehiclesRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(int id) {
        return reservationRepository.findById(id);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteById(int id) {
        reservationRepository.deleteById(id);
    }

    public Reservation createReservation(ReservationRequest request) {
        // Vérifier que le parking existe
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        // Vérifier que l'utilisateur existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculer le prix total
        BigDecimal totalPrice = calculateTotalPrice(
                PriceCalculationRequest.builder()
                        .parkingId(request.getParkingId())
                        .startDateTime(request.getStartDateTime())
                        .endDateTime(request.getEndDateTime())
                        .selectedVehicles(request.getSelectedVehicles())
                        .build()
        );

        // Créer la réservation
        Reservation reservation = Reservation.builder()
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .totalPrice(totalPrice)
                .paymentMethod(request.getPaymentMethod())
                .creationDate(LocalDateTime.now())
                .user(user)
                .reservationStatus(getDefaultReservationStatus())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // Créer les réservations de véhicules
        if (request.getSelectedVehicles() != null) {
            for (VehicleSelection vehicleSelection : request.getSelectedVehicles()) {
                createReservationVehicles(savedReservation, vehicleSelection, request.getParkingId());
            }
        }

        return savedReservation;
    }

    public BigDecimal calculateTotalPrice(PriceCalculationRequest request) {
        // Récupérer le parking
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        // Calculer la durée en heures
        long hours = ChronoUnit.HOURS.between(request.getStartDateTime(), request.getEndDateTime());
        if (hours <= 0) {
            hours = 1; // Minimum 1 heure
        }

        // Calculer le nombre total de véhicules
        int totalVehicles = request.getSelectedVehicles() != null ? 
                request.getSelectedVehicles().stream().mapToInt(VehicleSelection::getQuantity).sum() : 1;

        // Prix total = tarif horaire * nombre d'heures * nombre de véhicules
        return parking.getHourlyRate().multiply(BigDecimal.valueOf(hours)).multiply(BigDecimal.valueOf(totalVehicles));
    }

    public List<Reservation> findByUserId(int userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByCreationDateDesc(userId);
        
        // Calculer les statuts automatiquement pour l'interface "Mes réservations"
        for (Reservation reservation : reservations) {
            updateReservationStatus(reservation);
        }
        
        return reservations;
    }
    
    private void updateReservationStatus(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        
        if (reservation.getStartDateTime().isAfter(now)) {
            // Statut : "Confirmée" (vert) - À venir (value_ = 20)
            reservation.setReservationStatus(getStatusByValue(20));
        } else if (reservation.getEndDateTime().isAfter(now)) {
            // Statut : "En cours" (bleu) - Actuellement utilisé (value_ = 15)
            reservation.setReservationStatus(getStatusByValue(15));
        } else {
            // Statut : "Terminée" (gris) - Passé (value_ = 30)
            reservation.setReservationStatus(getStatusByValue(30));
        }
    }
    
    private ReservationStatus getStatusByValue(int value) {
        // Chercher le statut par sa valeur dans la base
        return reservationStatusRepository.findByValue(value)
                .orElse(getDefaultReservationStatus());
    }

    public boolean checkAvailability(PriceCalculationRequest request) {
        // Logique simple : vérifier s'il n'y a pas de conflit de réservation
        // À améliorer avec la logique des availabilities
        List<Reservation> conflictingReservations = reservationRepository
                .findOverlappingReservations(request.getParkingId(), request.getStartDateTime(), request.getEndDateTime());
        
        return conflictingReservations.isEmpty();
    }

    private ReservationStatus getDefaultReservationStatus() {
        return reservationStatusRepository.findByValue(10)
                .orElse(ReservationStatus.builder().label("En attente").value(10).build());
    }

    private void createReservationVehicles(Reservation reservation, VehicleSelection vehicleSelection, int parkingId) {
        // Logique : trouver l'Announcements_vehicles correspondant au parking + type de véhicule
        List<AnnouncementsVehicles> announcementsVehicles = announcementsVehiclesRepository
                .findByParkingAndVehicleType(parkingId, vehicleSelection.getVehicleTypeId());
        
        AnnouncementsVehicles selectedAnnouncementVehicle = announcementsVehicles.stream()
                .findFirst()
                .orElse(null);
        
        ReservationVehicles reservationVehicles = ReservationVehicles.builder()
                .reservation(reservation)
                .numbers(vehicleSelection.getQuantity())
                .announcementsVehicles(selectedAnnouncementVehicle)
                .build();
        
        reservationVehiclesRepository.save(reservationVehicles);
    }
}