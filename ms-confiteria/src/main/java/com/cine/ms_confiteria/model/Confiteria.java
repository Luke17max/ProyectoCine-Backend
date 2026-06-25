package com.cine.ms_confiteria.model;
 
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
 
@Data
@Entity
@Table(name = "confiteria")
public class Confiteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String nombre;
 
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
 
    @Column(nullable = false)
    private Integer stock;
 
    @Column(nullable = false, length = 50)
    private String categoria;
}
