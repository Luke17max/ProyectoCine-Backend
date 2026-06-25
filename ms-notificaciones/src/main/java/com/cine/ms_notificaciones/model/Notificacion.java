package com.cine.ms_notificaciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(name = "pago_id")
    private Long pagoId; // Puede ser nulo si es solo un recordatorio de reserva

    @Column(nullable = false, length = 50)
    private String tipo; // Ej: CONFIRMACION_PAGO, RECORDATORIO_RESERVA, CANCELACION

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
