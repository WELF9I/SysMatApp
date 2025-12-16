package com.institut.sysmat.desktop.service;

import com.institut.sysmat.desktop.model.Utilisateur;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsersService {

    public Service<List<Utilisateur>> getAllUsers() {
        return new Service<>() {
            @Override
            protected Task<List<Utilisateur>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<Utilisateur> call() throws Exception {
                        // Simulate API call
                        Thread.sleep(500);
                        
                        List<Utilisateur> users = new ArrayList<>();
                        
                        // Add some dummy users
                        Utilisateur admin = new Utilisateur();
                        admin.setId(1L);
                        admin.setNom("Admin");
                        admin.setPrenom("System");
                        admin.setEmail("admin@sysmat.com");
                        admin.setRole("ADMIN");
                        admin.setActif(true);
                        admin.setDateCreation(LocalDateTime.now().minusMonths(6));
                        users.add(admin);
                        
                        Utilisateur prof = new Utilisateur();
                        prof.setId(2L);
                        prof.setNom("Dupont");
                        prof.setPrenom("Jean");
                        prof.setEmail("jean.dupont@sysmat.com");
                        prof.setRole("PROFESSEUR");
                        prof.setDepartement("Informatique");
                        prof.setActif(true);
                        prof.setDateCreation(LocalDateTime.now().minusMonths(3));
                        users.add(prof);
                        
                        Utilisateur prof2 = new Utilisateur();
                        prof2.setId(3L);
                        prof2.setNom("Martin");
                        prof2.setPrenom("Sophie");
                        prof2.setEmail("sophie.martin@sysmat.com");
                        prof2.setRole("PROFESSEUR");
                        prof2.setDepartement("Mathématiques");
                        prof2.setActif(false);
                        prof2.setDateCreation(LocalDateTime.now().minusMonths(1));
                        users.add(prof2);
                        
                        return users;
                    }
                };
            }
        };
    }

    public Service<Void> toggleUserStatus(Long userId) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        // Simulate API call
                        Thread.sleep(500);
                        return null;
                    }
                };
            }
        };
    }

    public Service<Void> deleteUser(Long userId) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        // Simulate API call
                        Thread.sleep(500);
                        return null;
                    }
                };
            }
        };
    }
}
