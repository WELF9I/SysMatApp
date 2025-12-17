package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private Button dashboardButton;
    @FXML private Button materialsButton;
    @FXML private Button reservationsButton;
    @FXML private Button usersButton;
    @FXML private Button reportsButton;
    @FXML private Button newReservationButton;
    @FXML private Button logoutButton;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private boolean isProfessorMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check user role and setup UI accordingly
        if (sessionManager.getCurrentUser() != null) {
            String role = sessionManager.getCurrentUser().getRole();
            setProfessorMode(AppConfig.ROLE_PROFESSEUR.equals(role));
        }
    }

    public void setProfessorMode(boolean professorMode) {
        this.isProfessorMode = professorMode;
        if (professorMode) {
            if (usersButton != null) {
                usersButton.setVisible(false);
                usersButton.setManaged(false);
            }
            if (reportsButton != null) {
                reportsButton.setVisible(false);
                reportsButton.setManaged(false);
            }
            if (newReservationButton != null) {
                newReservationButton.setVisible(true);
                newReservationButton.setManaged(true);
            }
        } else {
            if (newReservationButton != null) {
                newReservationButton.setVisible(false);
                newReservationButton.setManaged(false);
            }
        }
    }

    @FXML
    private void navigateToDashboard() {
        try {
            String role = sessionManager.getCurrentUser().getRole();
            String fxmlPath;
            
            if (AppConfig.ROLE_ADMIN.equals(role)) {
                fxmlPath = AppConfig.VIEW_ADMIN_DASHBOARD;
            } else if (AppConfig.ROLE_PROFESSEUR.equals(role)) {
                fxmlPath = AppConfig.VIEW_PROF_DASHBOARD;
            } else {
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger le tableau de bord");
        }
    }

    @FXML
    private void navigateToMaterials() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_MATERIELS));
            Parent root = loader.load();
            
            // Set professor mode if needed
            if (isProfessorMode) {
                MaterielController controller = loader.getController();
                controller.setProfessorMode(true);
            }
            
            Stage stage = (Stage) materialsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des matériels");
        }
    }

    @FXML
    private void navigateToReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_RESERVATIONS));
            Parent root = loader.load();
            
            // Set professor mode if needed
            if (isProfessorMode) {
                ReservationController controller = loader.getController();
                controller.setProfessorMode(true);
            }
            
            Stage stage = (Stage) reservationsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des réservations");
        }
    }

    @FXML
    private void navigateToUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_USERS));
            Parent root = loader.load();
            
            Stage stage = (Stage) usersButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des utilisateurs");
        }
    }

    @FXML
    private void navigateToReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_REPORTS));
            Parent root = loader.load();
            
            Stage stage = (Stage) reportsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des rapports");
        }
    }

    @FXML
    private void navigateToNewReservation() {
        if (!isProfessorMode) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_NEW_RESERVATION));
            Parent root = loader.load();
            
            Stage stage = (Stage) newReservationButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger le formulaire de réservation");
        }
    }

    @FXML
    private void handleLogout() {
        if (DialogUtil.showConfirmation("Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?")) {
            sessionManager.clearSession();
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_LOGIN));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource(AppConfig.CSS_STYLES)).toExternalForm());
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setMaximized(false);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
