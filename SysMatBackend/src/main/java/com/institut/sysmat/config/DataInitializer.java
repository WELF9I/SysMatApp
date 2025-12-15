package com.institut.sysmat.config;

import com.institut.sysmat.model.Role;
import com.institut.sysmat.model.Utilisateur;
import com.institut.sysmat.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Vérifier si l'admin existe déjà
        if (utilisateurRepository.findByEmail("admin@institut.edu").isEmpty()) {
            Utilisateur admin = new Utilisateur(
                "Admin",
                "System",
                "admin@institut.edu",
                passwordEncoder.encode("admin123"),
                Role.ADMIN
            );
            admin.setDepartement("Administration");
            utilisateurRepository.save(admin);
            System.out.println("Admin utilisateur créé: admin@institut.edu / admin123");
        }
        
        if (utilisateurRepository.findByEmail("s.martin@institut.edu").isEmpty()) {
            Utilisateur admin2 = new Utilisateur(
                "Martin",
                "Sophie",
                "s.martin@institut.edu",
                passwordEncoder.encode("admin123"),
                Role.ADMIN
            );
            admin2.setDepartement("Administration");
            utilisateurRepository.save(admin2);
            System.out.println("Admin utilisateur créé: s.martin@institut.edu / admin123");
        }
        
        if (utilisateurRepository.findByEmail("p.dupont@institut.edu").isEmpty()) {
            Utilisateur prof1 = new Utilisateur(
                "Dupont",
                "Pierre",
                "p.dupont@institut.edu",
                passwordEncoder.encode("prof123"),
                Role.PROFESSEUR
            );
            prof1.setDepartement("Informatique");
            utilisateurRepository.save(prof1);
            System.out.println("Professeur utilisateur créé: p.dupont@institut.edu / prof123");
        }
        
        if (utilisateurRepository.findByEmail("m.bernard@institut.edu").isEmpty()) {
            Utilisateur prof2 = new Utilisateur(
                "Bernard",
                "Marie",
                "m.bernard@institut.edu",
                passwordEncoder.encode("prof123"),
                Role.PROFESSEUR
            );
            prof2.setDepartement("Réseaux");
            utilisateurRepository.save(prof2);
            System.out.println("Professeur utilisateur créé: m.bernard@institut.edu / prof123");
        }
        
        if (utilisateurRepository.findByEmail("j.dubois@institut.edu").isEmpty()) {
            Utilisateur prof3 = new Utilisateur(
                "Dubois",
                "Jean",
                "j.dubois@institut.edu",
                passwordEncoder.encode("prof123"),
                Role.PROFESSEUR
            );
            prof3.setDepartement("Systèmes");
            utilisateurRepository.save(prof3);
            System.out.println("Professeur utilisateur créé: j.dubois@institut.edu / prof123");
        }
        
        System.out.println("=== Initialisation des données terminée ===");
    }
}