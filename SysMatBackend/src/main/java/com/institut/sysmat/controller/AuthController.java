package com.institut.sysmat.controller;

import com.institut.sysmat.dto.request.AuthRequest;
import com.institut.sysmat.dto.request.LoginRequest;
import com.institut.sysmat.dto.response.ApiResponse;
import com.institut.sysmat.dto.response.AuthResponse;
import com.institut.sysmat.model.Role;
import com.institut.sysmat.model.Utilisateur;
import com.institut.sysmat.service.AuthService;
import com.institut.sysmat.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")

public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(ApiResponse.success("Connexion réussie", response));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Email ou mot de passe incorrect"));
        }
    }
    
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Utilisateur utilisateur = authService.registerUser(authRequest, Role.ADMIN);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Administrateur créé avec succès", utilisateur));
        } catch (RuntimeException e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/register/professeur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerProfesseur(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Utilisateur utilisateur = authService.registerUser(authRequest, Role.PROFESSEUR);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Professeur créé avec succès", utilisateur));
        } catch (RuntimeException e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Utilisateur utilisateur = authService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success("Profil récupéré avec succès", utilisateur));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération du profil"));
        }
    }
}