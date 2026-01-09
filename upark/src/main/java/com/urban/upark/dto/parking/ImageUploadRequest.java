package com.urban.upark.dto.parking;

import lombok.Data;

@Data
public class ImageUploadRequest {
    private String imageBase64;     // Image en base64
    private String fileName;         // Nom du fichier
    private Integer userId;          // ID de l'utilisateur
    private Boolean isPrimary;       // Image principale ou non
}
