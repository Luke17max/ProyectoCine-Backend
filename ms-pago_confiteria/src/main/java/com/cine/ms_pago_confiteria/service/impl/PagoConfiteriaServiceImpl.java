package com.cine.ms_pago_confiteria.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_pago_confiteria.client.ProductoClient;
import com.cine.ms_pago_confiteria.client.UsuarioClient;
import com.cine.ms_pago_confiteria.dto.PagoConfiteriaDTO;
import com.cine.ms_pago_confiteria.model.PagoConfiteria;
import com.cine.ms_pago_confiteria.repository.PagoConfiteriaRepository;
import com.cine.ms_pago_confiteria.service.IPagoConfiteriaService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoConfiteriaServiceImpl implements IPagoConfiteriaService {
    private final PagoConfiteriaRepository repository;
    private final UsuarioClient usuarioClient;
    private final ProductoClient productoClient;

    @Override
    public List<PagoConfiteriaDTO> listarTodos() {
        log.info("Capa Servicio: Listando todos los pagos de confitería");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PagoConfiteriaDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando pago de confitería ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Pago de confitería no encontrado"));
    }

    @Override
    public List<PagoConfiteriaDTO> buscarPorUsuario(Long usuarioId) {
        log.info("Capa Servicio: Buscando pagos para el usuario ID: {}", usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PagoConfiteriaDTO guardar(PagoConfiteriaDTO dto) {
        log.info("Capa Servicio: Guardando nuevo pago, validando dependencias cruzadas");
        validarUsuario(dto.getUsuarioId());
        validarProducto(dto.getProductoId());

        PagoConfiteria pago = mapToEntity(dto);
        return mapToDTO(repository.save(pago));
    }

    @Override
    public PagoConfiteriaDTO actualizar(Long id, PagoConfiteriaDTO dto) {
        log.info("Capa Servicio: Actualizando pago ID: {}", id);
        PagoConfiteria pago = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago de confitería no encontrado"));

        if (!pago.getUsuarioId().equals(dto.getUsuarioId())) validarUsuario(dto.getUsuarioId());
        if (!pago.getProductoId().equals(dto.getProductoId())) validarProducto(dto.getProductoId());

        pago.setUsuarioId(dto.getUsuarioId());
        pago.setProductoId(dto.getProductoId());
        pago.setCantidad(dto.getCantidad());
        pago.setTotalPagado(dto.getTotalPagado());

        return mapToDTO(repository.save(pago));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando pago ID: {}", id);
        repository.deleteById(id);
    }

    private void validarUsuario(Long id) {
        try {
            usuarioClient.obtenerUsuario(id);
            log.info("Validación exitosa: Usuario ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Usuario ID {} no encontrado", id);
            throw new RuntimeException("Error: El usuario con ID " + id + " no existe.");
        }
    }

    private void validarProducto(Long id) {
        try {
            productoClient.obtenerProducto(id);
            log.info("Validación exitosa: Producto ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Producto ID {} no encontrado", id);
            throw new RuntimeException("Error: El producto de confitería con ID " + id + " no existe.");
        }
    }

    private PagoConfiteriaDTO mapToDTO(PagoConfiteria p) {
        PagoConfiteriaDTO dto = new PagoConfiteriaDTO();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuarioId());
        dto.setProductoId(p.getProductoId());
        dto.setCantidad(p.getCantidad());
        dto.setTotalPagado(p.getTotalPagado());
        dto.setFechaCompra(p.getFechaCompra());
        return dto;
    }

    private PagoConfiteria mapToEntity(PagoConfiteriaDTO dto) {
        PagoConfiteria p = new PagoConfiteria();
        p.setUsuarioId(dto.getUsuarioId());
        p.setProductoId(dto.getProductoId());
        p.setCantidad(dto.getCantidad());
        p.setTotalPagado(dto.getTotalPagado());
        return p;
    }

}
