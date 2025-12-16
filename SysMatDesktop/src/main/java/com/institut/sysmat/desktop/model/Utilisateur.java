package com.institut.sysmat.desktop.model;

public class Utilisateur {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String departement;
    private boolean actif;
    private java.time.LocalDateTime dateCreation;
    
    public Utilisateur() {}
    
    public Utilisateur(Long id, String nom, String prenom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
    
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public java.time.LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(java.time.LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public String getFullName() {
        return prenom + " " + nom;
    }
    
    @Override
    public String toString() {
        return getFullName() + " (" + email + ")";
    }
}