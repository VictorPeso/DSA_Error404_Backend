package edu.upc.dsa.models.dto;

import edu.upc.dsa.models.Objects;

public class GameObjectDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private Objects tipo;
    private int precio;
    private Integer cantidad;

    public GameObjectDTO() {
    }

    public GameObjectDTO(String id, String nombre, String descripcion, Objects tipo, int precio,
            Integer cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Objects getTipo() {
        return tipo;
    }

    public void setTipo(Objects tipo) {
        this.tipo = tipo;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
