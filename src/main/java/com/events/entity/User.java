package com.events.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entité JPA : représente un étudiant.
 * Hibernate crée/met à jour la table "users" dans MySQL.
 *
 * Note : on utilise jakarta.persistence.* (Jakarta EE 10)
 * et non plus javax.persistence.* (Java EE — ancienne version).
 */
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;

    /** Identifiant de connexion — doit être unique. */
    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Mot de passe HACHÉ en BCrypt — jamais en clair. */
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructeurs
    public User() {}
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.password  = password;
    }

    public String getFullName() { return firstName + " " + lastName; }

    // Getters / Setters
    public Long          getId()          { return id; }
    public void          setId(Long v)    { this.id = v; }
    public String        getFirstName()   { return firstName; }
    public void          setFirstName(String v) { this.firstName = v; }
    public String        getLastName()    { return lastName; }
    public void          setLastName(String v)  { this.lastName = v; }
    public String        getEmail()       { return email; }
    public void          setEmail(String v)     { this.email = v; }
    public String        getPassword()    { return password; }
    public void          setPassword(String v)  { this.password = v; }
    public String        getRole()        { return role; }
    public void          setRole(String v)      { this.role = v; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
