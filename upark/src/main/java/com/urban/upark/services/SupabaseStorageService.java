package com.urban.upark.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url:https://fbpefbjoxzkxombdcqif.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZicGVmYmpveHpreG9tYmRjcWlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM0NDY4NTUsImV4cCI6MjA3OTAyMjg1NX0.R3m5_xHHkh-OH_GNY-DnV6RF5oRGNn6ED7pr5LeIFO0}")
    private String supabaseKey;

    /**
     * Upload un fichier vers Supabase Storage
     * @param imageBase64 Image encodée en base64
     * @param fileName Nom du fichier (avec chemin: userId/parkingId/filename.jpg)
     * @return URL publique du fichier uploadé
     */
    public String uploadFile(String imageBase64, String fileName) {
        try {
            RestTemplate restTemplate = new RestTemplate(); // Créer l'instance ici
            
            // Décoder base64
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);

            // Construire l'URL de l'API Storage
            String uploadUrl = supabaseUrl + "/storage/v1/object/parking-images/" + fileName;

            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("apikey", supabaseKey);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Créer la requête
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

            // Envoyer la requête POST
            ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Construire l'URL publique
                String publicUrl = supabaseUrl + "/storage/v1/object/public/parking-images/" + fileName;
                System.out.println("✅ Image uploadée vers Supabase: " + publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Erreur upload Supabase: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur upload Supabase: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'upload vers Supabase", e);
        }
    }
}
