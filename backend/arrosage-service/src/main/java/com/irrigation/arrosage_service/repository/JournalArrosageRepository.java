package com.irrigation.arrosage_service.repository;

import com.irrigation.arrosage_service.entity.JournalArrosage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalArrosageRepository extends JpaRepository<JournalArrosage, Long> {
    
    List<JournalArrosage> findByProgrammeId(Long programmeId);
    
    List<JournalArrosage> findByDateExecutionBetween(LocalDateTime start, LocalDateTime end);
}
