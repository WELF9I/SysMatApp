package com.institut.sysmat.desktop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private Long materielId;
    private String materielNom;
    private int quantite;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private String motifUtilisation;
    private LocalDateTime dateReservation;
    private LocalDateTime dateValidation;
    
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public Reservation() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    
    public String getUtilisateurPrenom() { return utilisateurPrenom; }
    public void setUtilisateurPrenom(String utilisateurPrenom) { this.utilisateurPrenom = utilisateurPrenom; }
    
    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }
    
    public String getMaterielNom() { return materielNom; }
    public void setMaterielNom(String materielNom) { this.materielNom = materielNom; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getMotifUtilisation() { return motifUtilisation; }
    public void setMotifUtilisation(String motifUtilisation) { this.motifUtilisation = motifUtilisation; }
    
    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public String getFormattedDateDebut() {
        return dateDebut != null ? dateDebut.format(dateFormatter) : "";
    }
    
    public String getFormattedDateFin() {
        return dateFin != null ? dateFin.format(dateFormatter) : "";
    }
    
    public String getFormattedDateReservation() {
        return dateReservation != null ? dateReservation.format(dateFormatter) : "";
    }
    
    public String getFormattedDateValidation() {
        return dateValidation != null ? dateValidation.format(dateFormatter) : "";
    }
    
    public String getUtilisateurFullName() {
        return utilisateurPrenom + " " + utilisateurNom;
    }
    
    public String getStatusColor() {
        switch (statut) {
            case "EN_ATTENTE": return "#FFA726"; // Orange
            case "CONFIRMEE": return "#4CAF50";  // Vert
            case "EN_COURS": return "#2196F3";   // Bleu
            case "TERMINEE": return "#9E9E9E";   // Gris
            case "ANNULEE": return "#F44336";    // Rouge
            case "REFUSEE": return "#795548";    // Marron
            default: return "#000000";
        }
    }
    
    public String getStatusText() {
        switch (statut) {
            case "EN_ATTENTE": return "En attente";
            case "CONFIRMEE": return "Confirmée";
            case "EN_COURS": return "En cours";
            case "TERMINEE": return "Terminée";
            case "ANNULEE": return "Annulée";
            case "REFUSEE": return "Refusée";
            default: return statut;
        }
    }
    
    public boolean isPending() {
        return "EN_ATTENTE".equals(statut);
    }
    
    public boolean isConfirmed() {
        return "CONFIRMEE".equals(statut);
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return isConfirmed() && dateDebut != null && dateFin != null &&
               now.isAfter(dateDebut) && now.isBefore(dateFin);
    }
    
    public boolean isUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        return isConfirmed() && dateDebut != null && dateDebut.isAfter(now);
    }
    
    @Override
    public String toString() {
        return materielNom + " x" + quantite + " - " + getFormattedDateDebut() + " → " + getFormattedDateFin();
    }
}