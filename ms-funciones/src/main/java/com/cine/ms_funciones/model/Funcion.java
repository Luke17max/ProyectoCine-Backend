package com.cine.ms_funciones.model;
 
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Data
@Entity
@Table(name = "funciones")
public class Funcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
 
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;
 
    @Column(name = "pelicula_id", nullable = false)
    private Long peliculaId;
 
    @Column(name = "sala_id", nullable = false)
    private Long salaId;
}
