package edu.upc.dsa.models.dto;

public class Team {
    String name;
    String avatar; // URL de la imagen
    int points;

    public Team() {} // Constructor vacío necesario

    public Team(String name, String avatar, int points) {
        this.name = name;
        this.avatar = avatar;
        this.points = points;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}