package com.institut.sysmat.dto.request;

import com.institut.sysmat.model.EtatMateriel;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class UpdateMaterielRequest {
    
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String nom;
    
    private String description;
    
    @Size(max = 100, message = "Le type ne doit pas dépasser 100 caractères")
    private String typeMateriel;
    
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantiteTotale;
    
    @Min(value = 0, message = "La quantité disponible ne peut pas être négative")
    private Integer quantiteDisponible;
    
    private EtatMateriel etat;
    
    @Size(max = 100, message = "La localisation ne doit pas dépasser 100 caractères")
    private String localisation;
    
    private LocalDate dateAcquisition;
    
    public UpdateMaterielRequest() {}
    
    // Getters and Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTypeMateriel() { return typeMateriel; }
    public void setTypeMateriel(String typeMateriel) { this.typeMateriel = typeMateriel; }
    
    public Integer getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(Integer quantiteTotale) { this.quantiteTotale = quantiteTotale; }
    
    public Integer getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(Integer quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }
    
    public EtatMateriel getEtat() { return etat; }
    public void setEtat(EtatMateriel etat) { this.etat = etat; }
    
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
}