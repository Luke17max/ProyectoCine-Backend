package com.cine.ms_reservas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; 

    @Column(name = "funcion_id", nullable = false)
    private Long funcionId; 

    @Column(name = "cantidad_asientos", nullable = false)
    private Integer cantidadAsientos;

    @Column(nullable = false, length = 20)
    private String estado; // PENDIENTE, PAGADA, CANCELADA

}
