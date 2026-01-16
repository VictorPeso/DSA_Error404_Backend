package edu.upc.dsa.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    /**
     * Valida que el username cumpla con el formato requerido.
     * - Entre 3 y 20 caracteres
     * - Solo letras, numeros y guion bajo
     * 
     * @param username el nombre de usuario a validar
     * @throws IllegalArgumentException si el username es invalido
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede estar vacío");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "Username debe tener 3-20 caracteres alfanuméricos");
        }
    }
    
    /**
     * Valida que el email tenga un formato valido.
     * 
     * @param email el email a validar
     * @throws IllegalArgumentException si el email es invalido
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
    
    /**
     * Valida que un string no sea null ni vacio.
     * 
     * @param str el string a validar
     * @param fieldName nombre del campo para el mensaje de error
     * @throws IllegalArgumentException si el string es null o vacio
     */
    public static void validateNotEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío");
        }
    }
    
    /**
     * Valida que la contraseña cumpla con los requisitos minimos de seguridad.
     * - Minimo 8 caracteres
     * 
     * @param password la contraseña a validar
     * @throws IllegalArgumentException si la contraseña no cumple los requisitos
     */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres");
        }
    }
    
    /**
     * Valida que un valor numerico sea positivo.
     * 
     * @param value el valor a validar
     * @param fieldName nombre del campo para el mensaje de error
     * @throws IllegalArgumentException si el valor es negativo
     */
    public static void validatePositive(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " no puede ser negativo");
        }
    }
}
