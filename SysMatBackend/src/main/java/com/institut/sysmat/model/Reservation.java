package com.institut.sysmat.model;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations",
       indexes = {
           @Index(name = "idx_reservation_date_debut", columnList = "date_debut"),
           @Index(name = "idx_reservation_date_fin", columnList = "date_fin"),
           @Index(name = "idx_reservation_statut", columnList = "statut")
       })
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;
    
    @Min(1)
    private int quantite;
    
    @NotNull
    @Future
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;
    
    @NotNull
    @Future
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;
    
    @Size(max = 500)
    private String motifUtilisation;
    
    @Column(name = "date_reservation")
    private LocalDateTime dateReservation;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @PrePersist
    protected void onCreate() {
        dateReservation = LocalDateTime.now();
    }
    
    public Reservation() {}
    
    public Reservation(Utilisateur utilisateur, Materiel materiel, int quantite,
                      LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.utilisateur = utilisateur;
        this.materiel = materiel;
        this.quantite = quantite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    
    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }
    
    public String getMotifUtilisation() { return motifUtilisation; }
    public void setMotifUtilisation(String motifUtilisation) { this.motifUtilisation = motifUtilisation; }
    
    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
}