package edu.upc.dsa.models.dto;

public class AddObject {

    private String nombre;
    private String objectId;

    public AddObject() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}