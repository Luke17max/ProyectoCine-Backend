package com.cine.ms_confiteria.repository;
 
import com.cine.ms_confiteria.model.Confiteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface ConfiteriaRepository extends JpaRepository<Confiteria, Long> {
    List<Confiteria> findByCategoriaIgnoreCase(String categoria);
}
