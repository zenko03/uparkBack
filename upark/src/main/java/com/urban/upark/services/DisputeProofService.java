package com.urban.upark.services;

import com.urban.upark.dto.dispute.DisputeProofUploadRequest;
import com.urban.upark.models.Dispute;
import com.urban.upark.models.DisputeProof;
import com.urban.upark.repositories.DisputeProofRepository;
import com.urban.upark.repositories.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisputeProofService {

    private final DisputeProofRepository disputeProofRepository;
    private final DisputeRepository disputeRepository;
    private final SupabaseStorageService supabaseStorageService;

    public List<DisputeProof> getAllDisputeProofs() {
        return disputeProofRepository.findAll();
    }

    public List<DisputeProof> getProofsByDisputeId(Long disputeId) {
        return disputeProofRepository.findByDispute_Id(disputeId);
    }

    public Optional<DisputeProof> getDisputeProofById(Long id) {
        return disputeProofRepository.findById(id);
    }

    public DisputeProof createDisputeProof(DisputeProof disputeProof) {
        return disputeProofRepository.save(disputeProof);
    }

    public DisputeProof updateDisputeProof(Long id, DisputeProof disputeProofDetails) {
        return disputeProofRepository.findById(id).map(proof -> {
            proof.setProofUrl(disputeProofDetails.getProofUrl());
            proof.setDispute(disputeProofDetails.getDispute());
            return disputeProofRepository.save(proof);
        }).orElse(null);
    }

    public void deleteDisputeProof(Long id) {
        disputeProofRepository.deleteById(id);
    }

    /**
     * Upload une image de preuve vers Supabase et sauvegarder les métadonnées
     */
    @Transactional
    public DisputeProof uploadAndSaveProof(Long disputeId, DisputeProofUploadRequest request) {
        try {
            System.out.println("📤 Upload preuve pour litige " + disputeId + " - User: " + request.getUserId());
            
            // Vérifier que le litige existe
            Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));
            
            // Générer le nom de fichier avec chemin
            long timestamp = System.currentTimeMillis();
            String extension = request.getFileName().contains(".") 
                ? request.getFileName().substring(request.getFileName().lastIndexOf('.'))
                : ".jpg";
            String fileName = "disputes/" + request.getUserId() + "/" + disputeId + "/" + timestamp + extension;
            
            // Upload vers Supabase Storage
            String publicUrl = supabaseStorageService.uploadFile(request.getImageBase64(), fileName);
            
            // Sauvegarder les métadonnées en BDD
            DisputeProof proof = new DisputeProof();
            proof.setDispute(dispute);
            proof.setProofUrl(publicUrl);
            
            DisputeProof savedProof = disputeProofRepository.save(proof);
            System.out.println(" Preuve sauvegardée - ID: " + savedProof.getId());
            
            return savedProof;
        } catch (Exception e) {
            System.err.println("Erreur: Erreur upload preuve: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'upload de la preuve", e);
        }
    }
}
