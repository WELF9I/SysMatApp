package com.institut.sysmat.controller;

import com.institut.sysmat.dto.request.CreateMaterielRequest;
import com.institut.sysmat.dto.request.UpdateMaterielRequest;
import com.institut.sysmat.dto.response.ApiResponse;
import com.institut.sysmat.dto.response.MaterielResponse;
import com.institut.sysmat.model.Materiel;
import com.institut.sysmat.service.MaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/materiels")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaterielController {
    
    @Autowired
    private MaterielService materielService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMateriel(@Valid @RequestBody CreateMaterielRequest request) {
        try {
            Materiel materiel = materielService.createMateriel(request);
            MaterielResponse response = materielService.convertToResponse(materiel);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Matériel créé avec succès", response));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getAllMateriels() {
        try {
            List<Materiel> materiels = materielService.getAllMateriels();
            List<MaterielResponse> responses = materielService.convertToResponseList(materiels);
            return ResponseEntity.ok(ApiResponse.success("Matériels récupérés avec succès", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération des matériels"));
        }
    }
    
    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getAvailableMateriels() {
        try {
            List<Materiel> materiels = materielService.getAvailableMateriels();
            List<MaterielResponse> responses = materielService.convertToResponseList(materiels);
            return ResponseEntity.ok(ApiResponse.success("Matériels disponibles récupérés avec succès", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération des matériels disponibles"));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getMaterielById(@PathVariable Long id) {
        try {
            Materiel materiel = materielService.getMaterielById(id);
            MaterielResponse response = materielService.convertToResponse(materiel);
            return ResponseEntity.ok(ApiResponse.success("Matériel récupéré avec succès", response));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMateriel(@PathVariable Long id, 
                                           @Valid @RequestBody UpdateMaterielRequest request) {
        try {
            Materiel materiel = materielService.updateMateriel(id, request);
            MaterielResponse response = materielService.convertToResponse(materiel);
            return ResponseEntity.ok(ApiResponse.success("Matériel mis à jour avec succès", response));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMateriel(@PathVariable Long id) {
        try {
            materielService.deleteMateriel(id);
            return ResponseEntity.ok(ApiResponse.success("Matériel supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> searchMateriels(@RequestParam String keyword) {
        try {
            List<Materiel> materiels = materielService.searchMateriels(keyword);
            List<MaterielResponse> responses = materielService.convertToResponseList(materiels);
            return ResponseEntity.ok(ApiResponse.success("Résultats de recherche", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la recherche"));
        }
    }
    
    @GetMapping("/types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getAllTypes() {
        try {
            List<String> types = materielService.getAllTypes();
            return ResponseEntity.ok(ApiResponse.success("Types de matériels récupérés", types));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération des types"));
        }
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<?> getMaterielsByType(@PathVariable String type) {
        try {
            List<Materiel> materiels = materielService.getMaterielsByType(type);
            List<MaterielResponse> responses = materielService.convertToResponseList(materiels);
            return ResponseEntity.ok(ApiResponse.success("Matériels par type", responses));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la récupération"));
        }
    }
}