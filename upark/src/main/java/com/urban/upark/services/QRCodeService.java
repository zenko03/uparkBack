package com.urban.upark.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QRCodeService {

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    /**
     * Génère un token QR unique
     * @return Token UUID unique
     */
    public String generateQRToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Génère un QR Code sous forme d'image PNG (byte array)
     * @param qrToken Token à encoder dans le QR
     * @return Byte array de l'image PNG
     * @throws WriterException Si erreur lors de la génération
     * @throws IOException Si erreur lors de l'écriture de l'image
     */
    public byte[] generateQRCodeImage(String qrToken) throws WriterException, IOException {
        // Configuration du QR Code
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        // Création du QR Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            qrToken,
            BarcodeFormat.QR_CODE,
            QR_CODE_WIDTH,
            QR_CODE_HEIGHT,
            hints
        );

        // Conversion en image PNG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Génère un token formaté pour le QR Code avec métadonnées
     * @param reservationId ID de la réservation
     * @return Token formaté
     */
    public String generateFormattedQRToken(int reservationId) {
        String uuid = UUID.randomUUID().toString();
        // Format: RES-{reservationId}-{UUID}
        return String.format("RES-%d-%s", reservationId, uuid);
    }

    /**
     * Valide le format d'un token QR
     * @param qrToken Token à valider
     * @return true si le format est valide
     */
    public boolean isValidQRTokenFormat(String qrToken) {
        if (qrToken == null || qrToken.isEmpty()) {
            return false;
        }
        
        // Vérifier le format RES-{id}-{uuid}
        if (qrToken.startsWith("RES-")) {
            String[] parts = qrToken.split("-");
            return parts.length == 3;
        }
        
        // Accepter aussi les UUID simples pour rétrocompatibilité
        try {
            UUID.fromString(qrToken);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrait l'ID de réservation depuis un token formaté
     * @param qrToken Token formaté
     * @return ID de réservation ou -1 si non trouvé
     */
    public int extractReservationIdFromToken(String qrToken) {
        if (qrToken == null || !qrToken.startsWith("RES-")) {
            return -1;
        }
        
        try {
            String[] parts = qrToken.split("-");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        
        return -1;
    }
}
