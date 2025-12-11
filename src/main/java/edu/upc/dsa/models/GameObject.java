package edu.upc.dsa.models;

import java.util.UUID;

public class GameObject {
    private String id;
    private String nombre;
    private String descripcion;
    private Objects tipo;
    private int precio;

    public GameObject() {
    }

    public GameObject(String nombre, String descripcion, Objects tipo, int precio) {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.precio = precio;
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

}
