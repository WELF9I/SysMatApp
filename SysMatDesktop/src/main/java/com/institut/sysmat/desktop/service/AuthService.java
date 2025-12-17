package com.institut.sysmat.desktop.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.institut.sysmat.desktop.model.Utilisateur;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class AuthService {
    
    private final ApiService apiService = new ApiService();
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    public Service<AuthResult> login(String email, String password) {
        return new Service<>() {
            @Override
            protected Task<AuthResult> createTask() {
                return new Task<>() {
                    @Override
                    protected AuthResult call() throws Exception {
                        updateMessage("Connexion en cours...");
                        
                        JsonObject loginRequest = new JsonObject();
                        loginRequest.addProperty("email", email);
                        loginRequest.addProperty("password", password);
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/auth/login", 
                            ApiService.HttpMethod.POST, 
                            loginRequest,
                            false
                        );
                        
                        if (response.isSuccess()) {
                            JsonObject data = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonObject();
                            
                            String token = data.get("token").getAsString();
                            String role = data.get("role").getAsString();
                            String nom = data.get("nom").getAsString();
                            String prenom = data.get("prenom").getAsString();
                            String userEmail = data.get("email").getAsString();
                            String departement = data.has("departement") && !data.get("departement").isJsonNull() 
                                    ? data.get("departement").getAsString() : "N/A";
                            
                            // Créer l'objet utilisateur
                            Utilisateur user = new Utilisateur();
                            user.setId(data.has("id") ? data.get("id").getAsLong() : 0L);
                            user.setNom(nom);
                            user.setPrenom(prenom);
                            user.setEmail(userEmail);
                            user.setRole(role);
                            user.setDepartement(departement);
                            
                            // Initialiser la session
                            sessionManager.initializeSession(token, user);
                            
                            return new AuthResult(true, "Connexion réussie", user);
                        } else {
                            return new AuthResult(false, response.getMessage(), null);
                        }
                    }
                };
            }
        };
    }
    
    public void logout() {
        sessionManager.clearSession();
    }
    
    public boolean isAuthenticated() {
        return sessionManager.isLoggedIn();
    }
    
    // Classe pour les résultats d'authentification
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final Utilisateur user;
        
        public AuthResult(boolean success, String message, Utilisateur user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Utilisateur getUser() { return user; }
    }
}