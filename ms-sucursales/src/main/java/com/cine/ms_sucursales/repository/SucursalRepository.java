package com.cine.ms_sucursales.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cine.ms_sucursales.model.Sucursal;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long>{
    // Método de búsqueda personalizado
    List<Sucursal> findByCiudadIgnoreCase(String ciudad);
}
