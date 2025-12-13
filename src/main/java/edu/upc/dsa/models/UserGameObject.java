package edu.upc.dsa.models;

public class UserGameObject {
    private GameObject gameObject;
    private Integer cantidad;

    public UserGameObject() {
    }

    public UserGameObject(GameObject gameObject, Integer cantidad) {
        this.gameObject = gameObject;
        this.cantidad = cantidad;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
