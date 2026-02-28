package com.juliana.api_juliana.repositories;

import com.juliana.api_juliana.entities.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Integer>{

    // Retorna todos los servicios que estan habilitados
    @Query("""
            SELECT a FROM Treatment a
            WHERE a.state IN ('AVAILABLE')
            """)
    List<Treatment> findAvailableTreatments();
}
