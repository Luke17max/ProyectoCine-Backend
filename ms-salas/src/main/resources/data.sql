-- Suponiendo que la sucursal ID 1 (Cine Center Mall) existe en ms-sucursales
INSERT IGNORE INTO salas (id, nombre, capacidad, sucursal_id) VALUES (1, 'Sala 1 - IMAX', 200, 1);
INSERT IGNORE INTO salas (id, nombre, capacidad, sucursal_id) VALUES (2, 'Sala 2 - 3D', 120, 1);