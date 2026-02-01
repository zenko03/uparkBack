package com.urban.upark.services;

import com.urban.upark.models.*;
import com.urban.upark.repositories.*;
import com.urban.upark.dto.reservation.ReservationRequest;
import com.urban.upark.dto.reservation.ReservationResponse;
import com.urban.upark.dto.reservation.PriceCalculationRequest;
import com.urban.upark.dto.reservation.VehicleSelection;
import com.urban.upark.dto.reservation.ReservationDetailDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

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
    private final QRCodeService qrCodeService;
    private final NotificationService notificationService;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
    
    // get all reservations with details
    public List<ReservationDetailDTO> findAllWithDetails() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<ReservationDetailDTO> detailDTOs = new ArrayList<>();
        
        for (Reservation reservation : reservations) {
            ReservationDetailDTO dto = mapToDetailDTO(reservation);
            detailDTOs.add(dto);
        }
        
        return detailDTOs;
    }
    
    private ReservationDetailDTO mapToDetailDTO(Reservation reservation) {
        ReservationDetailDTO.ParkingInfo parkingInfo = extractParkingInfo(reservation);
        BigDecimal commission = commissionReceivedService.getTotalCommissionByReservation(reservation.getId_Reservation());
        
        return ReservationDetailDTO.builder()
                .idReservation(reservation.getId_Reservation())
                .totalPrice(reservation.getTotalPrice())
                .creationDate(reservation.getCreationDate())
                .paymentDate(reservation.getPaymentDate())
                .startDateTime(reservation.getStartDateTime())
                .endDateTime(reservation.getEndDateTime())
                .paymentMethod(reservation.getPaymentMethod())
                .user(ReservationDetailDTO.UserInfo.builder()
                        .idUsers(reservation.getUser().getId_Users())
                        .firstName(reservation.getUser().getFirst_name())
                        .lastName(reservation.getUser().getName())
                        .email(reservation.getUser().getEmail())
                        .telephone(reservation.getUser().getPhone_number())
                        .build())
                .parking(parkingInfo)
                .reservationStatus(ReservationDetailDTO.ReservationStatusInfo.builder()
                        .idReservationStatus(reservation.getReservationStatus().getId_Reservation_status())
                        .label(reservation.getReservationStatus().getLabel())
                        .value(reservation.getReservationStatus().getValue())
                        .build())
                .commission(commission)
                .build();
    }
    
    private ReservationDetailDTO.ParkingInfo extractParkingInfo(Reservation reservation) {
        try {
            if (reservation.getReservationVehicles() != null && !reservation.getReservationVehicles().isEmpty()) {
                ReservationVehicles rv = reservation.getReservationVehicles().get(0);
                AnnouncementsVehicles av = rv.getAnnouncementsVehicles();
                
                if (av != null && av.getParkingVehicles() != null) {
                    ParkingVehicles pv = av.getParkingVehicles();
                    Parking parking = pv.getParking();
                    
                    if (parking != null) {
                        Users owner = parking.getUser();
                        
                        return ReservationDetailDTO.ParkingInfo.builder()
                                .idParking(parking.getId_Parking())
                                .name(parking.getLabel())
                                .address(parking.getAddress())
                                .idOwner(owner != null ? owner.getId_Users() : 0)
                                .ownerName(owner != null ? owner.getFirst_name() + " " + owner.getName() : "")
                                .build();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting parking info for reservation {}: {}", reservation.getId_Reservation(), e.getMessage());
        }
        
        return ReservationDetailDTO.ParkingInfo.builder()
                .idParking(0)
                .name("Parking non trouve")
                .address("")
                .idOwner(0)
                .ownerName("")
                .build();
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

    // cancel reservation - status 25
    public Optional<Reservation> cancelReservation(int id) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            
            ReservationStatus canceledStatus = getStatusByValue(25);
            reservation.setReservationStatus(canceledStatus);
            
            Reservation savedReservation = reservationRepository.save(reservation);
            
            // send cancellation notification
            try {
                Integer clientId = reservation.getUser().getId_Users();
                String parkingName = "votre parking";
                
                try {
                    List<ReservationVehicles> resVehicles = reservationVehiclesRepository.findByReservationId(id);
                    if (!resVehicles.isEmpty() && resVehicles.get(0).getAnnouncementsVehicles() != null) {
                        parkingName = resVehicles.get(0).getAnnouncementsVehicles()
                                .getAnnouncements().getParking().getLabel();
                    }
                } catch (Exception e) {
                    log.warn("Could not get parking name: {}", e.getMessage());
                }
                
                Map<String, String> clientNotifData = new HashMap<>();
                clientNotifData.put("type", "reservation_cancelled");
                clientNotifData.put("reservationId", String.valueOf(savedReservation.getId_Reservation()));
                clientNotifData.put("parkingName", parkingName);
                
                notificationService.sendPushNotification(
                    clientId,
                    "Reservation annulee",
                    "Votre reservation pour " + parkingName + " a ete annulee.",
                    "reservation_cancelled",
                    clientNotifData
                );
            } catch (Exception e) {
                log.error("Error sending cancellation notification: {}", e.getMessage());
            }
            
            return Optional.of(savedReservation);
        }
        
        return Optional.empty();
    }

    // update status manually
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
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PriceCalculationRequest availabilityRequest = PriceCalculationRequest.builder()
                .parkingId(request.getParkingId())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .selectedVehicles(request.getSelectedVehicles())
                .build();
        
        if (!checkAvailability(availabilityRequest)) {
            throw new RuntimeException("Le parking n'est pas disponible pour la periode selectionnee");
        }

        BigDecimal totalPrice = calculateTotalPrice(
                PriceCalculationRequest.builder()
                        .parkingId(request.getParkingId())
                        .startDateTime(request.getStartDateTime())
                        .endDateTime(request.getEndDateTime())
                        .selectedVehicles(request.getSelectedVehicles())
                        .build()
        );

        Reservation reservation = Reservation.builder()
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .totalPrice(totalPrice)
                .paymentMethod(request.getPaymentMethod())
                .creationDate(LocalDateTime.now())
                .user(user)
                .build();
        
        updateReservationStatus(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        
        // generate QR token
        String qrToken = qrCodeService.generateFormattedQRToken(savedReservation.getId_Reservation());
        savedReservation.setQrCodeToken(qrToken);
        savedReservation.setIsValidated(false);
        savedReservation = reservationRepository.save(savedReservation);

        // create vehicle reservations
        if (request.getSelectedVehicles() != null && !request.getSelectedVehicles().isEmpty()) {
            for (VehicleSelection vehicleSelection : request.getSelectedVehicles()) {
                createReservationVehicles(savedReservation, vehicleSelection, request.getParkingId());
            }
        }

        // create commission
        commissionReceivedService.createCommissionForReservation(savedReservation);

        return savedReservation;
    }

    public BigDecimal calculateTotalPrice(PriceCalculationRequest request) {
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        long hours = ChronoUnit.HOURS.between(request.getStartDateTime(), request.getEndDateTime());
        if (hours <= 0) {
            hours = 1;
        }

        int totalVehicles = request.getSelectedVehicles() != null ? 
                request.getSelectedVehicles().stream().mapToInt(VehicleSelection::getQuantity).sum() : 1;

        return parking.getHourlyRate().multiply(BigDecimal.valueOf(hours)).multiply(BigDecimal.valueOf(totalVehicles));
    }

    public List<Reservation> findByUserId(int userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByCreationDateDesc(userId);
        
        for (Reservation reservation : reservations) {
            updateReservationStatus(reservation);
        }
        
        return reservations;
    }
    
    // get owner reservations from view
    public List<ReservationResponse> findByParkingOwnerIdWithDetails(int ownerId) {
        List<Map<String, Object>> results = reservationRepository.findOwnerReservationsFromView(ownerId);
        
        return results.stream()
            .map(this::mapOwnerViewToResponse)
            .collect(Collectors.toList());
    }
    
    private ReservationResponse mapOwnerViewToResponse(Map<String, Object> row) {
        try {
            ReservationResponse.ParkingInfo parkingInfo = null;
            Integer parkingId = (Integer) row.get("parking_id");
            
            if (parkingId != null && parkingId > 0) {
                parkingInfo = ReservationResponse.ParkingInfo.builder()
                    .id(parkingId)
                    .name((String) row.get("parking_name"))
                    .address((String) row.get("parking_address"))
                    .city("")
                    .zipCode("")
                    .build();
            }
            
            LocalDateTime startDT = convertToLocalDateTime(row.get("start_datetime"));
            LocalDateTime endDT = convertToLocalDateTime(row.get("end_datetime"));
            
            return ReservationResponse.builder()
                .id((Integer) row.get("id_reservation"))
                .totalPrice((BigDecimal) row.get("total_price"))
                .creationDate(convertToLocalDateTime(row.get("creation_date")))
                .paymentDate(convertToLocalDateTime(row.get("payment_date")))
                .startDateTime(startDT)
                .endDateTime(endDT)
                .paymentMethod((String) row.get("payment_method"))
                .status((String) row.get("status_label"))
                .parking(parkingInfo)
                .parkingId(parkingId)
                .parkingName((String) row.get("parking_name"))
                .parkingAddress((String) row.get("parking_address"))
                .clientId((Integer) row.get("client_id"))
                .clientName((String) row.get("client_name"))
                .build();
        } catch (Exception e) {
            log.error("Error mapping owner view: {}", e.getMessage());
            throw new RuntimeException("Erreur lors du mapping de la reservation", e);
        }
    }

    // get user reservations with parking info
    public List<ReservationResponse> findByUserIdWithParkingInfo(int userId) {
        List<Map<String, Object>> results = reservationRepository.findUserReservationsFromView(userId);
        
        return results.stream()
            .map(this::mapViewToResponse)
            .collect(Collectors.toList());
    }

    private ReservationResponse mapViewToResponse(Map<String, Object> row) {
        try {
            ReservationResponse.ParkingInfo parkingInfo = null;
            Integer parkingId = (Integer) row.get("parking_id");
            
            if (parkingId != null && parkingId > 0) {
                parkingInfo = ReservationResponse.ParkingInfo.builder()
                    .id(parkingId)
                    .name((String) row.get("parking_name"))
                    .address((String) row.get("parking_address"))
                    .city("")
                    .zipCode("")
                    .build();
            }
            
            LocalDateTime startDT = convertToLocalDateTime(row.get("start_datetime"));
            LocalDateTime endDT = convertToLocalDateTime(row.get("end_datetime"));
            
            return ReservationResponse.builder()
                .id((Integer) row.get("id_reservation"))
                .totalPrice((BigDecimal) row.get("total_price"))
                .creationDate(convertToLocalDateTime(row.get("creation_date")))
                .paymentDate(convertToLocalDateTime(row.get("payment_date")))
                .startDateTime(startDT)
                .endDateTime(endDT)
                .paymentMethod((String) row.get("payment_method"))
                .status((String) row.get("status_label"))
                .parking(parkingInfo)
                .build();
        } catch (Exception e) {
            log.error("Error mapping view: {}", e.getMessage());
            throw new RuntimeException("Erreur lors du mapping de la reservation", e);
        }
    }
    
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        if (value instanceof java.time.Instant) {
            return LocalDateTime.ofInstant((java.time.Instant) value, java.time.ZoneOffset.UTC);
        }
        return null;
    }

    private ReservationResponse convertToResponse(Reservation reservation) {
        updateReservationStatus(reservation);
        
        Parking parking = getParkingFromReservation(reservation);
        
        ReservationResponse.ParkingInfo parkingInfo = null;
        if (parking != null) {
            parkingInfo = ReservationResponse.ParkingInfo.builder()
                .id(parking.getId_Parking())
                .name(parking.getLabel())
                .address(parking.getAddress())
                .city("")
                .zipCode("")
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

    // search with filters
    public List<Reservation> findReservationsWithFilters(Integer statusId, Integer userId,
                                                        Integer parkingId, LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        if (startDate != null || endDate != null) {
            return reservationRepository.findReservationsWithDateFilters(statusId, userId, parkingId, startDate, endDate);
        }
        return reservationRepository.findReservationsWithBasicFilters(statusId, userId, parkingId);
    }

    public List<Reservation> findReservationsByDateRange(Integer statusId, LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        return reservationRepository.findReservationsByDateRange(statusId, startDate, endDate);
    }
    
    public void updateReservationStatus(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        
        // dont update cancelled
        if (reservation.getReservationStatus() != null && reservation.getReservationStatus().getValue() == 25) {
            return;
        }
        
        if (reservation.getStartDateTime().isAfter(now)) {
            reservation.setReservationStatus(getStatusByValue(10)); // a venir
        } else if (reservation.getEndDateTime().isAfter(now)) {
            reservation.setReservationStatus(getStatusByValue(15)); // en cours
        } else {
            reservation.setReservationStatus(getStatusByValue(20)); // terminee
        }
    }
    
    private ReservationStatus getStatusByValue(int value) {
        return reservationStatusRepository.findByValue(value)
                .orElseGet(() -> {
                    String label = switch (value) {
                        case 10 -> "a venir";
                        case 15 -> "En cours";
                        case 20 -> "Terminee";
                        case 25 -> "Annulee";
                        default -> "a venir";
                    };
                    ReservationStatus status = ReservationStatus.builder()
                            .label(label)
                            .value(value)
                            .build();
                    return reservationStatusRepository.save(status);
                });
    }

    public boolean checkAvailability(PriceCalculationRequest request) {
        return checkAvailabilityWithAvailabilities(request);
    }

    private boolean checkAvailabilityWithAvailabilities(PriceCalculationRequest request) {
        int parkingId = request.getParkingId();
        LocalDateTime startDateTime = request.getStartDateTime();
        LocalDateTime endDateTime = request.getEndDateTime();
        
        if (request.getSelectedVehicles() != null) {
            for (var vehicleSelection : request.getSelectedVehicles()) {
                if (!checkVehicleAvailability(parkingId, vehicleSelection.getVehicleTypeId(),
                        vehicleSelection.getQuantity(), startDateTime, endDateTime)) {
                    return false;
                }
            }
        } else {
            if (!checkVehicleAvailability(parkingId, 1, 1, startDateTime, endDateTime)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean checkVehicleAvailability(int parkingId, int vehicleTypeId, int requiredQuantity,
                                           LocalDateTime startDateTime, LocalDateTime endDateTime) {
        
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        
        List<AvailabilitiesDate> dateAvailabilities =
            availabilitiesDateRepository.findOverlappingAvailabilities(parkingId, startDate, endDate);
        
        List<AvailabilitiesFrequence> frequencyAvailabilities =
            findFrequencyAvailabilities(parkingId, vehicleTypeId, startDateTime, endDateTime);
        
        int totalAvailableCapacity = calculateAvailableCapacity(
            parkingId, vehicleTypeId, dateAvailabilities, frequencyAvailabilities,
            startDateTime, endDateTime
        );
        
        return totalAvailableCapacity >= requiredQuantity;
    }

    private List<AvailabilitiesFrequence> findFrequencyAvailabilities(int parkingId, int vehicleTypeId,
                                                                     LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Integer> dayOfWeekIds = getDayOfWeekIdsInRange(startDateTime, endDateTime);
        
        return dayOfWeekIds.stream()
                .flatMap(dayId -> availabilitiesFrequenceRepository.findByParkingAndDayOfWeek(parkingId, dayId).stream())
                .filter(af -> af.getAnnouncementsVehicles().getParkingVehicles().getVehicle().getId_Vehicles() == vehicleTypeId)
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Integer> getDayOfWeekIdsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Integer> dayIds = new java.util.ArrayList<>();
        LocalDate currentDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        
        while (!currentDate.isAfter(endDate)) {
            int dayId = currentDate.getDayOfWeek().getValue();
            dayIds.add(dayId);
            currentDate = currentDate.plusDays(1);
        }
        
        return dayIds;
    }

    private boolean isFrequencyAvailable(AvailabilitiesFrequence af, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        java.time.LocalTime requestStartTime = startDateTime.toLocalTime();
        java.time.LocalTime requestEndTime = endDateTime.toLocalTime();
        java.time.LocalTime availStartTime = af.getStartHour();
        java.time.LocalTime availEndTime = af.getEndHour();
        
        boolean startTimeValid = !requestStartTime.isBefore(availStartTime);
        boolean endTimeValid = !requestEndTime.isAfter(availEndTime);
        
        return startTimeValid && endTimeValid;
    }

    private int calculateAvailableCapacity(int parkingId, int vehicleTypeId,
                                         List<AvailabilitiesDate> dateAvailabilities,
                                         List<AvailabilitiesFrequence> frequencyAvailabilities,
                                         LocalDateTime startDateTime, LocalDateTime endDateTime) {
        
        int baseCapacity = parkingVehiclesRepository.findByParkingId(parkingId)
                .stream()
                .filter(pv -> pv.getVehicle().getId_Vehicles() == vehicleTypeId)
                .mapToInt(ParkingVehicles::getNumbers)
                .sum();
        
        if (baseCapacity == 0) {
            return 0;
        }
        
        boolean hasDateAvailability = !dateAvailabilities.isEmpty() &&
            dateAvailabilities.stream().anyMatch(da ->
                isDateAvailabilityCoveringPeriod(da, startDateTime, endDateTime));
        
        boolean hasFrequencyAvailability = !frequencyAvailabilities.isEmpty() &&
            frequencyAvailabilities.stream().anyMatch(af ->
                isFrequencyAvailable(af, startDateTime, endDateTime));
        
        if (!hasDateAvailability && !hasFrequencyAvailability) {
            return 0;
        }
        
        int reservedCapacity = getReservedCapacity(parkingId, vehicleTypeId, startDateTime, endDateTime);
        
        return Math.max(0, baseCapacity - reservedCapacity);
    }

    private boolean isDateAvailabilityCoveringPeriod(AvailabilitiesDate da, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime availabilityStart = da.getStartDate().atTime(da.getStartHour());
        LocalDateTime availabilityEnd = da.getEndDate().atTime(da.getEndHour());
        
        return !startDateTime.isAfter(availabilityEnd) && !endDateTime.isBefore(availabilityStart);
    }

    private int getReservedCapacity(int parkingId, int vehicleTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return 0;
    }

    private ReservationStatus getDefaultReservationStatus() {
        return reservationStatusRepository.findByValue(10)
                .orElseGet(() -> {
                    ReservationStatus defaultStatus = ReservationStatus.builder()
                            .label("a venir")
                            .value(10)
                            .build();
                    return reservationStatusRepository.save(defaultStatus);
                });
    }

    private void createReservationVehicles(Reservation reservation, VehicleSelection vehicleSelection, int parkingId) {
        try {
            List<AnnouncementsVehicles> announcementsVehicles = announcementsVehiclesRepository
                    .findByParkingAndVehicleType(parkingId, vehicleSelection.getVehicleTypeId());
            
            AnnouncementsVehicles selectedAnnouncementVehicle = null;
            
            if (!announcementsVehicles.isEmpty()) {
                selectedAnnouncementVehicle = announcementsVehicles.get(0);
            } else {
                selectedAnnouncementVehicle = announcementsVehiclesRepository
                        .findByParkingAndVehicleTypeNative(parkingId, vehicleSelection.getVehicleTypeId());
            }
            
            if (selectedAnnouncementVehicle == null) {
                log.warn("No AnnouncementsVehicles found for parkingId={} vehicleTypeId={}", parkingId, vehicleSelection.getVehicleTypeId());
                return;
            }
            
            ReservationVehicles reservationVehicles = ReservationVehicles.builder()
                    .reservation(reservation)
                    .numbers(vehicleSelection.getQuantity())
                    .announcementsVehicles(selectedAnnouncementVehicle)
                    .build();
            
            reservationVehiclesRepository.save(reservationVehicles);
        } catch (Exception e) {
            log.error("Error creating reservation vehicles: {}", e.getMessage());
        }
    }

    // update all statuses manually
    public int updateAllReservationStatuses() {
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = 0;
        
        try {
            ReservationStatus statusAVenir = getStatusByValue(10);
            ReservationStatus statusEnCours = getStatusByValue(15);
            ReservationStatus statusTerminee = getStatusByValue(20);

            List<Reservation> reservationsAVenir = reservationRepository
                .findByReservationStatusId(statusAVenir.getId_Reservation_status());
            
            for (Reservation reservation : reservationsAVenir) {
                boolean updated = false;
                
                if (reservation.getStartDateTime().isBefore(now) || reservation.getStartDateTime().isEqual(now)) {
                    if (reservation.getEndDateTime().isAfter(now)) {
                        reservation.setReservationStatus(statusEnCours);
                        updated = true;
                    } else {
                        reservation.setReservationStatus(statusTerminee);
                        updated = true;
                    }
                }
                
                if (updated) {
                    reservationRepository.save(reservation);
                    updatedCount++;
                }
            }

            List<Reservation> reservationsEnCours = reservationRepository
                .findByReservationStatusId(statusEnCours.getId_Reservation_status());
            
            for (Reservation reservation : reservationsEnCours) {
                if (reservation.getEndDateTime().isBefore(now) || reservation.getEndDateTime().isEqual(now)) {
                    reservation.setReservationStatus(statusTerminee);
                    reservationRepository.save(reservation);
                    updatedCount++;
                }
            }

            return updatedCount;
            
        } catch (Exception e) {
            log.error("Error updating statuses: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la mise a jour des statuts: " + e.getMessage());
        }
    }

    public void updateReservationStatusById(int reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            updateReservationStatus(reservation);
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation non trouvee avec l'ID: " + reservationId);
        }
    }
    
    // find by QR token
    public Optional<Reservation> findByQRToken(String qrToken) {
        return reservationRepository.findByQrCodeToken(qrToken);
    }
}