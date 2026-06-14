package com.cine.ms_pago_confiteria.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cine.ms_pago_confiteria.model.PagoConfiteria;

@Repository
public interface PagoConfiteriaRepository extends JpaRepository<PagoConfiteria, Long> {
    List<PagoConfiteria> findByUsuarioId(Long usuarioId);

}
