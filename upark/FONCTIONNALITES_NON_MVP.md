# 📋 PLAN D'IMPLÉMENTATION - FONCTIONNALITÉS NON-MVP
## Projet uPark - Application de Location de Parking

**Date :** 22 décembre 2025  
**Version :** 1.0  
**État MVP :** ✅ Terminé et Fonctionnel

---

## 📖 TABLE DES MATIÈRES

1. [Fonctionnalité 1 : Gestion des Parkings Propriétaire](#fonctionnalité-1--gestion-des-parkings-propriétaire)
2. [Fonctionnalité 2 : Gestion des Publications](#fonctionnalité-2--gestion-des-publications)
3. [Fonctionnalité 3 : Gestion des Demandes de Réservation](#fonctionnalité-3--gestion-des-demandes-de-réservation)
4. [Fonctionnalité 4 : Tableau de Bord Utilisateur](#fonctionnalité-4--tableau-de-bord-utilisateur)
5. [Fonctionnalité 5 : Notation des Utilisateurs](#fonctionnalité-5--notation-des-utilisateurs)
6. [Fonctionnalité 6 : Paiement en Ligne (Stripe)](#fonctionnalité-6--paiement-en-ligne-stripe)
7. [Fonctionnalité 7 : QR Code pour Validation](#fonctionnalité-7--qr-code-pour-validation)
8. [Fonctionnalité 8 : Authentification Sociale](#fonctionnalité-8--authentification-sociale)
9. [Fonctionnalité 9 : Notifications Push](#fonctionnalité-9--notifications-push)
10. [Fonctionnalité 10 : Gestion des Litiges et Remboursements](#fonctionnalité-10--gestion-des-litiges-et-remboursements)
11. [Récapitulatif Global](#récapitulatif-global)

---

## Fonctionnalité 1 : Gestion des Parkings Propriétaire

### 🎯 QUOI
Permettre aux propriétaires de gérer leurs propres parkings (créer, modifier, supprimer, activer/désactiver).

### 🤔 POURQUOI
- Autonomie des propriétaires dans la gestion de leurs biens
- Possibilité d'ajouter/retirer des parkings selon disponibilité
- Activation/désactivation temporaire sans suppression
- Centralisation de la gestion dans l'application mobile

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Modifications à apporter :**

**ParkingController.java**
- Ajouter endpoint `GET /api/parkings/user/{userId}` - Récupérer les parkings d'un utilisateur
- Ajouter endpoint `PUT /api/parkings/{id}/toggle-active` - Activer/désactiver un parking

**ParkingService.java**
- Ajouter méthode `findByUserId(int userId)`
- Ajouter méthode `toggleActive(int id)`

**ParkingRepository.java**
- Ajouter méthode `List<Parking> findByUser_IdUsers(int userId)`

**Parking.java (Modèle)**
- Ajouter champ `Boolean isActive`
- Ajouter champ `LocalDateTime createdAt`
- Ajouter champ `LocalDateTime updatedAt`

#### **BASE DE DONNÉES (PostgreSQL)**

**Table Parking**
- Ajouter colonne `is_active BOOLEAN DEFAULT TRUE`
- Ajouter colonne `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- Ajouter colonne `updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
- `MyParkings.jsx` - Liste des parkings du propriétaire avec statistiques
- `AddEditParking.jsx` - Formulaire de création/modification de parking

**Nouveau service à créer :**
- `ownerService.js` avec méthodes :
  - `getMyParkings(userId)`
  - `createParking(parkingData)`
  - `updateParking(id, parkingData)`
  - `deleteParking(id)`
  - `toggleParkingActive(id)`

**Navigation à modifier :**
- Ajouter routes vers `MyParkings` et `AddEditParking`
- Ajouter entrée menu pour "Mes Parkings"

---

## Fonctionnalité 2 : Gestion des Publications

### 🎯 QUOI
Permettre aux propriétaires de créer et gérer des annonces de disponibilité pour leurs parkings.

### 🤔 POURQUOI
- Séparation entre parking (bien physique) et annonce (disponibilité)
- Gestion flexible des périodes de disponibilité
- Possibilité de publier/dépublier sans supprimer
- Meilleur contrôle de la visibilité

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- `AnnouncementsController.java` avec endpoints :
  - `GET /api/announcements/user/{userId}` - Annonces d'un utilisateur
  - `POST /api/announcements` - Créer une annonce
  - `PUT /api/announcements/{id}` - Modifier une annonce
  - `DELETE /api/announcements/{id}` - Supprimer une annonce
  - `PUT /api/announcements/{id}/publish` - Publier une annonce
  - `PUT /api/announcements/{id}/unpublish` - Dépublier une annonce

- `AnnouncementsService.java` avec logique métier pour :
  - Création d'annonces avec disponibilités
  - Gestion de la publication
  - Validation des données
  - Association avec parkings et véhicules

- `AnnouncementsRepository.java` avec méthodes :
  - `findByParking_User_IdUsers(int userId)`
  - `findByIsPublished(boolean isPublished)`

**Modifications à apporter :**

**Announcements.java (Modèle)**
- Ajouter champ `Boolean isPublished`
- Ajouter relation `@ManyToOne` vers `Parking`

#### **BASE DE DONNÉES (PostgreSQL)**

**Table Announcements**
- Ajouter colonne `is_published BOOLEAN DEFAULT FALSE`
- Ajouter colonne `Id_Parking INTEGER`
- Ajouter contrainte `FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)`

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
- `MyAnnouncements.jsx` - Liste des annonces avec statut publié/non publié
- `CreateAnnouncement.jsx` - Formulaire de création d'annonce avec :
  - Sélection du parking
  - Définition des disponibilités
  - Sélection des types de véhicules acceptés
  - Option publication immédiate ou brouillon

**Nouveau service à créer :**
- `announcementService.js` avec méthodes :
  - `getUserAnnouncements(userId)`
  - `createAnnouncement(announcementData)`
  - `updateAnnouncement(id, announcementData)`
  - `deleteAnnouncement(id)`
  - `publishAnnouncement(id)`
  - `unpublishAnnouncement(id)`

---

## Fonctionnalité 3 : Gestion des Demandes de Réservation

### 🎯 QUOI
Implémenter un système de demandes de réservation nécessitant l'approbation du propriétaire avant confirmation.

### 🤔 POURQUOI
- Contrôle du propriétaire sur qui peut réserver
- Éviter les réservations non désirées
- Possibilité de négociation
- Meilleure gestion de la disponibilité

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- `ReservationRequest.java` (Modèle) avec champs :
  - `Id_Reservation_request`
  - `startDateTime`, `endDateTime`
  - `proposedPrice`
  - `status` (PENDING, APPROVED, REJECTED)
  - `message`
  - Relations vers `Users` et `Announcements`

- `ReservationRequestController.java` avec endpoints :
  - `POST /api/reservation-requests` - Créer une demande
  - `GET /api/reservation-requests/user/{userId}` - Demandes du locataire
  - `GET /api/reservation-requests/owner/{ownerId}` - Demandes reçues par propriétaire
  - `PUT /api/reservation-requests/{id}/approve` - Approuver (crée la réservation)
  - `PUT /api/reservation-requests/{id}/reject` - Rejeter avec raison

- `ReservationRequestService.java` avec logique :
  - Création de demande
  - Validation des disponibilités
  - Approbation (création automatique de réservation)
  - Rejet avec notification

- `ReservationRequestRepository.java` avec méthodes :
  - `findByUser_IdUsers(int userId)`
  - `findByAnnouncements_Parking_User_IdUsers(int ownerId)`
  - `findByStatus(RequestStatus status)`

#### **BASE DE DONNÉES (PostgreSQL)**

**Nouvelle table à créer :**
- `Reservation_requests` avec colonnes :
  - `Id_Reservation_request SERIAL PRIMARY KEY`
  - `start_datetime TIMESTAMP NOT NULL`
  - `end_datetime TIMESTAMP NOT NULL`
  - `proposed_price NUMERIC(15,2) NOT NULL`
  - `status VARCHAR(50) DEFAULT 'PENDING'`
  - `message TEXT`
  - `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
  - `Id_Users INTEGER` (FK vers Users)
  - `Id_Announcements INTEGER` (FK vers Announcements)

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
- `ReservationRequests.jsx` - Liste des demandes pour le propriétaire avec :
  - Détails de chaque demande
  - Informations du locataire
  - Boutons Accepter/Refuser
  - Filtres par statut

**Écrans à modifier :**
- `Reservation.jsx` - Remplacer création directe par envoi de demande

**Nouveau service à créer :**
- `reservationRequestService.js` avec méthodes :
  - `createRequest(requestData)`
  - `getUserRequests(userId)`
  - `getOwnerRequests(ownerId)`
  - `approveRequest(id)`
  - `rejectRequest(id, reason)`

---

## Fonctionnalité 4 : Tableau de Bord Utilisateur

### 🎯 QUOI
Créer un tableau de bord personnalisé affichant les statistiques et activités de l'utilisateur.

### 🤔 POURQUOI
- Vue d'ensemble rapide de l'activité
- Suivi des performances (propriétaire)
- Suivi des dépenses (locataire)
- Amélioration de l'expérience utilisateur

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- `UserDashboardController.java` avec endpoints :
  - `GET /api/user-dashboard/{userId}/stats` - Statistiques globales
  - `GET /api/user-dashboard/{userId}/recent-activity` - Activités récentes
  - `GET /api/user-dashboard/{userId}/earnings` - Rapport de revenus (propriétaire)

- `UserDashboardService.java` avec logique :
  - Calcul des statistiques (réservations, revenus, dépenses)
  - Agrégation des données
  - Calcul de la note moyenne
  - Génération de rapports

- `UserDashboardStats.java` (DTO) avec champs :
  - `totalReservations`
  - `activeReservations`
  - `totalParkings`
  - `totalEarnings`
  - `averageRating`

#### **BASE DE DONNÉES (PostgreSQL)**

**Pas de nouvelle table nécessaire** - Utilise les tables existantes avec requêtes d'agrégation

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
- `UserDashboard.jsx` avec sections :
  - Statistiques en cartes (réservations, revenus, etc.)
  - Graphiques d'évolution
  - Activité récente
  - Raccourcis vers actions principales

**Nouveau service à créer :**
- `dashboardService.js` avec méthodes :
  - `getUserStats(userId)`
  - `getRecentActivity(userId)`
  - `getEarnings(userId, period)`

**Composants à créer :**
- `StatCard.jsx` - Carte de statistique
- `ActivityItem.jsx` - Élément d'activité récente
- `EarningsChart.jsx` - Graphique des revenus

---

## Fonctionnalité 5 : Notation des Utilisateurs

### 🎯 QUOI
Permettre aux utilisateurs de se noter mutuellement après une réservation (en plus de la notation des parkings déjà existante).

### 🤔 POURQUOI
- Système de confiance entre utilisateurs
- Identification des utilisateurs fiables
- Amélioration de la qualité du service
- Feedback bidirectionnel (locataire ↔ propriétaire)

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- `UserNote.java` (Modèle) avec champs :
  - `Id_User_note`
  - `note` (sur 5)
  - `comment`
  - `createdAt`
  - Relations vers `reviewer` (celui qui note), `reviewed` (celui qui est noté), `reservation`

- `UserNoteController.java` avec endpoints :
  - `POST /api/user-notes` - Créer une note
  - `GET /api/user-notes/user/{userId}` - Notes d'un utilisateur
  - `GET /api/user-notes/user/{userId}/average` - Note moyenne

- `UserNoteService.java` avec logique :
  - Création de note (validation : 1 note par réservation)
  - Calcul de la moyenne
  - Mise à jour du champ `average_rating` dans Users

- `UserNoteRepository.java` avec méthodes :
  - `findByReviewed_IdUsers(int userId)`
  - `findByReservation_IdReservation(int reservationId)`

**Modifications à apporter :**

**Users.java (Modèle)**
- Ajouter champ `BigDecimal averageRating`
- Ajouter champ `Integer totalRatings`

#### **BASE DE DONNÉES (PostgreSQL)**

**Nouvelle table à créer :**
- `User_note` avec colonnes :
  - `Id_User_note SERIAL PRIMARY KEY`
  - `note NUMERIC(6,2) NOT NULL`
  - `comment TEXT`
  - `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
  - `Id_Reviewer INTEGER` (FK vers Users)
  - `Id_Reviewed INTEGER` (FK vers Users)
  - `Id_Reservation INTEGER` (FK vers Reservation)

**Table Users**
- Ajouter colonne `average_rating NUMERIC(6,2) DEFAULT 0`
- Ajouter colonne `total_ratings INTEGER DEFAULT 0`

#### **FRONTEND MOBILE (React Native)**

**Nouveaux composants à créer :**
- `RatingModal.jsx` - Modal de notation avec :
  - Sélection d'étoiles (1-5)
  - Champ commentaire
  - Distinction parking vs utilisateur
- `ReviewCard.jsx` - Affichage d'un avis

**Écrans à modifier :**
- `ReservationList.jsx` - Ajouter bouton "Noter" pour réservations terminées non notées
- `ParkingDetails.jsx` - Afficher les avis utilisateurs sur le propriétaire

**Nouveau service à créer :**
- `ratingService.js` avec méthodes :
  - `rateParkingNote(ratingData)` (déjà existant - à vérifier)
  - `rateUser(ratingData)`
  - `getParkingNotes(parkingId)`
  - `getUserRating(userId)`
  - `getUserReviews(userId)`

---

## Fonctionnalité 6 : Paiement en Ligne (Stripe)

### 🎯 QUOI
Intégrer Stripe pour permettre le paiement en ligne par carte bancaire.

### 🤔 POURQUOI
- Sécurisation des transactions
- Automatisation du paiement
- Réduction des impayés
- Meilleure expérience utilisateur
- Traçabilité des paiements

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Dépendances à ajouter :**
- `pom.xml` : Ajouter dépendance `stripe-java` version 24.0.0

**Configuration à ajouter :**
- `application.properties` : Ajouter `stripe.api.key=${STRIPE_SECRET_KEY}`

**Nouveaux fichiers à créer :**
- `PaymentService.java` avec méthodes :
  - `createPaymentIntent(amount, currency)` - Créer intention de paiement
  - `confirmPayment(paymentIntentId)` - Confirmer le paiement
  - `createRefund(paymentIntentId, amount)` - Créer un remboursement

- `PaymentController.java` avec endpoints :
  - `POST /api/payments/create-intent` - Créer intention de paiement
  - `POST /api/payments/confirm` - Confirmer paiement
  - `POST /api/payments/refund` - Demander remboursement
  - `POST /api/payments/webhook` - Webhook Stripe (événements)

- `PaymentIntentResponse.java` (DTO)
- `PaymentConfirmRequest.java` (DTO)
- `RefundRequest.java` (DTO)

**Modifications à apporter :**

**Reservation.java (Modèle)**
- Ajouter champ `String paymentIntentId`
- Ajouter champ `PaymentStatus paymentStatus` (enum)

**ReservationService.java**
- Intégrer logique de paiement dans création de réservation

#### **BASE DE DONNÉES (PostgreSQL)**

**Table Reservation**
- Ajouter colonne `payment_intent_id VARCHAR(255)`
- Ajouter colonne `payment_status VARCHAR(50) DEFAULT 'PENDING'`

#### **FRONTEND MOBILE (React Native)**

**Dépendances à ajouter :**
- `package.json` : Ajouter `@stripe/stripe-react-native` version 0.37.0

**Configuration à ajouter :**
- Configurer Stripe Publishable Key dans variables d'environnement

**Nouveaux écrans à créer :**
- `PaymentScreen.jsx` avec :
  - Intégration Stripe CardField
  - Affichage du montant
  - Bouton de confirmation
  - Gestion des erreurs de paiement

**Écrans à modifier :**
- `ReservationConfirmation.jsx` - Ajouter choix du mode de paiement (carte ou sur place)

**Nouveau service à créer :**
- `paymentService.js` avec méthodes :
  - `createPaymentIntent(amount, currency)`
  - `confirmPayment(paymentIntentId, paymentMethodId)`
  - `requestRefund(reservationId, amount, reason)`

---

## Fonctionnalité 7 : QR Code pour Validation

### 🎯 QUOI
Générer un QR code unique pour chaque réservation permettant la validation à l'arrivée.

### 🤔 POURQUOI
- Validation rapide et sans contact
- Sécurisation de l'accès au parking
- Preuve d'arrivée
- Automatisation du check-in

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Dépendances à ajouter :**
- `pom.xml` : Ajouter dépendances ZXing :
  - `com.google.zxing:core` version 3.5.1
  - `com.google.zxing:javase` version 3.5.1

**Nouveaux fichiers à créer :**
- `QRCodeService.java` avec méthodes :
  - `generateQRCode(data, width, height)` - Générer image QR code
  - `decodeQRCode(file)` - Décoder QR code
  - `generateReservationToken(reservationId)` - Générer token JWT unique

**Modifications à apporter :**

**ReservationController.java**
- Ajouter endpoint `GET /api/reservations/{id}/qrcode` - Obtenir QR code (image PNG)
- Ajouter endpoint `POST /api/reservations/validate-qrcode` - Valider un QR code

**Reservation.java (Modèle)**
- Ajouter champ `String qrCodeToken`
- Ajouter champ `Boolean isValidated`
- Ajouter champ `LocalDateTime validatedAt`

#### **BASE DE DONNÉES (PostgreSQL)**

**Table Reservation**
- Ajouter colonne `qr_code_token TEXT`
- Ajouter colonne `is_validated BOOLEAN DEFAULT FALSE`
- Ajouter colonne `validated_at TIMESTAMP`

#### **FRONTEND MOBILE (React Native)**

**Dépendances à ajouter :**
- `package.json` : Ajouter :
  - `react-native-qrcode-svg` version 6.2.0
  - `react-native-camera` version 4.2.1

**Nouveaux écrans à créer :**
- `QRCodeDisplay.jsx` - Affichage du QR code pour le locataire avec :
  - Génération et affichage du QR code
  - Informations de la réservation
  - Bouton partager
  
- `QRCodeScanner.jsx` - Scanner pour le propriétaire avec :
  - Caméra pour scanner
  - Validation du code
  - Confirmation d'arrivée

**Écrans à modifier :**
- `ReservationList.jsx` - Ajouter bouton "Afficher QR Code" pour réservations à venir/en cours

**Nouveau service à créer :**
- `qrcodeService.js` avec méthodes :
  - `getReservationQRCode(reservationId)`
  - `validateQRCode(token)`

---

## Fonctionnalité 8 : Authentification Sociale

### 🎯 QUOI
Permettre la connexion via Google, Facebook et Apple (iOS).

### 🤔 POURQUOI
- Simplification de l'inscription
- Réduction des frictions
- Pas de gestion de mot de passe
- Augmentation du taux de conversion

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Dépendances à ajouter :**
- `pom.xml` : Ajouter `spring-boot-starter-oauth2-client`

**Configuration à ajouter :**
- `application.properties` : Configurer OAuth2 pour :
  - Google (client-id, client-secret)
  - Facebook (client-id, client-secret)
  - Apple (configuration spécifique)

**Modifications à apporter :**

**AuthenticationController.java**
- Ajouter endpoint `POST /api/auth/oauth/google` - Authentification Google
- Ajouter endpoint `POST /api/auth/oauth/facebook` - Authentification Facebook
- Ajouter endpoint `POST /api/auth/oauth/apple` - Authentification Apple

**AuthenticationService.java**
- Ajouter méthode `authenticateWithGoogle(token)`
- Ajouter méthode `authenticateWithFacebook(token)`
- Ajouter méthode `authenticateWithApple(token)`
- Logique : vérifier token, créer ou récupérer utilisateur, générer JWT

**Users.java (Modèle)**
- Ajouter champ `OAuthProvider oauthProvider` (enum: GOOGLE, FACEBOOK, APPLE, LOCAL)
- Ajouter champ `String oauthId`
- Ajouter champ `String profilePictureUrl`

#### **BASE DE DONNÉES (PostgreSQL)**

**Table Users**
- Ajouter colonne `oauth_provider VARCHAR(50)`
- Ajouter colonne `oauth_id VARCHAR(255)`
- Ajouter colonne `profile_picture_url TEXT`

#### **FRONTEND MOBILE (React Native)**

**Dépendances à ajouter :**
- `package.json` : Ajouter :
  - `@react-native-google-signin/google-signin` version 10.1.0
  - `react-native-fbsdk-next` version 12.1.0
  - `@invertase/react-native-apple-authentication` version 2.3.0

**Configuration à ajouter :**
- Configurer Google Sign-In (Android/iOS)
- Configurer Facebook SDK
- Configurer Apple Sign-In (iOS uniquement)

**Nouveaux composants à créer :**
- `SocialLoginButtons.jsx` - Boutons de connexion sociale

**Écrans à modifier :**
- `Login.jsx` - Ajouter boutons de connexion sociale
- `Registration.jsx` - Ajouter boutons d'inscription sociale

**Services à modifier :**
- `authService.js` - Ajouter méthodes :
  - `loginWithGoogle(googleToken)`
  - `loginWithFacebook(facebookToken)`
  - `loginWithApple(appleToken)`

---

## Fonctionnalité 9 : Notifications Push

### 🎯 QUOI
Envoyer des notifications push aux utilisateurs pour les événements importants.

### 🤔 POURQUOI
- Engagement utilisateur
- Notifications en temps réel
- Rappels importants (réservation à venir, etc.)
- Amélioration de la communication

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Dépendances à ajouter :**
- `pom.xml` : Ajouter `firebase-admin` version 9.2.0

**Configuration à ajouter :**
- Ajouter fichier `firebase-service-account.json` (credentials Firebase)
- `application.properties` : Configurer chemin vers credentials

**Nouveaux fichiers à créer :**
- `DeviceToken.java` (Modèle) avec champs :
  - `Id_Device_token`
  - `token`
  - `deviceType` (ANDROID, IOS)
  - `createdAt`, `lastUsedAt`
  - Relation vers `Users`

- `Notification.java` (Modèle) avec champs :
  - `Id_Notification`
  - `title`, `body`, `type`
  - `isRead`
  - `createdAt`
  - Relations vers `Users` et `Reservation`

- `NotificationService.java` avec méthodes :
  - `sendPushNotification(deviceToken, title, body, data)`
  - `notifyReservationConfirmed(reservation)`
  - `notifyNewReservationRequest(request)`
  - `notifyReservationStartingSoon(reservation)`

- `NotificationController.java` avec endpoints :
  - `POST /api/notifications/register-device` - Enregistrer token device
  - `GET /api/notifications/user/{userId}` - Notifications d'un utilisateur
  - `PUT /api/notifications/{id}/mark-read` - Marquer comme lu

- `DeviceTokenRepository.java`
- `NotificationRepository.java`

**Modifications à apporter :**

**ReservationService.java**
- Intégrer envoi de notifications dans workflow réservations

#### **BASE DE DONNÉES (PostgreSQL)**

**Nouvelles tables à créer :**

**Device_tokens**
- `Id_Device_token SERIAL PRIMARY KEY`
- `token TEXT NOT NULL UNIQUE`
- `device_type VARCHAR(20) NOT NULL`
- `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- `last_used_at TIMESTAMP`
- `Id_Users INTEGER` (FK vers Users)

**Notifications**
- `Id_Notification SERIAL PRIMARY KEY`
- `title VARCHAR(255) NOT NULL`
- `body TEXT NOT NULL`
- `type VARCHAR(50) NOT NULL`
- `is_read BOOLEAN DEFAULT FALSE`
- `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- `Id_Users INTEGER` (FK vers Users)
- `Id_Reservation INTEGER` (FK vers Reservation)

#### **FRONTEND MOBILE (React Native)**

**Dépendances à ajouter :**
- `package.json` : Ajouter :
  - `@react-native-firebase/app` version 18.7.0
  - `@react-native-firebase/messaging` version 18.7.0

**Configuration à ajouter :**
- Configurer Firebase (Android: google-services.json, iOS: GoogleService-Info.plist)
- Demander permissions notifications

**Nouveaux services à créer :**
- `notificationService.js` avec méthodes :
  - `requestPermission()`
  - `getToken()`
  - `registerDevice(userId)`
  - `onNotificationReceived(callback)`
  - `onNotificationOpened(callback)`

**Nouveaux écrans à créer :**
- `Notifications.jsx` - Liste des notifications avec :
  - Badge non lues
  - Marquer comme lu
  - Navigation vers élément concerné

**Fichiers à modifier :**
- `App.tsx` - Initialiser Firebase et enregistrer device token au démarrage

---

## Fonctionnalité 10 : Gestion des Litiges et Remboursements

### 🎯 QUOI
Permettre aux utilisateurs de signaler des problèmes et demander des remboursements.

### 🤔 POURQUOI
- Résolution de conflits
- Service client intégré
- Traçabilité des problèmes
- Amélioration de la qualité

### 🔧 COMMENT

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- `Dispute.java` (Modèle) avec champs :
  - `Id_Dispute`
  - `description`
  - `status` (OPEN, IN_REVIEW, RESOLVED, CLOSED)
  - `type` (PARKING_UNAVAILABLE, WRONG_DESCRIPTION, PAYMENT_ISSUE, OTHER)
  - `adminResponse`
  - `refundAmount`
  - `createdAt`, `resolvedAt`
  - Relations vers `Reservation` et `reporter` (Users)

- `DisputeController.java` avec endpoints :
  - `POST /api/disputes` - Créer un litige
  - `GET /api/disputes/user/{userId}` - Litiges d'un utilisateur
  - `GET /api/disputes/reservation/{reservationId}` - Litiges d'une réservation
  - `PUT /api/disputes/{id}/resolve` - Résoudre un litige (admin)
  - `POST /api/disputes/{id}/refund` - Traiter un remboursement (admin)

- `DisputeService.java` avec logique :
  - Création de litige
  - Validation
  - Résolution
  - Intégration avec PaymentService pour remboursements

- `DisputeRepository.java` avec méthodes :
  - `findByReporter_IdUsers(int userId)`
  - `findByReservation_IdReservation(int reservationId)`
  - `findByStatus(DisputeStatus status)`

#### **BASE DE DONNÉES (PostgreSQL)**

**Nouvelle table à créer :**
- `Disputes` avec colonnes :
  - `Id_Dispute SERIAL PRIMARY KEY`
  - `description TEXT NOT NULL`
  - `status VARCHAR(50) DEFAULT 'OPEN'`
  - `type VARCHAR(50) NOT NULL`
  - `admin_response TEXT`
  - `refund_amount NUMERIC(15,2)`
  - `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
  - `resolved_at TIMESTAMP`
  - `Id_Reservation INTEGER` (FK vers Reservation)
  - `Id_Reporter INTEGER` (FK vers Users)

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
- `ReportIssue.jsx` - Formulaire de signalement avec :
  - Sélection du type de problème
  - Description détaillée
  - Upload de photos (optionnel)
  
- `MyDisputes.jsx` - Liste des litiges avec :
  - Statut de chaque litige
  - Réponses de l'admin
  - Historique

**Écrans à modifier :**
- `ReservationList.jsx` - Ajouter bouton "Signaler un problème" pour réservations en cours/terminées

**Nouveau service à créer :**
- `disputeService.js` avec méthodes :
  - `createDispute(disputeData)`
  - `getUserDisputes(userId)`
  - `getDisputeDetails(disputeId)`

---

## RÉCAPITULATIF GLOBAL

### 📊 STATISTIQUES DES MODIFICATIONS

#### **BACKEND (Spring Boot)**

**Nouveaux fichiers à créer :**
- 7 Controllers
- 6 Services
- 5 Modèles (entités JPA)
- 5 Repositories
- 10+ DTOs

**Fichiers à modifier :**
- 3 Controllers existants
- 2 Services existants
- 4 Modèles existants
- 1 fichier de configuration (pom.xml)

**Dépendances Maven à ajouter :**
- stripe-java
- ZXing (core + javase)
- spring-boot-starter-oauth2-client
- firebase-admin

---

#### **BASE DE DONNÉES (PostgreSQL)**

**Nouvelles tables à créer :**
1. User_note
2. Reservation_requests
3. Device_tokens
4. Notifications
5. Disputes

**Tables à modifier :**
1. Users (5 nouvelles colonnes)
2. Parking (3 nouvelles colonnes)
3. Announcements (2 nouvelles colonnes)
4. Reservation (5 nouvelles colonnes)

**Total :** 5 nouvelles tables + 15 nouvelles colonnes

---

#### **FRONTEND MOBILE (React Native)**

**Nouveaux écrans à créer :**
1. MyParkings.jsx
2. AddEditParking.jsx
3. MyAnnouncements.jsx
4. CreateAnnouncement.jsx
5. ReservationRequests.jsx
6. UserDashboard.jsx
7. QRCodeDisplay.jsx
8. QRCodeScanner.jsx
9. PaymentScreen.jsx
10. Notifications.jsx
11. ReportIssue.jsx
12. MyDisputes.jsx

**Nouveaux composants à créer :**
- RatingModal.jsx
- ReviewCard.jsx
- SocialLoginButtons.jsx
- StatCard.jsx
- ActivityItem.jsx
- EarningsChart.jsx

**Nouveaux services à créer :**
1. ownerService.js
2. announcementService.js
3. reservationRequestService.js
4. dashboardService.js
5. ratingService.js
6. paymentService.js
7. qrcodeService.js
8. notificationService.js
9. disputeService.js

**Écrans à modifier :**
- Reservation.jsx
- ReservationList.jsx
- ParkingDetails.jsx
- Login.jsx
- Registration.jsx
- App.tsx

**Dépendances npm à ajouter :**
- @stripe/stripe-react-native
- react-native-qrcode-svg
- react-native-camera
- @react-native-google-signin/google-signin
- react-native-fbsdk-next
- @invertase/react-native-apple-authentication
- @react-native-firebase/app
- @react-native-firebase/messaging

---

### 🎯 ORDRE D'IMPLÉMENTATION RECOMMANDÉ

**Phase 1 - Fonctionnalités Propriétaire (Priorité HAUTE)**
1. Gestion des Parkings Propriétaire
2. Gestion des Publications
3. Gestion des Demandes de Réservation

**Phase 2 - Engagement Utilisateur (Priorité MOYENNE)**
4. Tableau de Bord Utilisateur
5. Notation des Utilisateurs

**Phase 3 - Monétisation (Priorité HAUTE)**
6. Paiement en Ligne (Stripe)

**Phase 4 - Sécurité et Validation (Priorité MOYENNE)**
7. QR Code pour Validation

**Phase 5 - Acquisition (Priorité MOYENNE)**
8. Authentification Sociale

**Phase 6 - Rétention (Priorité MOYENNE)**
9. Notifications Push

**Phase 7 - Support (Priorité BASSE)**
10. Gestion des Litiges et Remboursements

---

### ✅ NOTES IMPORTANTES

**Points forts de l'architecture actuelle :**
- Backend Spring Boot bien structuré avec séparation des responsabilités
- Base de données PostgreSQL avec PostGIS pour la géolocalisation
- Frontend React Native moderne avec architecture en services
- Authentification JWT déjà implémentée
- Système de statuts de réservations automatisé

**Recommandations générales :**
- Implémenter les tests unitaires et d'intégration au fur et à mesure
- Documenter chaque nouvelle API avec Swagger/OpenAPI
- Versionner les APIs (ex: /api/v1/...)
- Mettre en place un système de logs centralisé
- Configurer des environnements séparés (dev, staging, prod)
- Sécuriser les clés API (Stripe, Firebase, OAuth) avec variables d'environnement

**Prérequis techniques :**
- Compte Stripe (mode test puis production)
- Projet Firebase (pour notifications push)
- Applications OAuth configurées (Google, Facebook, Apple Developer)
- Serveur de staging pour tests

---

**Document généré le :** 22 décembre 2025  
**Version :** 1.0  
**Auteur :** Équipe de développement uPark
