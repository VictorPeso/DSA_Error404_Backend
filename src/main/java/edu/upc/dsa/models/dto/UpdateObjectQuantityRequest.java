package edu.upc.dsa.models.dto;

public class UpdateObjectQuantityRequest {
    private String username;
    private String objectId;
    private Integer newQuantity;

    // Constructores
    public UpdateObjectQuantityRequest() {}

    public UpdateObjectQuantityRequest(String username, String objectId, Integer newQuantity) {
        this.username = username;
        this.objectId = objectId;
        this.newQuantity = newQuantity;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }
}
