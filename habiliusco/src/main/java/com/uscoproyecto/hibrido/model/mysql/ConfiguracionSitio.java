package com.uscoproyecto.hibrido.model.mysql;

import jakarta.persistence.*;

@Entity
@Table(name = "configuraciones_sitio")
public class ConfiguracionSitio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String clave;

    @Column(nullable = false, length = 500)
    private String valor;

    @Column(length = 200)
    private String descripcion;

    // ---- Getters y Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
