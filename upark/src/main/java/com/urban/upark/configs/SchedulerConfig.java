package com.urban.upark.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration pour activer les tâches planifiées (@Scheduled) dans l'application.
 * Permet l'exécution automatique des méthodes annotées avec @Scheduled,
 * notamment pour la mise à jour automatique des statuts de réservations.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Configuration activée automatiquement
    // Les beans avec @Scheduled seront exécutés selon leur configuration
}
