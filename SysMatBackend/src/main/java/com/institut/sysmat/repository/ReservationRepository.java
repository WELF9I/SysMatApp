package com.institut.sysmat.repository;

import com.institut.sysmat.model.Reservation;
import com.institut.sysmat.model.StatutReservation;
import com.institut.sysmat.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateur(Utilisateur utilisateur);
    List<Reservation> findByStatut(StatutReservation statut);
    List<Reservation> findByMaterielId(Long materielId);
    
    @Query("SELECT r FROM Reservation r WHERE r.materiel.id = :materielId " +
           "AND r.statut NOT IN ('ANNULEE', 'TERMINEE', 'REFUSEE') " +
           "AND ((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))")
    List<Reservation> findConflictingReservations(@Param("materielId") Long materielId,
                                                 @Param("dateDebut") LocalDateTime dateDebut,
                                                 @Param("dateFin") LocalDateTime dateFin);
    
    List<Reservation> findByDateDebutBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur.id = :userId " +
           "AND r.statut = 'EN_ATTENTE' " +
           "AND r.dateDebut > CURRENT_TIMESTAMP")
    List<Reservation> findPendingReservationsByUser(Long userId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = 'EN_ATTENTE'")
    long countPendingReservations();
    
    @Query("SELECT r FROM Reservation r WHERE " +
           "r.dateDebut >= :startDate AND r.dateFin <= :endDate " +
           "ORDER BY r.dateDebut")
    List<Reservation> findReservationsInPeriod(LocalDateTime startDate, LocalDateTime endDate);
}