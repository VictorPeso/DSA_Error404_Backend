package edu.upc.dsa.models;

import edu.upc.dsa.util.RandomUtils;

import java.util.UUID;

public class Object {

    String id;
    String nombre;
    String descripcion;

    public Object() {

    }

    public Object(String nombre, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
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

}
