package com.institut.sysmat.desktop.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.institut.sysmat.desktop.model.Materiel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class MaterielService {
    
    private final ApiService apiService = new ApiService();
    
    public Service<ObservableList<Materiel>> getAllMateriels() {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Materiel>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Materiel> call() throws Exception {
                        updateMessage("Chargement des matériels...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Materiel> materiels = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Materiel materiel = apiService.parseData(element, Materiel.class);
                                materiels.add(materiel);
                            }
                            
                            return FXCollections.observableArrayList(materiels);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<ObservableList<Materiel>> getAvailableMateriels() {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Materiel>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Materiel> call() throws Exception {
                        updateMessage("Chargement des matériels disponibles...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels/available",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Materiel> materiels = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Materiel materiel = apiService.parseData(element, Materiel.class);
                                materiels.add(materiel);
                            }
                            
                            return FXCollections.observableArrayList(materiels);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<Materiel> createMateriel(Materiel materiel) {
        return new Service<>() {
            @Override
            protected Task<Materiel> createTask() {
                return new Task<>() {
                    @Override
                    protected Materiel call() throws Exception {
                        updateMessage("Création du matériel...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels",
                            ApiService.HttpMethod.POST,
                            materiel
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            return apiService.parseData(response.getData(), Materiel.class);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<Materiel> updateMateriel(Materiel materiel) {
        return new Service<>() {
            @Override
            protected Task<Materiel> createTask() {
                return new Task<>() {
                    @Override
                    protected Materiel call() throws Exception {
                        updateMessage("Mise à jour du matériel...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels/" + materiel.getId(),
                            ApiService.HttpMethod.PUT,
                            materiel
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            return apiService.parseData(response.getData(), Materiel.class);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<Boolean> deleteMateriel(Long materielId) {
        return new Service<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateMessage("Suppression du matériel...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels/" + materielId,
                            ApiService.HttpMethod.DELETE,
                            null
                        );
                        
                        if (response.isSuccess()) {
                            return true;
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<ObservableList<Materiel>> searchMateriels(String keyword) {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Materiel>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Materiel> call() throws Exception {
                        updateMessage("Recherche en cours...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/materiels/search?keyword=" + keyword,
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Materiel> materiels = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Materiel materiel = apiService.parseData(element, Materiel.class);
                                materiels.add(materiel);
                            }
                            
                            return FXCollections.observableArrayList(materiels);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
}