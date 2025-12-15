package com.institut.sysmat.service;

import com.institut.sysmat.dto.request.CreateReservationRequest;
import com.institut.sysmat.dto.response.ReservationResponse;
import com.institut.sysmat.exception.ResourceNotFoundException;
import com.institut.sysmat.model.*;
import com.institut.sysmat.repository.MaterielRepository;
import com.institut.sysmat.repository.ReservationRepository;
import com.institut.sysmat.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private MaterielRepository materielRepository;
    
    @Autowired
    private AuthService authService;
    
    public Reservation createReservation(CreateReservationRequest request) {
        // Vérifier l'utilisateur courant
        Utilisateur utilisateur = authService.getCurrentUser();
        
        // Vérifier le matériel
        Materiel materiel = materielRepository.findById(request.getMaterielId())
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'id: " + request.getMaterielId()));
        
        // Vérifier la disponibilité
        if (materiel.getEtat() != EtatMateriel.DISPONIBLE) {
            throw new RuntimeException("Le matériel n'est pas disponible");
        }
        
        if (materiel.getQuantiteDisponible() < request.getQuantite()) {
            throw new RuntimeException("Quantité insuffisante. Disponible: " + materiel.getQuantiteDisponible());
        }
        
        // Vérifier les conflits de réservation
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
            request.getMaterielId(), request.getDateDebut(), request.getDateFin()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Conflit de réservation pour ce créneau horaire");
        }
        
        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setMateriel(materiel);
        reservation.setQuantite(request.getQuantite());
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setMotifUtilisation(request.getMotifUtilisation());
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        
        // Mettre à jour la quantité disponible
        materiel.setQuantiteDisponible(materiel.getQuantiteDisponible() - request.getQuantite());
        materielRepository.save(materiel);
        
        return reservationRepository.save(reservation);
    }
    
    public Reservation updateReservationStatus(Long reservationId, StatutReservation newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + reservationId));
        
        Utilisateur currentUser = authService.getCurrentUser();
        
        // Seuls les admins peuvent valider/refuser les réservations
        if (currentUser.getRole() != Role.ADMIN && 
            (newStatus == StatutReservation.CONFIRMEE || newStatus == StatutReservation.REFUSEE)) {
            throw new RuntimeException("Permission refusée");
        }
        
        // Les professeurs ne peuvent annuler que leurs propres réservations en attente
        if (currentUser.getRole() == Role.PROFESSEUR && newStatus == StatutReservation.ANNULEE) {
            if (!reservation.getUtilisateur().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Vous ne pouvez annuler que vos propres réservations");
            }
            if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
                throw new RuntimeException("Seules les réservations en attente peuvent être annulées");
            }
        }
        
        // Si la réservation est refusée ou annulée, remettre la quantité disponible
        if ((newStatus == StatutReservation.REFUSEE || newStatus == StatutReservation.ANNULEE) 
            && reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            Materiel materiel = reservation.getMateriel();
            materiel.setQuantiteDisponible(materiel.getQuantiteDisponible() + reservation.getQuantite());
            materielRepository.save(materiel);
        }
        
        // Si la réservation est confirmée, mettre à jour la date de validation
        if (newStatus == StatutReservation.CONFIRMEE) {
            reservation.setDateValidation(LocalDateTime.now());
        }
        
        reservation.setStatut(newStatus);
        
        return reservationRepository.save(reservation);
    }
    
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getUserReservations() {
        Utilisateur utilisateur = authService.getCurrentUser();
        return reservationRepository.findByUtilisateur(utilisateur);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getPendingReservations() {
        return reservationRepository.findByStatut(StatutReservation.EN_ATTENTE);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByStatus(StatutReservation status) {
        return reservationRepository.findByStatut(status);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByMateriel(Long materielId) {
        return reservationRepository.findByMaterielId(materielId);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsInPeriod(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findReservationsInPeriod(start, end);
    }
    
    @Transactional(readOnly = true)
    public long countPendingReservations() {
        return reservationRepository.countPendingReservations();
    }
    
    public ReservationResponse convertToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setUtilisateurId(reservation.getUtilisateur().getId());
        response.setUtilisateurNom(reservation.getUtilisateur().getNom());
        response.setUtilisateurPrenom(reservation.getUtilisateur().getPrenom());
        response.setMaterielId(reservation.getMateriel().getId());
        response.setMaterielNom(reservation.getMateriel().getNom());
        response.setQuantite(reservation.getQuantite());
        response.setDateDebut(reservation.getDateDebut());
        response.setDateFin(reservation.getDateFin());
        response.setStatut(reservation.getStatut());
        response.setMotifUtilisation(reservation.getMotifUtilisation());
        response.setDateReservation(reservation.getDateReservation());
        response.setDateValidation(reservation.getDateValidation());
        
        return response;
    }
    
    public List<ReservationResponse> convertToResponseList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}