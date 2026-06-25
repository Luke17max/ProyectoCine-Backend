package com.cine.ms_salas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cine.ms_salas.model.Sala;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long>{
    List<Sala> findBySucursalId(Long sucursalId);
}
