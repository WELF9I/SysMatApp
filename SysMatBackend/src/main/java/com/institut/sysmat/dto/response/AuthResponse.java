package com.institut.sysmat.dto.response;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String role;
    private String nom;
    private String prenom;
    private String email;
    private String departement;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, String role, String nom, String prenom, String email, String departement) {
        this.token = token;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.departement = departement;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }
}