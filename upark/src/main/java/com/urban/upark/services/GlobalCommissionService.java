package com.urban.upark.services;

import com.urban.upark.models.GlobalCommission;
import com.urban.upark.repositories.GlobalCommissionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlobalCommissionService {

    private final GlobalCommissionRepository globalCommissionRepository;

    public List<GlobalCommission> findAll() {
        return globalCommissionRepository.findAll();
    }

    public Optional<GlobalCommission> findById(int id) {
        return globalCommissionRepository.findById(id);
    }

    public GlobalCommission save(GlobalCommission globalCommission) {
        return globalCommissionRepository.save(globalCommission);
    }

    public void deleteById(int id) {
        globalCommissionRepository.deleteById(id);
    }
    
    /**
     * Récupère la commission globale actuelle (la plus récente)
     */
    public Optional<GlobalCommission> getCurrentCommission() {
        List<GlobalCommission> commissions = globalCommissionRepository.findAll();
        if (commissions.isEmpty()) {
            return Optional.empty();
        }
        // Retourner la plus récente
        return Optional.of(commissions.get(commissions.size() - 1));
    }
    
    /**
     * Crée une nouvelle commission globale avec la date actuelle
     */
    public GlobalCommission createNewCommission(BigDecimal rate) {
        GlobalCommission commission = GlobalCommission.builder()
                .rate(rate)
                .creationDate(LocalDateTime.now())
                .build();
        return globalCommissionRepository.save(commission);
    }
    
    /**
     * Récupère l'historique des commissions trié par date décroissante
     */
    public List<GlobalCommission> getCommissionHistory() {
        List<GlobalCommission> commissions = globalCommissionRepository.findAll();
        // Trier par date décroissante (plus récent en premier)
        commissions.sort((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()));
        return commissions;
    }
}