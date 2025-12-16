package com.institut.sysmat.desktop.model;

public class Materiel {
    private Long id;
    private String nom;
    private String description;
    private String typeMateriel;
    private int quantiteTotale;
    private int quantiteDisponible;
    private String etat;
    private String localisation;
    
    public Materiel() {}
    
    public Materiel(String nom, String typeMateriel, int quantiteTotale) {
        this.nom = nom;
        this.typeMateriel = typeMateriel;
        this.quantiteTotale = quantiteTotale;
        this.quantiteDisponible = quantiteTotale;
        this.etat = "DISPONIBLE";
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
    
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    
    public boolean isAvailable() {
        return "DISPONIBLE".equals(etat) && quantiteDisponible > 0;
    }
    
    public double getAvailabilityPercentage() {
        if (quantiteTotale == 0) return 0;
        return (quantiteDisponible * 100.0) / quantiteTotale;
    }
    
    public String getAvailabilityStatus() {
        if (!"DISPONIBLE".equals(etat)) return etat;
        if (quantiteDisponible == 0) return "RUPTURE DE STOCK";
        if (quantiteDisponible < quantiteTotale * 0.2) return "STOCK FAIBLE";
        return "DISPONIBLE";
    }
    
    @Override
    public String toString() {
        return nom + " (" + typeMateriel + ") - Disponible: " + quantiteDisponible + "/" + quantiteTotale;
    }
}