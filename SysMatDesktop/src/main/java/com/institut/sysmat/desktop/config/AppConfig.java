package com.institut.sysmat.desktop.config;

public class AppConfig {
    
    // Configuration API Backend
    public static final String API_BASE_URL = "http://localhost:8080/api";
    public static final int API_TIMEOUT = 30000; // 30 secondes
    
    // Chemins des vues FXML
    public static final String VIEW_LOGIN = "/com/institut/sysmat/desktop/fxml/login.fxml";
    public static final String VIEW_ADMIN_DASHBOARD = "/com/institut/sysmat/desktop/fxml/admin-dashboard.fxml";
    public static final String VIEW_PROF_DASHBOARD = "/com/institut/sysmat/desktop/fxml/prof-dashboard.fxml";
    public static final String VIEW_MATERIELS = "/com/institut/sysmat/desktop/fxml/materiels.fxml";
    public static final String VIEW_RESERVATIONS = "/com/institut/sysmat/desktop/fxml/reservations.fxml";
    public static final String VIEW_NEW_RESERVATION = "/com/institut/sysmat/desktop/fxml/new-reservation.fxml";
    public static final String VIEW_USERS = "/com/institut/sysmat/desktop/fxml/users.fxml";
    public static final String VIEW_REPORTS = "/com/institut/sysmat/desktop/fxml/reports.fxml";
    
    // Styles CSS
    public static final String CSS_STYLES = "/com/institut/sysmat/desktop/css/styles.css";
    
    // Messages
    public static final String MSG_LOGIN_SUCCESS = "Connexion réussie !";
    public static final String MSG_LOGIN_ERROR = "Email ou mot de passe incorrect";
    public static final String MSG_SESSION_EXPIRED = "Session expirée, veuillez vous reconnecter";
    public static final String MSG_NETWORK_ERROR = "Erreur de connexion au serveur";
    public static final String MSG_LOADING_ERROR = "Erreur lors du chargement des données";
    public static final String MSG_SAVE_SUCCESS = "Enregistrement réussi";
    public static final String MSG_DELETE_SUCCESS = "Suppression réussie";
    public static final String MSG_CONFIRM_DELETE = "Êtes-vous sûr de vouloir supprimer cet élément ?";
    
    // Constantes pour les rôles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PROFESSEUR = "PROFESSEUR";
}