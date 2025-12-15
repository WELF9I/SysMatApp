package com.institut.sysmat.service;

import com.institut.sysmat.dto.request.CreateMaterielRequest;
import com.institut.sysmat.dto.request.UpdateMaterielRequest;
import com.institut.sysmat.dto.response.MaterielResponse;
import com.institut.sysmat.exception.ResourceNotFoundException;
import com.institut.sysmat.model.EtatMateriel;
import com.institut.sysmat.model.Materiel;
import com.institut.sysmat.repository.MaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterielService {
    
    @Autowired
    private MaterielRepository materielRepository;
    
    public Materiel createMateriel(CreateMaterielRequest request) {
        Materiel materiel = new Materiel();
        materiel.setNom(request.getNom());
        materiel.setDescription(request.getDescription());
        materiel.setTypeMateriel(request.getTypeMateriel());
        materiel.setQuantiteTotale(request.getQuantiteTotale());
        materiel.setQuantiteDisponible(request.getQuantiteTotale());
        materiel.setLocalisation(request.getLocalisation());
        materiel.setDateAcquisition(request.getDateAcquisition());
        materiel.setEtat(EtatMateriel.DISPONIBLE);
        
        return materielRepository.save(materiel);
    }
    
    public Materiel updateMateriel(Long id, UpdateMaterielRequest request) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'id: " + id));
        
        if (request.getNom() != null) {
            materiel.setNom(request.getNom());
        }
        if (request.getDescription() != null) {
            materiel.setDescription(request.getDescription());
        }
        if (request.getTypeMateriel() != null) {
            materiel.setTypeMateriel(request.getTypeMateriel());
        }
        if (request.getQuantiteTotale() != null) {
            materiel.setQuantiteTotale(request.getQuantiteTotale());
        }
        if (request.getQuantiteDisponible() != null) {
            materiel.setQuantiteDisponible(request.getQuantiteDisponible());
        }
        if (request.getEtat() != null) {
            materiel.setEtat(request.getEtat());
        }
        if (request.getLocalisation() != null) {
            materiel.setLocalisation(request.getLocalisation());
        }
        if (request.getDateAcquisition() != null) {
            materiel.setDateAcquisition(request.getDateAcquisition());
        }
        
        return materielRepository.save(materiel);
    }
    
    @Transactional(readOnly = true)
    public Materiel getMaterielById(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Materiel> getAllMateriels() {
        return materielRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Materiel> getAvailableMateriels() {
        return materielRepository.findAvailableMateriels();
    }
    
    @Transactional(readOnly = true)
    public List<Materiel> searchMateriels(String keyword) {
        return materielRepository.searchByKeyword(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Materiel> getMaterielsByType(String type) {
        return materielRepository.findByTypeMateriel(type);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllTypes() {
        return materielRepository.findAllTypes();
    }
    
    public void deleteMateriel(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel non trouvé avec l'id: " + id));
        
        materielRepository.delete(materiel);
    }
    
    public MaterielResponse convertToResponse(Materiel materiel) {
        MaterielResponse response = new MaterielResponse();
        response.setId(materiel.getId());
        response.setNom(materiel.getNom());
        response.setDescription(materiel.getDescription());
        response.setTypeMateriel(materiel.getTypeMateriel());
        response.setQuantiteTotale(materiel.getQuantiteTotale());
        response.setQuantiteDisponible(materiel.getQuantiteDisponible());
        response.setEtat(materiel.getEtat());
        response.setLocalisation(materiel.getLocalisation());
        response.setDateAcquisition(materiel.getDateAcquisition());
        response.setCreatedAt(materiel.getCreatedAt());
        response.setUpdatedAt(materiel.getUpdatedAt());
        
        return response;
    }
    
    public List<MaterielResponse> convertToResponseList(List<Materiel> materiels) {
        return materiels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}