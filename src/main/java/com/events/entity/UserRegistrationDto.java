package com.events.entity;

import jakarta.validation.constraints.*;

/**
 * DTO du formulaire d'inscription.
 * Séparé de l'entité User pour capturer la confirmation du mot de passe
 * et isoler la validation propre au formulaire.
 */
public class UserRegistrationDto {

    @NotBlank @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, max = 50)
    private String password;

    @NotBlank
    private String confirmPassword;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public boolean isPasswordStrong() {
        if (password == null || password.length() < 8) return false;
        return password.chars().anyMatch(Character::isUpperCase)
            && password.chars().anyMatch(Character::isDigit);
    }

    // Getters / Setters
    public String getFirstName()               { return firstName; }
    public void   setFirstName(String v)       { this.firstName = v; }
    public String getLastName()                { return lastName; }
    public void   setLastName(String v)        { this.lastName = v; }
    public String getEmail()                   { return email; }
    public void   setEmail(String v)           { this.email = v; }
    public String getPassword()                { return password; }
    public void   setPassword(String v)        { this.password = v; }
    public String getConfirmPassword()         { return confirmPassword; }
    public void   setConfirmPassword(String v) { this.confirmPassword = v; }
}
