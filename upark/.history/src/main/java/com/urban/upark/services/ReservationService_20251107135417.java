package com.urban.upark.services;

import com.urban.upark.models.*;
import com.urban.upark.repositories.*;
import com.urban.upark.dto.reservation.ReservationRequest;
import com.urban.upark.dto.reservation.PriceCalculationRequest;
import com.urban.upark.dto.reservation.VehicleSelection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final AvailabilitiesDateRepository availabilitiesDateRepository;
    private final ParkingVehiclesRepository parkingVehiclesRepository;
    private final AvailabilitiesFrequenceRepository availabilitiesFrequenceRepository;

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
        // Vérifier la disponibilité en utilisant les tables availabilities
        return checkAvailabilityWithAvailabilities(request);
    }

    /**
     * Vérifie la disponibilité via Availabilities_date $ Availabilities_frequence
     */
    private boolean checkAvailabilityWithAvailabilities(PriceCalculationRequest request) {
        int parkingId = request.getParkingId();
        LocalDateTime startDateTime = request.getStartDateTime();
        LocalDateTime endDateTime = request.getEndDateTime();
        
        // Pour chaque type de véhicule demandé
        if (request.getSelectedVehicles() != null) {
            for (var vehicleSelection : request.getSelectedVehicles()) {
                if (!checkVehicleAvailability(parkingId, vehicleSelection.getVehicleTypeId(),
                        vehicleSelection.getQuantity(), startDateTime, endDateTime)) {
                    return false;
                }
            }
        } else {
            // Si pas de sélection spécifique, vérifier pour 1 véhicule par défaut
            if (!checkVehicleAvailability(parkingId, 1, 1, startDateTime, endDateTime)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Vérifie la disponibilité pour un type de véhicule spécifique
     */
    private boolean checkVehicleAvailability(int parkingId, int vehicleTypeId, int requiredQuantity,
                                           LocalDateTime startDateTime, LocalDateTime endDateTime) {
        
        // 1. Vérifier les disponibilités par dates spécifiques
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        
        List<AvailabilitiesDate> dateAvailabilities =
            availabilitiesDateRepository.findOverlappingAvailabilities(parkingId, startDate, endDate);
        
        // 2. Vérifier les disponibilités par fréquence (jours de la semaine)
        List<AvailabilitiesFrequence> frequencyAvailabilities =
            findFrequencyAvailabilities(parkingId, vehicleTypeId, startDateTime, endDateTime);
        
        // 3. Calculer la capacité disponible totale
        int totalAvailableCapacity = calculateAvailableCapacity(
            parkingId, vehicleTypeId, dateAvailabilities, frequencyAvailabilities,
            startDateTime, endDateTime
        );
        
        // 4. Vérifier si la capacité est suffisante
        return totalAvailableCapacity >= requiredQuantity;
    }

    /**
     * Trouve les disponibilités par fréquence pour une période donnée
     */
    private List<AvailabilitiesFrequence> findFrequencyAvailabilities(int parkingId, int vehicleTypeId,
                                                                     LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Récupérer les jours de la semaine dans la période
        List<Integer> dayOfWeekIds = getDayOfWeekIdsInRange(startDateTime, endDateTime);
        
        return dayOfWeekIds.stream()
                .flatMap(dayId -> availabilitiesFrequenceRepository.findByParkingAndDayOfWeek(parkingId, dayId).stream())
                .filter(af -> af.getAnnouncementsVehicles().getParkingVehicles().getVehicle().getId_Vehicles() == vehicleTypeId)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Extrait les IDs des jours de la semaine pour une période donnée
     */
    private List<Integer> getDayOfWeekIdsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Integer> dayIds = new java.util.ArrayList<>();
        LocalDate currentDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        
        while (!currentDate.isAfter(endDate)) {
            // DayOfWeek: 1=Monday, 7=Sunday (selon Java)
            // Adapter selon la base de données
            int dayId = currentDate.getDayOfWeek().getValue();
            dayIds.add(dayId);
            currentDate = currentDate.plusDays(1);
        }
        
        return dayIds;
    }

    /**
     * Vérifie si une disponibilité par fréquence couvre la période demandée
     */
    private boolean isFrequencyAvailable(AvailabilitiesFrequence af, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Vérifier si les heures de disponibilité couvrent la période demandée
        return !startDateTime.toLocalTime().isAfter(af.getEndHour()) &&
               !endDateTime.toLocalTime().isBefore(af.getStartHour());
    }

    /**
     * Calcule la capacité disponible totale
     */
    private int calculateAvailableCapacity(int parkingId, int vehicleTypeId,
                                         List<AvailabilitiesDate> dateAvailabilities,
                                         List<AvailabilitiesFrequence> frequencyAvailabilities,
                                         LocalDateTime startDateTime, LocalDateTime endDateTime) {
        
        // Capacité de base depuis Parking_vehicles
        int baseCapacity = parkingVehiclesRepository.findByParkingId(parkingId)
                .stream()
                .filter(pv -> pv.getVehicle().getId_Vehicles() == vehicleTypeId)
                .mapToInt(ParkingVehicles::getNumbers)
                .sum();
        
        if (baseCapacity == 0) {
            return 0; // Pas de capacité pour ce type de véhicule
        }
        
        // Vérifier les disponibilités spécifiques
        boolean hasDateAvailability = !dateAvailabilities.isEmpty() &&
            dateAvailabilities.stream().anyMatch(da ->
                isDateAvailabilityCoveringPeriod(da, startDateTime, endDateTime));
        
        boolean hasFrequencyAvailability = !frequencyAvailabilities.isEmpty() &&
            frequencyAvailabilities.stream().anyMatch(af ->
                isFrequencyAvailable(af, startDateTime, endDateTime));
        
        // Si aucune disponibilité définie, considérer comme non disponible
        if (!hasDateAvailability && !hasFrequencyAvailability) {
            return 0;
        }
        
        // Soustraire les réservations existantes
        int reservedCapacity = getReservedCapacity(parkingId, vehicleTypeId, startDateTime, endDateTime);
        
        return Math.max(0, baseCapacity - reservedCapacity);
    }

    /**
     * Vérifie si une disponibilité par date couvre la période
     */
    private boolean isDateAvailabilityCoveringPeriod(AvailabilitiesDate da, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime availabilityStart = da.getStartDate().atTime(da.getStartHour());
        LocalDateTime availabilityEnd = da.getEndDate().atTime(da.getEndHour());
        
        return !startDateTime.isAfter(availabilityEnd) && !endDateTime.isBefore(availabilityStart);
    }

    /**
     * Calcule le nombre de places déjà réservées
     */
    private int getReservedCapacity(int parkingId, int vehicleTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Logique pour compter les véhicules déjà réservés dans cette période
        // Pour l'instant, retourne 0 comme base - à implémenter selon les besoins
        return 0;
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