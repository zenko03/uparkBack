package com.urban.upark.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageProxyController {

    @Value("${supabase.url:https://fbpefbjoxzkxombdcqif.supabase.co}")
    private String supabaseUrl;

    //proxy pour recuperer image via supabase
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String path) {
        try {
            // Construire l'URL complète vers Supabase
            String imageUrl = supabaseUrl + "/storage/v1/object/public/parking-images/" + path;
            
            System.out.println(" Proxy image depuis Supabase: " + imageUrl);
            
            // Récupérer l'image depuis Supabase
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Retourner l'image avec les bons headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setCacheControl(CacheControl.maxAge(7, java.util.concurrent.TimeUnit.DAYS)); // Cache 7 jours
                
                System.out.println(" Image proxiée avec succès (" + response.getBody().length + " bytes)");
                return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            System.err.println("Erreur: Erreur proxy image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
