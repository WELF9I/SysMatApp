package com.institut.sysmat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "materiels")
public class Materiel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Size(max = 100)
    private String typeMateriel;
    
    @Min(0)
    private int quantiteTotale;
    
    @Min(0)
    private int quantiteDisponible;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EtatMateriel etat = EtatMateriel.DISPONIBLE;
    
    @Size(max = 100)
    private String localisation;
    
    private LocalDate dateAcquisition;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "materiel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Reservation> reservations = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (quantiteDisponible == 0 && quantiteTotale > 0) {
            quantiteDisponible = quantiteTotale;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (quantiteDisponible > quantiteTotale) {
            quantiteDisponible = quantiteTotale;
        }
        if (quantiteDisponible < 0) {
            quantiteDisponible = 0;
        }
    }
    
    public Materiel() {}
    
    public Materiel(String nom, String typeMateriel, int quantiteTotale) {
        this.nom = nom;
        this.typeMateriel = typeMateriel;
        this.quantiteTotale = quantiteTotale;
        this.quantiteDisponible = quantiteTotale;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTypeMateriel() { return typeMateriel; }
    public void setTypeMateriel(String typeMateriel) { this.typeMateriel = typeMateriel; }
    
    public int getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(int quantiteTotale) { 
        this.quantiteTotale = quantiteTotale;
        if (this.quantiteDisponible > quantiteTotale) {
            this.quantiteDisponible = quantiteTotale;
        }
    }
    
    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { 
        this.quantiteDisponible = quantiteDisponible;
        if (this.quantiteDisponible < 0) this.quantiteDisponible = 0;
        if (this.quantiteDisponible > this.quantiteTotale) {
            this.quantiteDisponible = this.quantiteTotale;
        }
    }
    
    public EtatMateriel getEtat() { return etat; }
    public void setEtat(EtatMateriel etat) { this.etat = etat; }
    
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<Reservation> getReservations() { return reservations; }
    public void setReservations(Set<Reservation> reservations) { this.reservations = reservations; }
}