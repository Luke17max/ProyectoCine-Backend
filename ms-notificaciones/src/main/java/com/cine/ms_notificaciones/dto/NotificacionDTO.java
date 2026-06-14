package com.cine.ms_notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionDTO {
    private Long id;

    @NotNull(message = "El ID de la reserva es obligatorio")
    private Long reservaId;

    private Long pagoId;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;

    @NotBlank(message = "El mensaje de la notificación es obligatorio")
    private String mensaje;

    private LocalDateTime fechaCreacion;
}
