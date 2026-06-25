package com.cine.ms_peliculas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cine.ms_peliculas.model.Pelicula;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long>{
    //Jparepository ya incluye los métodos básicos (save, findById, findAll, delete)
}
