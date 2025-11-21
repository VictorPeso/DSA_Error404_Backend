package edu.upc.dsa.models.dto;

public class RegisterCredentials {

    private String nombre;
    private String password;
    private String email;

    public RegisterCredentials() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}