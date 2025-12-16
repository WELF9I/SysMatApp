package com.institut.sysmat.desktop.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.institut.sysmat.desktop.model.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    
    private final ApiService apiService = new ApiService();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    
    public Service<ObservableList<Reservation>> getUserReservations() {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Reservation>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Reservation> call() throws Exception {
                        updateMessage("Chargement de vos réservations...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations/my",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Reservation> reservations = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Reservation reservation = parseReservation(element.getAsJsonObject());
                                reservations.add(reservation);
                            }
                            
                            return FXCollections.observableArrayList(reservations);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<ObservableList<Reservation>> getAllReservations() {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Reservation>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Reservation> call() throws Exception {
                        updateMessage("Chargement de toutes les réservations...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Reservation> reservations = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Reservation reservation = parseReservation(element.getAsJsonObject());
                                reservations.add(reservation);
                            }
                            
                            return FXCollections.observableArrayList(reservations);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<ObservableList<Reservation>> getPendingReservations() {
        return new Service<>() {
            @Override
            protected Task<ObservableList<Reservation>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<Reservation> call() throws Exception {
                        updateMessage("Chargement des réservations en attente...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations/pending",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonArray dataArray = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonArray();
                            
                            List<Reservation> reservations = new ArrayList<>();
                            for (JsonElement element : dataArray) {
                                Reservation reservation = parseReservation(element.getAsJsonObject());
                                reservations.add(reservation);
                            }
                            
                            return FXCollections.observableArrayList(reservations);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<Reservation> createReservation(ReservationRequest request) {
        return new Service<>() {
            @Override
            protected Task<Reservation> createTask() {
                return new Task<>() {
                    @Override
                    protected Reservation call() throws Exception {
                        updateMessage("Création de la réservation...");
                        
                        JsonObject jsonRequest = new JsonObject();
                        jsonRequest.addProperty("materielId", request.getMaterielId());
                        jsonRequest.addProperty("quantite", request.getQuantite());
                        jsonRequest.addProperty("dateDebut", request.getDateDebut().format(formatter));
                        jsonRequest.addProperty("dateFin", request.getDateFin().format(formatter));
                        jsonRequest.addProperty("motifUtilisation", request.getMotifUtilisation());
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations",
                            ApiService.HttpMethod.POST,
                            jsonRequest
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonObject data = JsonParser.parseString(response.getData().toString())
                                    .getAsJsonObject();
                            return parseReservation(data);
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    public Service<Boolean> updateReservationStatus(Long reservationId, String status) {
        return new Service<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        updateMessage("Mise à jour du statut...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations/" + reservationId + "/status?status=" + status,
                            ApiService.HttpMethod.PUT,
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
    
    public Service<Long> getPendingReservationsCount() {
        return new Service<>() {
            @Override
            protected Task<Long> createTask() {
                return new Task<>() {
                    @Override
                    protected Long call() throws Exception {
                        updateMessage("Calcul des réservations en attente...");
                        
                        ApiService.ApiResponse response = apiService.executeRequest(
                            "/reservations/stats/pending-count",
                            ApiService.HttpMethod.GET,
                            null
                        );
                        
                        if (response.isSuccess() && response.getData() != null) {
                            JsonElement dataElement = (JsonElement) response.getData();
                            return dataElement.getAsLong();
                        } else {
                            throw new Exception(response.getMessage());
                        }
                    }
                };
            }
        };
    }
    
    private Reservation parseReservation(JsonObject json) {
        Reservation reservation = new Reservation();
        reservation.setId(json.get("id").getAsLong());
        reservation.setUtilisateurId(json.get("utilisateurId").getAsLong());
        reservation.setUtilisateurNom(json.get("utilisateurNom").getAsString());
        reservation.setUtilisateurPrenom(json.get("utilisateurPrenom").getAsString());
        reservation.setMaterielId(json.get("materielId").getAsLong());
        reservation.setMaterielNom(json.get("materielNom").getAsString());
        reservation.setQuantite(json.get("quantite").getAsInt());
        reservation.setDateDebut(LocalDateTime.parse(json.get("dateDebut").getAsString(), formatter));
        reservation.setDateFin(LocalDateTime.parse(json.get("dateFin").getAsString(), formatter));
        reservation.setStatut(json.get("statut").getAsString());
        reservation.setMotifUtilisation(json.get("motifUtilisation").getAsString());
        reservation.setDateReservation(LocalDateTime.parse(json.get("dateReservation").getAsString(), formatter));
        
        if (json.has("dateValidation") && !json.get("dateValidation").isJsonNull()) {
            reservation.setDateValidation(LocalDateTime.parse(json.get("dateValidation").getAsString(), formatter));
        }
        
        return reservation;
    }
    
    // Classe pour les requêtes de réservation
    public static class ReservationRequest {
        private Long materielId;
        private int quantite;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String motifUtilisation;
        
        public Long getMaterielId() { return materielId; }
        public void setMaterielId(Long materielId) { this.materielId = materielId; }
        
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
        
        public LocalDateTime getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
        
        public LocalDateTime getDateFin() { return dateFin; }
        public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
        
        public String getMotifUtilisation() { return motifUtilisation; }
        public void setMotifUtilisation(String motifUtilisation) { this.motifUtilisation = motifUtilisation; }
    }
}