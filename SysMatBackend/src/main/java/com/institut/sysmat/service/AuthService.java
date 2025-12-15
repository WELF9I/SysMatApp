package com.institut.sysmat.service;

import com.institut.sysmat.dto.request.AuthRequest;
import com.institut.sysmat.dto.response.AuthResponse;
import com.institut.sysmat.model.Role;
import com.institut.sysmat.model.Utilisateur;
import com.institut.sysmat.repository.UtilisateurRepository;
import com.institut.sysmat.security.JwtUtil;
import com.institut.sysmat.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponse authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return new AuthResponse(
            jwt,
            userDetails.getRole(),
            userDetails.getNom(),
            userDetails.getPrenom(),
            userDetails.getUsername()
        );
    }
    
    public Utilisateur registerUser(AuthRequest authRequest, Role role) {
        if (utilisateurRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }
        
        Utilisateur utilisateur = new Utilisateur(
            authRequest.getNom(),
            authRequest.getPrenom(),
            authRequest.getEmail(),
            passwordEncoder.encode(authRequest.getMotDePasse()),
            role
        );
        
        utilisateur.setDepartement(authRequest.getDepartement());
        
        return utilisateurRepository.save(utilisateur);
    }
    
    public Utilisateur getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return utilisateurRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}