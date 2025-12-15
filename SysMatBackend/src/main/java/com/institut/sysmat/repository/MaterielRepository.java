package com.institut.sysmat.repository;

import com.institut.sysmat.model.EtatMateriel;
import com.institut.sysmat.model.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByNomContainingIgnoreCase(String nom);
    List<Materiel> findByTypeMateriel(String typeMateriel);
    List<Materiel> findByQuantiteDisponibleGreaterThan(int quantite);
    List<Materiel> findByEtat(EtatMateriel etat);
    List<Materiel> findByQuantiteDisponibleLessThan(int quantite);
    List<Materiel> findByLocalisation(String localisation);
    
    @Query("SELECT m FROM Materiel m WHERE m.quantiteDisponible > 0 AND m.etat = 'DISPONIBLE'")
    List<Materiel> findAvailableMateriels();
    
    @Query("SELECT DISTINCT m.typeMateriel FROM Materiel m")
    List<String> findAllTypes();
    
    @Query("SELECT m FROM Materiel m WHERE " +
           "LOWER(m.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(m.typeMateriel) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Materiel> searchByKeyword(String searchTerm);
}