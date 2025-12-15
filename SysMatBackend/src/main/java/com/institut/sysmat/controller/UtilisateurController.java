package com.institut.sysmat.controller;

import com.institut.sysmat.dto.request.AuthRequest;
import com.institut.sysmat.dto.response.ApiResponse;
import com.institut.sysmat.model.Role;
import com.institut.sysmat.model.Utilisateur;
import com.institut.sysmat.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/utilisateurs")

public class UtilisateurController {
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Utilisateurs récupérés", utilisateurs));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@PathVariable Role role) {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getUsersByRole(role);
            return ResponseEntity.ok(ApiResponse.success("Utilisateurs par rôle", utilisateurs));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getActiveUsers() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getActiveUsers();
            return ResponseEntity.ok(ApiResponse.success("Utilisateurs actifs", utilisateurs));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur récupéré", utilisateur));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody AuthRequest request, 
                                       @RequestParam Role role) {
        try {
            Utilisateur utilisateur = utilisateurService.createUser(request, role);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Utilisateur créé avec succès", utilisateur));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, 
                                       @Valid @RequestBody AuthRequest request) {
        try {
            Utilisateur utilisateur = utilisateurService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour", utilisateur));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.toggleUserStatus(id);
            return ResponseEntity.ok(ApiResponse.success("Statut utilisateur modifié", utilisateur));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            utilisateurService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.searchUsers(keyword);
            return ResponseEntity.ok(ApiResponse.success("Résultats de recherche", utilisateurs));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la recherche"));
        }
    }
}