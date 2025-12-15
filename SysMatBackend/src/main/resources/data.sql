-- Ce fichier sera exécuté automatiquement au démarrage de l'application
-- Les mots de passe sont encodés avec BCrypt
-- admin123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iK6lP3WpJ2D.6NcWYQO3t5J2lBxW
-- prof123 -> $2a$10$h3Bq6V6S5D4L6W7N8vXcG.9bZ1aC2d3E4f5G6h7I8j9K0lM1n2O3p

-- Désactiver les contraintes de clé étrangère temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Vider les tables si nécessaire (pour les tests)
-- DELETE FROM reservations;
-- DELETE FROM materiels;
-- DELETE FROM utilisateurs;

-- Réactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;

-- Insérer les matériels de test
INSERT INTO materiels (nom, description, type_materiel, quantite_totale, quantite_disponible, etat, localisation, date_acquisition, created_at, updated_at) VALUES
('Double-fiche RJ45', 'Câbles réseau Cat6 pour travaux pratiques', 'Connectique', 50, 50, 'DISPONIBLE', 'Salle 101 - Rack réseau', '2023-01-15', NOW(), NOW()),
('Projecteur Epson EB-X41', 'Projecteur HD 1080p, 3500 lumens, connectivité HDMI/VGA', 'Audiovisuel', 5, 5, 'DISPONIBLE', 'Bureau Administration - Armoire AV', '2023-02-20', NOW(), NOW()),
('Laptop Dell Latitude 5420', 'Intel i5-1135G7, 8GB RAM, 256GB SSD, Windows 10 Pro', 'Informatique', 10, 10, 'DISPONIBLE', 'Salle Informatique 203', '2023-03-10', NOW(), NOW()),
('Switch Cisco Catalyst 2960', '24 ports Gigabit Ethernet, managé, rackable', 'Réseau', 3, 3, 'DISPONIBLE', 'Salle Serveurs - Rack principal', '2023-01-30', NOW(), NOW()),
('Tableau blanc interactif Smart Board', 'Écran tactile 75", logiciel inclus, stylets', 'Pédagogique', 2, 2, 'DISPONIBLE', 'Amphithéâtre A', '2023-04-05', NOW(), NOW()),
('Visualiseur HUE HD Pro', 'Caméra document 1080p, micro intégré, flexible', 'Audiovisuel', 4, 4, 'DISPONIBLE', 'Bureau Administration', '2023-02-28', NOW(), NOW()),
('Kit Arduino UNO Starter', 'Arduino UNO R3 + breadboard + 20 capteurs + câbles', 'Électronique', 15, 15, 'DISPONIBLE', 'Laboratoire Électronique - Boîte 3', '2023-03-25', NOW(), NOW()),
('Double-fiche électrique', 'Rallonge 3m, 4 prises, parafoudre', 'Électrique', 35, 35, 'DISPONIBLE', 'Salle 101 - Armoire électrique', '2023-01-10', NOW(), NOW()),
('Microphone sans fil UHF', 'Système micro UHF double émetteur, récepteur 2 canaux', 'Audiovisuel', 6, 6, 'DISPONIBLE', 'Bureau Administration - Armoire son', '2023-02-15', NOW(), NOW()),
('Clé USB 32GB sécurisée', 'USB 3.0, chiffrement matériel, compatible Windows/Mac', 'Informatique', 20, 20, 'DISPONIBLE', 'Bureau Administration - Tiroir sécurisé', '2023-03-30', NOW(), NOW()),
('Oscilloscope numérique', 'Oscilloscope 100MHz, 2 canaux, écran LCD 7"', 'Électronique', 4, 4, 'DISPONIBLE', 'Laboratoire Électronique', '2023-04-12', NOW(), NOW()),
('Routeur Wi-Fi Cisco', 'Routeur dual-band AC1750, 4 ports LAN', 'Réseau', 5, 5, 'DISPONIBLE', 'Salle Serveurs', '2023-03-18', NOW(), NOW()),
('Imprimante 3D Creality', 'Imprimante 3D FDM, plateau 220x220mm, filament PLA inclus', 'Fabrication', 2, 2, 'DISPONIBLE', 'Atelier Fabrication', '2023-05-01', NOW(), NOW());

SELECT 'Données initiales insérées avec succès!' AS message;