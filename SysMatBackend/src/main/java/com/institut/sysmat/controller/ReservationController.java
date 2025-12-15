package com.institut.sysmat.controller;

import com.institut.sysmat.dto.request.CreateReservationRequest;
import com.institut.sysmat.dto.response.ApiResponse;
import com.institut.sysmat.dto.response.ReservationResponse;
import com.institut.sysmat.model.Reservation;
import com.institut.sysmat.model.StatutReservation;
import com.institut.sysmat.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(request);
            ReservationResponse response = reservationService.convertToResponse(reservation);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Réservation créée avec succès", response));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public ResponseEntity<?> getMyReservations() {
        try {
            List<Reservation> reservations = reservationService.getUserReservations();
            List<ReservationResponse> responses = reservationService.convertToResponseList(reservations);
            return ResponseEntity.ok(ApiResponse.success("Mes réservations", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération des réservations"));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            List<ReservationResponse> responses = reservationService.convertToResponseList(reservations);
            return ResponseEntity.ok(ApiResponse.success("Toutes les réservations", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération des réservations"));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingReservations() {
        try {
            List<Reservation> reservations = reservationService.getPendingReservations();
            List<ReservationResponse> responses = reservationService.convertToResponseList(reservations);
            return ResponseEntity.ok(ApiResponse.success("Réservations en attente", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.getReservationById(id);
            ReservationResponse response = reservationService.convertToResponse(reservation);
            return ResponseEntity.ok(ApiResponse.success("Réservation récupérée", response));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable Long id, 
                                                    @RequestParam StatutReservation status) {
        try {
            Reservation reservation = reservationService.updateReservationStatus(id, status);
            ReservationResponse response = reservationService.convertToResponse(reservation);
            return ResponseEntity.ok(ApiResponse.success("Statut mis à jour", response));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservationsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<Reservation> reservations = reservationService.getReservationsInPeriod(start, end);
            List<ReservationResponse> responses = reservationService.convertToResponseList(reservations);
            return ResponseEntity.ok(ApiResponse.success("Réservations par période", responses));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/materiel/{materielId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservationsByMateriel(@PathVariable Long materielId) {
        try {
            List<Reservation> reservations = reservationService.getReservationsByMateriel(materielId);
            List<ReservationResponse> responses = reservationService.convertToResponseList(reservations);
            return ResponseEntity.ok(ApiResponse.success("Réservations par matériel", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
    
    @GetMapping("/stats/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingReservationsCount() {
        try {
            long count = reservationService.countPendingReservations();
            return ResponseEntity.ok(ApiResponse.success("Nombre de réservations en attente", count));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors du calcul"));
        }
    }
}