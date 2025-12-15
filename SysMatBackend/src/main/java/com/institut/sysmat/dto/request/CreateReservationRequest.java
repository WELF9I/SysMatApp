package com.institut.sysmat.dto.request;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class CreateReservationRequest {
    
    @NotNull(message = "L'ID du matériel est obligatoire")
    private Long materielId;
    
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private int quantite;
    
    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDateTime dateFin;
    
    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String motifUtilisation;
    
    public CreateReservationRequest() {}
    
    // Getters and Setters
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