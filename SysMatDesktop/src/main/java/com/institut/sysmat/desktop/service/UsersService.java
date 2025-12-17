package com.institut.sysmat.desktop.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.institut.sysmat.desktop.model.Utilisateur;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UsersService {
    
    private final ApiService apiService = new ApiService();
    private final Gson gson = new Gson();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Service<List<Utilisateur>> getAllUsers() {
        return new Service<>() {
            @Override
            protected Task<List<Utilisateur>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<Utilisateur> call() throws Exception {
                        updateMessage("Chargement des utilisateurs...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/utilisateurs",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (!response.isSuccess()) {
                            throw new Exception(response.getMessage());
                        }
                        
                        List<Utilisateur> users = new ArrayList<>();
                        if (response.getData() != null) {
                            JsonArray jsonArray = ((JsonElement) response.getData()).getAsJsonArray();
                            for (JsonElement element : jsonArray) {
                                users.add(parseUtilisateur(element.getAsJsonObject()));
                            }
                        }
                        
                        return users;
                    }
                };
            }
        };
    }

    public Service<Void> toggleUserStatus(Long userId) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Modification du statut...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/utilisateurs/" + userId + "/toggle-status",
                            ApiService.HttpMethod.PATCH,
                            null
                        );
                        
                        if (!response.isSuccess()) {
                            throw new Exception(response.getMessage());
                        }
                        
                        return null;
                    }
                };
            }
        };
    }

    public Service<Void> deleteUser(Long userId) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Suppression de l'utilisateur...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/utilisateurs/" + userId,
                            ApiService.HttpMethod.DELETE,
                            null
                        );
                        
                        if (!response.isSuccess()) {
                            throw new Exception(response.getMessage());
                        }
                        
                        return null;
                    }
                };
            }
        };
    }

    public Service<Utilisateur> createUser(Utilisateur user, String password) {
        return new Service<>() {
            @Override
            protected Task<Utilisateur> createTask() {
                return new Task<>() {
                    @Override
                    protected Utilisateur call() throws Exception {
                        updateMessage("Création de l'utilisateur...");
                        
                        // Create AuthRequest DTO
                        JsonObject authRequest = new JsonObject();
                        authRequest.addProperty("nom", user.getNom());
                        authRequest.addProperty("prenom", user.getPrenom());
                        authRequest.addProperty("email", user.getEmail());
                        authRequest.addProperty("motDePasse", password);
                        if (user.getDepartement() != null && !user.getDepartement().isEmpty()) {
                            authRequest.addProperty("departement", user.getDepartement());
                        }
                        
                        // Call API with role as query parameter
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/utilisateurs?role=" + user.getRole(),
                            ApiService.HttpMethod.POST,
                            gson.fromJson(authRequest, Object.class)
                        );
                        
                        if (!response.isSuccess()) {
                            throw new Exception(response.getMessage());
                        }
                        
                        // Parse response
                        if (response.getData() != null) {
                            JsonObject userData = ((JsonElement) response.getData()).getAsJsonObject();
                            return parseUtilisateur(userData);
                        }
                        
                        return user;
                    }
                };
            }
        };
    }

    public Service<Utilisateur> updateUser(Utilisateur user, String password) {
        return new Service<>() {
            @Override
            protected Task<Utilisateur> createTask() {
                return new Task<>() {
                    @Override
                    protected Utilisateur call() throws Exception {
                        updateMessage("Mise à jour de l'utilisateur...");
                        
                        // Create AuthRequest DTO
                        JsonObject authRequest = new JsonObject();
                        authRequest.addProperty("nom", user.getNom());
                        authRequest.addProperty("prenom", user.getPrenom());
                        authRequest.addProperty("email", user.getEmail());
                        
                        // Only include password if provided
                        if (password != null && !password.trim().isEmpty()) {
                            authRequest.addProperty("motDePasse", password);
                        } else {
                            // Backend requires password, use a placeholder or handle differently
                            authRequest.addProperty("motDePasse", "UNCHANGED");
                        }
                        
                        if (user.getDepartement() != null && !user.getDepartement().isEmpty()) {
                            authRequest.addProperty("departement", user.getDepartement());
                        }
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/utilisateurs/" + user.getId(),
                            ApiService.HttpMethod.PUT,
                            gson.fromJson(authRequest, Object.class)
                        );
                        
                        if (!response.isSuccess()) {
                            throw new Exception(response.getMessage());
                        }
                        
                        // Parse response
                        if (response.getData() != null) {
                            JsonObject userData = ((JsonElement) response.getData()).getAsJsonObject();
                            return parseUtilisateur(userData);
                        }
                        
                        return user;
                    }
                };
            }
        };
    }
    
    private Utilisateur parseUtilisateur(JsonObject json) {
        try {
            Utilisateur user = new Utilisateur();
            
            if (json.has("id") && !json.get("id").isJsonNull()) {
                user.setId(json.get("id").getAsLong());
            }
            
            if (json.has("nom") && !json.get("nom").isJsonNull()) {
                user.setNom(json.get("nom").getAsString());
            }
            
            if (json.has("prenom") && !json.get("prenom").isJsonNull()) {
                user.setPrenom(json.get("prenom").getAsString());
            }
            
            if (json.has("email") && !json.get("email").isJsonNull()) {
                user.setEmail(json.get("email").getAsString());
            }
            
            if (json.has("role") && !json.get("role").isJsonNull()) {
                user.setRole(json.get("role").getAsString());
            }
            
            if (json.has("actif") && !json.get("actif").isJsonNull()) {
                user.setActif(json.get("actif").getAsBoolean());
            }
            
            if (json.has("departement") && !json.get("departement").isJsonNull()) {
                user.setDepartement(json.get("departement").getAsString());
            }
            
            if (json.has("dateCreation") && !json.get("dateCreation").isJsonNull()) {
                String dateStr = json.get("dateCreation").getAsString();
                try {
                    user.setDateCreation(LocalDateTime.parse(dateStr, dateFormatter));
                } catch (Exception e) {
                    // If parsing fails, try without time
                    try {
                        user.setDateCreation(LocalDateTime.parse(dateStr));
                    } catch (Exception ex) {
                        // Ignore date parsing errors
                    }
                }
            }
            
            return user;
        } catch (Exception e) {
            System.err.println("Error parsing user: " + e.getMessage());
            System.err.println("JSON: " + json.toString());
            throw new RuntimeException("Failed to parse user data", e);
        }
    }
}
