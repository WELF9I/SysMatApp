package com.institut.sysmat.desktop.service;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.model.Utilisateur;

public class SessionManager {
    
    private static SessionManager instance;
    
    private String authToken;
    private Utilisateur currentUser;
    private String userRole;
    
    private SessionManager() {
        // Singleton
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void initializeSession(String token, Utilisateur user) {
        this.authToken = token;
        this.currentUser = user;
        this.userRole = user.getRole();
    }
    
    public void clearSession() {
        this.authToken = null;
        this.currentUser = null;
        this.userRole = null;
    }
    
    public boolean isLoggedIn() {
        return authToken != null && !authToken.isEmpty();
    }
    
    public boolean isAdmin() {
        return AppConfig.ROLE_ADMIN.equals(userRole);
    }
    
    public boolean isProfesseur() {
        return AppConfig.ROLE_PROFESSEUR.equals(userRole);
    }
    
    // Getters
    public String getAuthToken() {
        return authToken;
    }
    
    public String getAuthorizationHeader() {
        return "Bearer " + authToken;
    }
    
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getNom() + " " + currentUser.getPrenom() : "";
    }
    
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }
    
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}