package com.institut.sysmat.service;

import com.institut.sysmat.dto.request.AuthRequest;
import com.institut.sysmat.exception.ResourceNotFoundException;
import com.institut.sysmat.model.Role;
import com.institut.sysmat.model.Utilisateur;
import com.institut.sysmat.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UtilisateurService {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Utilisateur getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Utilisateur> getUsersByRole(Role role) {
        return utilisateurRepository.findByRole(role);
    }
    
    @Transactional(readOnly = true)
    public List<Utilisateur> getActiveUsers() {
        return utilisateurRepository.findByActifTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Utilisateur> searchUsers(String keyword) {
        return utilisateurRepository.searchByKeyword(keyword);
    }
    
    public Utilisateur createUser(AuthRequest request, Role role) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("L'email est déjà utilisé");
        }
        
        Utilisateur utilisateur = new Utilisateur(
            request.getNom(),
            request.getPrenom(),
            request.getEmail(),
            passwordEncoder.encode(request.getMotDePasse()),
            role
        );
        
        utilisateur.setDepartement(request.getDepartement());
        
        return utilisateurRepository.save(utilisateur);
    }
    
    public Utilisateur updateUser(Long id, AuthRequest request) {
        Utilisateur utilisateur = getUserById(id);
        
        if (request.getNom() != null) {
            utilisateur.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            utilisateur.setPrenom(request.getPrenom());
        }
        if (request.getEmail() != null && !request.getEmail().equals(utilisateur.getEmail())) {
            if (utilisateurRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("L'email est déjà utilisé");
            }
            utilisateur.setEmail(request.getEmail());
        }
        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }
        if (request.getDepartement() != null) {
            utilisateur.setDepartement(request.getDepartement());
        }
        
        return utilisateurRepository.save(utilisateur);
    }
    
    public Utilisateur toggleUserStatus(Long id) {
        Utilisateur utilisateur = getUserById(id);
        utilisateur.setActif(!utilisateur.isActif());
        return utilisateurRepository.save(utilisateur);
    }
    
    public void deleteUser(Long id) {
        Utilisateur utilisateur = getUserById(id);
        utilisateurRepository.delete(utilisateur);
    }
}