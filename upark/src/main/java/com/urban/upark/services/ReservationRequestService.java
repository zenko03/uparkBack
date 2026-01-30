package com.urban.upark.services;

import com.urban.upark.models.Announcements;
import com.urban.upark.models.AnnouncementsVehicles;
import com.urban.upark.models.Reservation;
import com.urban.upark.models.ReservationRequest;
import com.urban.upark.models.ReservationRequestVehicles;
import com.urban.upark.models.ReservationVehicles;
import com.urban.upark.models.Users;
import com.urban.upark.repositories.AnnouncementsRepository;
import com.urban.upark.repositories.AnnouncementsVehiclesRepository;
import com.urban.upark.repositories.ReservationRepository;
import com.urban.upark.repositories.ReservationRequestRepository;
import com.urban.upark.repositories.ReservationRequestVehiclesRepository;
import com.urban.upark.repositories.ReservationVehiclesRepository;
import com.urban.upark.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationRequestService {

    private final ReservationRequestRepository reservationRequestRepository;
    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
    private final AnnouncementsRepository announcementsRepository;
    private final AnnouncementsVehiclesRepository announcementsVehiclesRepository;
    private final ReservationVehiclesRepository reservationVehiclesRepository;
    private final ReservationRequestVehiclesRepository reservationRequestVehiclesRepository;
    private final ReservationService reservationService;
    private final QRCodeService qrCodeService;
    private final NotificationService notificationService;
    private final CommissionReceivedService commissionReceivedService;

    public List<ReservationRequest> findAll() {
        return reservationRequestRepository.findAll();
    }

    public Optional<ReservationRequest> findById(Long id) {
        return reservationRequestRepository.findById(id);
    }

    // Demandes reçues par propriétaire
    public List<ReservationRequest> findByOwnerId(Long ownerId) {
        return reservationRequestRepository.findByAnnouncementParkingUserId(ownerId);
    }

    // Demandes envoyées par client
    public List<ReservationRequest> findByRequesterId(Long requesterId) {
        return reservationRequestRepository.findByRequesterId(requesterId);
    }

    // Demandes par statut
    public List<ReservationRequest> findByState(Short state) {
        return reservationRequestRepository.findByState(state);
    }

    @Transactional
    public ReservationRequest createReservationRequest(Integer requesterId, Integer announcementId, 
                                                        LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                        Float totalGain, List<com.urban.upark.dto.reservation.VehicleSelection> selectedVehicles) {
        // Charger l'utilisateur (requester) depuis la base
        Users requester = usersRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + requesterId));

        // Charger l'annonce depuis la base
        Announcements announcement = announcementsRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée: " + announcementId));

        // Créer la demande avec les entités gérées par Hibernate
        ReservationRequest request = ReservationRequest.builder()
                .requester(requester)
                .announcement(announcement)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .totalGain(totalGain)
                .state((short) 10) // 10 = En attente
                .build();

        // Sauvegarder d'abord la demande pour obtenir l'ID
        ReservationRequest savedRequest = reservationRequestRepository.save(request);

        // Créer les ReservationRequestVehicles à partir des véhicules sélectionnés
        if (selectedVehicles != null && !selectedVehicles.isEmpty()) {
            System.out.println(" Création de " + selectedVehicles.size() + " type(s) de véhicule(s) pour la demande");
            
            for (com.urban.upark.dto.reservation.VehicleSelection vehicleSelection : selectedVehicles) {
                // Trouver l'AnnouncementsVehicles correspondant
                AnnouncementsVehicles announcementsVehicles = announcementsVehiclesRepository
                        .findByAnnouncementIdAndVehicleTypeId(
                                announcementId, 
                                vehicleSelection.getVehicleTypeId()
                        );
                
                if (announcementsVehicles == null) {
                    System.err.println("Erreur: AnnouncementsVehicles non trouvé pour annonce " + announcementId + 
                                     ", type véhicule " + vehicleSelection.getVehicleTypeId());
                    continue;
                }
                
                // Créer l'entrée ReservationRequestVehicles
                ReservationRequestVehicles rrv = ReservationRequestVehicles.builder()
                        .reservationRequest(savedRequest)
                        .announcementsVehicles(announcementsVehicles)
                        .numbers(vehicleSelection.getQuantity())
                        .build();
                
                reservationRequestVehiclesRepository.save(rrv);
                System.out.println("   Type véhicule " + vehicleSelection.getVehicleTypeId() + 
                                 " x" + vehicleSelection.getQuantity() + " enregistré");
            }
        }

        // Envoyer notification au PROPRIÉTAIRE "Nouvelle demande de réservation"
        try {
            Integer ownerId = announcement.getParking().getUser().getId_Users();
            String requesterName = requester.getUsername();
            String parkingName = announcement.getParking().getLabel();
            
            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("type", "reservation_request");
            notificationData.put("requestId", savedRequest.getId().toString());
            notificationData.put("parkingName", parkingName);
            notificationData.put("requesterName", requesterName);
            
            notificationService.sendPushNotification(
                ownerId,
                "Nouvelle demande de réservation",
                requesterName + " souhaite réserver votre parking " + parkingName,
                "new_request",  // Changé pour correspondre à la contrainte CHECK de la BDD
                notificationData
            );
            System.out.println("📤 Notification envoyée au propriétaire " + ownerId);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur envoi notification nouvelle demande: " + e.getMessage());
        }

        return savedRequest;
    }

    @Transactional
    public ReservationRequest save(ReservationRequest request) {
        return reservationRequestRepository.save(request);
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRequestRepository.deleteById(id);
    }

    // Accepter une demande = update state + définir expiration
    @Transactional
    public ReservationRequest acceptRequest(Long requestId) {
        ReservationRequest request = reservationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (request.getState() != 10) {
            throw new RuntimeException("Seules les demandes en attente peuvent être acceptées");
        }

        // Mettre à jour le statut de la demande
        request.setState((short) 20); // 20 = Acceptée
        request.setAcceptedAt(LocalDateTime.now());
        request.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24h pour payer

        ReservationRequest savedRequest = reservationRequestRepository.save(request);
        
        // Envoyer notification au client "Demande acceptée, payez dans 24h"
        try {
            Integer requesterId = request.getRequester().getId_Users();
            String parkingName = request.getAnnouncement().getParking().getLabel();
            
            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("type", "reservation_accepted");
            notificationData.put("requestId", requestId.toString());
            notificationData.put("parkingName", parkingName);
            
            notificationService.sendPushNotification(
                requesterId,
                "Demande acceptée",
                "Votre demande pour " + parkingName + " a été acceptée. Payez dans 24h pour confirmer.",
                "reservation_accepted",
                notificationData
            );
        } catch (Exception e) {
            System.err.println("Erreur: Erreur envoi notification acceptation: " + e.getMessage());
        }
        
        return savedRequest;
    }

    // Refuser une demande
    @Transactional
    public ReservationRequest rejectRequest(Long requestId) {
        ReservationRequest request = reservationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (request.getState() != 10) {
            throw new RuntimeException("Seules les demandes en attente peuvent être refusées");
        }

        request.setState((short) 25); // 25 = Refusée
        ReservationRequest savedRequest = reservationRequestRepository.save(request);
        
        // Envoyer notification au client "Demande refusée"
        try {
            Integer requesterId = request.getRequester().getId_Users();
            String parkingName = request.getAnnouncement().getParking().getLabel();
            
            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("type", "reservation_rejected");
            notificationData.put("requestId", requestId.toString());
            notificationData.put("parkingName", parkingName);
            
            notificationService.sendPushNotification(
                requesterId,
                "Demande refusée",
                "Votre demande pour " + parkingName + " a été refusée par le propriétaire.",
                "reservation_rejected",
                notificationData
            );
        } catch (Exception e) {
            System.err.println("Erreur: Erreur envoi notification refus: " + e.getMessage());
        }
        
        return savedRequest;
    }

    // Finaliser réservation après paiement (NOUVEAU)
    @Transactional
    public Reservation finalizeReservation(Long requestId, String paymentMethod) {
        ReservationRequest request = reservationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (request.getState() != 20) {
            throw new RuntimeException("Seules les demandes acceptées peuvent être finalisées");
        }

        // Vérifier que le délai n'est pas expiré
        if (request.getExpiresAt() != null && LocalDateTime.now().isAfter(request.getExpiresAt())) {
            request.setState((short) 35); // 35 = Expirée
            reservationRequestRepository.save(request);
            throw new RuntimeException("Le délai de paiement est expiré");
        }

        System.out.println("💳 Finalisation de la réservation pour la demande ID: " + requestId);

        // Créer la réservation MAINTENANT
        Reservation reservation = Reservation.builder()
                .user(request.getRequester())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .totalPrice(new java.math.BigDecimal(request.getTotalGain()))
                .creationDate(LocalDateTime.now())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .build();

        // Déterminer le statut selon les dates (à venir / en cours / terminée)
        reservationService.updateReservationStatus(reservation);
        
        // Sauvegarder la réservation avec le statut défini
        Reservation savedReservation = reservationRepository.save(reservation);
        System.out.println(" Réservation créée avec ID: " + savedReservation.getId_Reservation() + 
                         " - Statut: " + (savedReservation.getReservationStatus() != null ? 
                         savedReservation.getReservationStatus().getLabel() : "NON DÉFINI"));

        // 🔐 Générer le QR Code token pour la réservation
        String qrToken = qrCodeService.generateFormattedQRToken(savedReservation.getId_Reservation());
        savedReservation.setQrCodeToken(qrToken);
        savedReservation.setIsValidated(false);
        savedReservation = reservationRepository.save(savedReservation);
        System.out.println("🔐 QR Code token généré: " + qrToken);

        // Créer les ReservationVehicles à partir de l'annonce
        createReservationVehiclesFromAnnouncement(savedReservation, request);

        // Créer la commission pour cette réservation (cohérence avec createReservation)
        System.out.println("💰 Création de la commission pour la réservation finalisée...");
        try {
            commissionReceivedService.createCommissionForReservation(savedReservation);
            System.out.println("✅ Commission créée avec succès");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de la création de la commission (non-bloquant): " + e.getMessage());
        }

        // Mettre à jour la demande
        request.setState((short) 40); // 40 = Finalisée (réservation créée)
        reservationRequestRepository.save(request);

        // Envoyer notification de confirmation de paiement au CLIENT
        try {
            Integer clientId = request.getRequester().getId_Users();
            String parkingName = request.getAnnouncement().getParking().getLabel();
            
            Map<String, String> clientNotifData = new HashMap<>();
            clientNotifData.put("type", "payment_confirmed");
            clientNotifData.put("reservationId", String.valueOf(savedReservation.getId_Reservation()));
            clientNotifData.put("parkingName", parkingName);
            clientNotifData.put("qrToken", savedReservation.getQrCodeToken());
            
            notificationService.sendPushNotification(
                clientId,
                "Paiement confirmé",
                "Votre réservation pour " + parkingName + " est confirmée ! QR Code prêt.",
                "payment_confirmed",
                clientNotifData
            );
            System.out.println("📤 Notification paiement confirmé envoyée au client " + clientId);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur envoi notification paiement client: " + e.getMessage());
        }

        // Envoyer notification au PROPRIÉTAIRE
        try {
            Integer ownerId = request.getAnnouncement().getParking().getUser().getId_Users();
            String clientName = request.getRequester().getUsername();
            String parkingName = request.getAnnouncement().getParking().getLabel();
            
            Map<String, String> ownerNotifData = new HashMap<>();
            ownerNotifData.put("type", "payment_confirmed");
            ownerNotifData.put("reservationId", String.valueOf(savedReservation.getId_Reservation()));
            ownerNotifData.put("clientName", clientName);
            
            notificationService.sendPushNotification(
                ownerId,
                "Paiement reçu",
                clientName + " a payé pour la réservation de " + parkingName,
                "payment_confirmed",
                ownerNotifData
            );
            System.out.println("📤 Notification paiement confirmé envoyée au propriétaire " + ownerId);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur envoi notification paiement propriétaire: " + e.getMessage());
        }

        return savedReservation;
    }
    
    /**
     * Créer les ReservationVehicles à partir des véhicules sélectionnés dans la demande
     */
    private void createReservationVehiclesFromAnnouncement(Reservation reservation, ReservationRequest request) {
        try {
            System.out.println(" Création des ReservationVehicles pour la réservation " + reservation.getId_Reservation());
            
            // Récupérer les véhicules sélectionnés depuis la table reservation_request_vehicles
            List<ReservationRequestVehicles> requestVehicles = reservationRequestVehiclesRepository
                    .findByReservationRequestId(request.getId());
            
            if (requestVehicles == null || requestVehicles.isEmpty()) {
                System.err.println(" Aucun véhicule sélectionné trouvé dans la table reservation_request_vehicles pour la demande " + request.getId());
                System.err.println(" Fallback: utilisation du premier type disponible de l'annonce");
                createDefaultReservationVehicle(reservation, request);
                return;
            }
            
            System.out.println(" Véhicules sélectionnés trouvés: " + requestVehicles.size() + " type(s)");
            
            // Pour chaque type de véhicule sélectionné, créer un ReservationVehicles
            for (ReservationRequestVehicles rrv : requestVehicles) {
                System.out.println("  → Type véhicule: " + rrv.getAnnouncementsVehicles().getId_Announcements_vehicles() + 
                                 ", quantité: " + rrv.getNumbers());
                
                // Créer le ReservationVehicles avec les mêmes paramètres
                ReservationVehicles reservationVehicles = ReservationVehicles.builder()
                        .reservation(reservation)
                        .announcementsVehicles(rrv.getAnnouncementsVehicles())
                        .numbers(rrv.getNumbers())
                        .build();
                
                ReservationVehicles saved = reservationVehiclesRepository.save(reservationVehicles);
                System.out.println("   ReservationVehicles créé avec ID: " + saved.getId_Reservation_vehicles());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur: Erreur lors de la création des ReservationVehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Fallback: créer un ReservationVehicles par défaut si aucune sélection
     */
    private void createDefaultReservationVehicle(Reservation reservation, ReservationRequest request) {
        try {
            Announcements announcement = request.getAnnouncement();
            List<AnnouncementsVehicles> announcementsVehicles = announcementsVehiclesRepository
                    .findByAnnouncementId(announcement.getId_Announcements());
            
            if (!announcementsVehicles.isEmpty()) {
                AnnouncementsVehicles firstAvailable = announcementsVehicles.get(0);
                ReservationVehicles reservationVehicles = ReservationVehicles.builder()
                        .reservation(reservation)
                        .announcementsVehicles(firstAvailable)
                        .numbers(1)
                        .build();
                reservationVehiclesRepository.save(reservationVehicles);
                System.out.println(" ReservationVehicles par défaut créé");
            }
        } catch (Exception e) {
            System.err.println("Erreur: Erreur fallback: " + e.getMessage());
        }
    }
}
