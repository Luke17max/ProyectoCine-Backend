INSERT IGNORE INTO notificaciones (id, reserva_id, pago_id, tipo, mensaje, fecha_creacion) 
VALUES (1, 1, 1, 'CONFIRMACION_PAGO', 'Su pago ha sido procesado exitosamente. Disfrute la función.', NOW());

INSERT IGNORE INTO notificaciones (id, reserva_id, pago_id, tipo, mensaje, fecha_creacion) 
VALUES (2, 2, NULL, 'RECORDATORIO_RESERVA', 'Recuerde que tiene una reserva pendiente de pago.', NOW());