package com.cine.ms_funciones.controller;
 
import com.cine.ms_funciones.dto.FuncionDTO;
import com.cine.ms_funciones.service.IFuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@Slf4j
@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionController {
 
    private final IFuncionService service;
 
    @GetMapping
    public ResponseEntity<List<FuncionDTO>> listar() {
        log.info("REST: Petición GET a /api/funciones");
        return ResponseEntity.ok(service.listarTodas());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<FuncionDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/funciones/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }
 
    @PostMapping
    public ResponseEntity<FuncionDTO> crear(@Valid @RequestBody FuncionDTO dto) {
        log.info("REST: Petición POST a /api/funciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<FuncionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody FuncionDTO dto) {
        log.info("REST: Petición PUT a /api/funciones/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/funciones/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
