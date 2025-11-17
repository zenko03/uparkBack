package com.urban.upark.services;

import com.urban.upark.models.*;
import com.urban.upark.repositories.*;
import com.urban.upark.dto.reservation.ReservationRequest;
import com.urban.upark.dto.reservation.ReservationResponse;
import com.urban.upark.dto.reservation.PriceCalculationRequest;
import com.urban.upark.dto.reservation.VehicleSelection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CommissionReceivedService commissionReceivedService;

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

    /**
     * Annuler une réservation en changeant son statut à "Annulée" (25)
     * 
     * @param id L'ID de la réservation à annuler
     * @return La réservation annulée
     */
    public Optional<Reservation> cancelReservation(int id) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            
            // Récupérer le statut "Annulée" (25)
            ReservationStatus canceledStatus = getStatusByValue(25);
            reservation.setReservationStatus(canceledStatus);
            
            Reservation savedReservation = reservationRepository.save(reservation);
            return Optional.of(savedReservation);
        }
        
        return Optional.empty();
    }

    /**
     * Mettre à jour le statut d'une réservation manuellement
     * 
     * @param reservationId L'ID de la réservation
     * @param statusId L'ID du nouveau statut
     * @return La réservation mise à jour
     */
    public Optional<Reservation> updateStatus(int reservationId, int statusId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        Optional<ReservationStatus> statusOpt = reservationStatusRepository.findById(statusId);
        
        if (reservationOpt.isPresent() && statusOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            ReservationStatus newStatus = statusOpt.get();
            
            reservation.setReservationStatus(newStatus);
            
            Reservation savedReservation = reservationRepository.save(reservation);
            return Optional.of(savedReservation);
        }
        
        return Optional.empty();
    }

    public Reservation createReservation(ReservationRequest request) {
        System.out.println("📝 Creating reservation...");
        System.out.println("  - Parking ID: " + request.getParkingId());
        System.out.println("  - User ID: " + request.getUserId());
        System.out.println("  - Start: " + request.getStartDateTime());
        System.out.println("  - End: " + request.getEndDateTime());
        System.out.println("  - Selected Vehicles: " + (request.getSelectedVehicles() != null ? request.getSelectedVehicles().size() : 0));
        
        // Vérifier que le parking existe
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        System.out.println("  - Parking found: " + parking.getLabel());

        // Vérifier que l'utilisateur existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("  - User found: " + user.getFirst_name());

        // Vérifier la disponibilité (horaires et capacité)
        PriceCalculationRequest availabilityRequest = PriceCalculationRequest.builder()
                .parkingId(request.getParkingId())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .selectedVehicles(request.getSelectedVehicles())
                .build();
        
        if (!checkAvailability(availabilityRequest)) {
            throw new RuntimeException("Le parking n'est pas disponible pour la période sélectionnée ou les horaires sont en dehors des heures d'ouverture");
        }
        
        System.out.println("  - Availability checked: OK");

        // Calculer le prix total
        BigDecimal totalPrice = calculateTotalPrice(
                PriceCalculationRequest.builder()
                        .parkingId(request.getParkingId())
                        .startDateTime(request.getStartDateTime())
                        .endDateTime(request.getEndDateTime())
                        .selectedVehicles(request.getSelectedVehicles())
                        .build()
        );

        System.out.println("  - Calculated price: " + totalPrice);

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
        System.out.println("✅ Reservation saved with ID: " + savedReservation.getId_Reservation());

        // Créer les réservations de véhicules
        if (request.getSelectedVehicles() != null && !request.getSelectedVehicles().isEmpty()) {
            System.out.println("🚗 Creating vehicle reservations...");
            for (VehicleSelection vehicleSelection : request.getSelectedVehicles()) {
                createReservationVehicles(savedReservation, vehicleSelection, request.getParkingId());
            }
        } else {
            System.err.println("⚠️ WARNING: No vehicles selected!");
        }

        // Créer la commission pour cette réservation
        System.out.println("💰 Creating commission for reservation...");
        commissionReceivedService.createCommissionForReservation(savedReservation);

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

    /**
     * Convertir une réservation en ReservationResponse avec les infos du parking
     */
    public List<ReservationResponse> findByUserIdWithParkingInfo(int userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdWithParkingInfo(userId);
        
        return reservations.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convertir Reservation en ReservationResponse
     */
    private ReservationResponse convertToResponse(Reservation reservation) {
        // Mettre à jour le statut
        updateReservationStatus(reservation);
        
        // Récupérer les informations du parking via ReservationVehicles
        Parking parking = getParkingFromReservation(reservation);
        
        ReservationResponse.ParkingInfo parkingInfo = null;
        if (parking != null) {
            parkingInfo = ReservationResponse.ParkingInfo.builder()
                .id(parking.getId_Parking())
                .name(parking.getLabel())
                .address(parking.getLocalisation())
                .city("") // À compléter si besoin
                .zipCode("") // À compléter si besoin
                .build();
        }
        
        return ReservationResponse.builder()
            .id(reservation.getId_Reservation())
            .totalPrice(reservation.getTotalPrice())
            .creationDate(reservation.getCreationDate())
            .paymentDate(reservation.getPaymentDate())
            .startDateTime(reservation.getStartDateTime())
            .endDateTime(reservation.getEndDateTime())
            .paymentMethod(reservation.getPaymentMethod())
            .status(reservation.getReservationStatus() != null ? 
                    reservation.getReservationStatus().getLabel() : "UNKNOWN")
            .parking(parkingInfo)
            .build();
    }

    /**
     * Récupérer le parking associé à une réservation
     */
    private Parking getParkingFromReservation(Reservation reservation) {
        if (reservation.getReservationVehicles() != null && !reservation.getReservationVehicles().isEmpty()) {
            ReservationVehicles rv = reservation.getReservationVehicles().get(0);
            if (rv.getAnnouncementsVehicles() != null && 
                rv.getAnnouncementsVehicles().getParkingVehicles() != null) {
                return rv.getAnnouncementsVehicles().getParkingVehicles().getParking();
            }
        }
        return null;
    }

    /**
     * Recherche avancée de réservations avec filtres multiples
     */
    public List<Reservation> findReservationsWithFilters(Integer statusId, Integer userId,
                                                        Integer parkingId, LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        // Si les dates sont fournies, utiliser la méthode avec dates
        if (startDate != null || endDate != null) {
            return reservationRepository.findReservationsWithDateFilters(statusId, userId, parkingId, startDate, endDate);
        }
        // Sinon utiliser la version simplifiée pour éviter les problèmes de type
        return reservationRepository.findReservationsWithBasicFilters(statusId, userId, parkingId);
    }

    /**
     * Recherche de réservations par plage de dates (période de réservation)
     */
    public List<Reservation> findReservationsByDateRange(Integer statusId, LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        return reservationRepository.findReservationsByDateRange(statusId, startDate, endDate);
    }
    
    private void updateReservationStatus(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        
        // Statuts selon le fichier SQL :
        // 'à venir' (10) : Réservation confirmée mais pas encore commencée
        // 'En cours' (15) : Réservation active actuellement
        // 'Terminée' (20) : Réservation terminée
        // 'Annulée' (25) : Réservation annulée (non géré ici, uniquement manuel)
        
        if (reservation.getStartDateTime().isAfter(now)) {
            // La réservation n'a pas encore commencé → "à venir"
            reservation.setReservationStatus(getStatusByValue(10));
        } else if (reservation.getEndDateTime().isAfter(now)) {
            // La réservation est en cours → "En cours"
            reservation.setReservationStatus(getStatusByValue(15));
        } else {
            // La réservation est terminée → "Terminée"
            reservation.setReservationStatus(getStatusByValue(20));
        }
    }
    
    private ReservationStatus getStatusByValue(int value) {
        // Chercher le statut par sa valeur dans la base
        return reservationStatusRepository.findByValue(value)
                .orElseGet(() -> {
                    // Créer et sauvegarder le statut s'il n'existe pas
                    String label = switch (value) {
                        case 10 -> "à venir";
                        case 15 -> "En cours";
                        case 20 -> "Terminée";
                        case 25 -> "Annulée";
                        default -> "à venir";
                    };
                    ReservationStatus status = ReservationStatus.builder()
                            .label(label)
                            .value(value)
                            .build();
                    return reservationStatusRepository.save(status);
                });
    }

    public boolean checkAvailability(PriceCalculationRequest request) {
        // Vérifier la disponibilité en utilisant les tables availabilities
        return checkAvailabilityWithAvailabilities(request);
    }

    /**
     * Vérifie la disponibilité via Availabilities_date _ Availabilities_frequence
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
        // Vérifier si les heures de disponibilité couvrent complètement la période demandée
        java.time.LocalTime requestStartTime = startDateTime.toLocalTime();
        java.time.LocalTime requestEndTime = endDateTime.toLocalTime();
        java.time.LocalTime availStartTime = af.getStartHour();
        java.time.LocalTime availEndTime = af.getEndHour();
        
        // La période de réservation doit être complètement dans les horaires d'ouverture
        boolean startTimeValid = !requestStartTime.isBefore(availStartTime);
        boolean endTimeValid = !requestEndTime.isAfter(availEndTime);
        
        System.out.println("🕐 Vérification horaire: " + requestStartTime + "-" + requestEndTime + 
                          " vs " + availStartTime + "-" + availEndTime + 
                          " => Start OK: " + startTimeValid + ", End OK: " + endTimeValid);
        
        return startTimeValid && endTimeValid;
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
        // Statut par défaut : "à venir" (10) pour une nouvelle réservation
        return reservationStatusRepository.findByValue(10)
                .orElseGet(() -> {
                    // Créer et sauvegarder le statut par défaut s'il n'existe pas
                    ReservationStatus defaultStatus = ReservationStatus.builder()
                            .label("à venir")
                            .value(10)
                            .build();
                    return reservationStatusRepository.save(defaultStatus);
                });
    }

    private void createReservationVehicles(Reservation reservation, VehicleSelection vehicleSelection, int parkingId) {
        try {
            System.out.println("🚗 Creating reservation vehicle: parkingId=" + parkingId + 
                              ", vehicleTypeId=" + vehicleSelection.getVehicleTypeId() + 
                              ", quantity=" + vehicleSelection.getQuantity());
            
            // Essayer d'abord la requête JPQL
            List<AnnouncementsVehicles> announcementsVehicles = announcementsVehiclesRepository
                    .findByParkingAndVehicleType(parkingId, vehicleSelection.getVehicleTypeId());
            
            System.out.println("✅ Found announcementsVehicles (JPQL): " + announcementsVehicles.size());
            
            AnnouncementsVehicles selectedAnnouncementVehicle = null;
            
            if (!announcementsVehicles.isEmpty()) {
                selectedAnnouncementVehicle = announcementsVehicles.get(0);
            } else {
                // Solution de secours : utiliser la requête SQL native
                System.out.println("⚠️ JPQL returned empty, trying native query...");
                selectedAnnouncementVehicle = announcementsVehiclesRepository
                        .findByParkingAndVehicleTypeNative(parkingId, vehicleSelection.getVehicleTypeId());
            }
            
            if (selectedAnnouncementVehicle == null) {
                System.err.println("❌ No AnnouncementsVehicles found for parkingId=" + parkingId + 
                                  " and vehicleTypeId=" + vehicleSelection.getVehicleTypeId());
                System.err.println("❌ This reservation will be created WITHOUT vehicle link!");
                return;
            }
            
            System.out.println("✅ Selected AnnouncementsVehicles ID: " + selectedAnnouncementVehicle.getId_Announcements_vehicles());
            
            ReservationVehicles reservationVehicles = ReservationVehicles.builder()
                    .reservation(reservation)
                    .numbers(vehicleSelection.getQuantity())
                    .announcementsVehicles(selectedAnnouncementVehicle)
                    .build();
            
            ReservationVehicles saved = reservationVehiclesRepository.save(reservationVehicles);
            System.out.println("✅ Saved ReservationVehicles with ID: " + saved.getId_Reservation_vehicles());
        } catch (Exception e) {
            System.err.println("❌ Error creating reservation vehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}