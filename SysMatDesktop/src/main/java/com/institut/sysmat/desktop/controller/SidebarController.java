package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SidebarController {

    @FXML private Button dashboardButton;
    @FXML private Button materialsButton;
    @FXML private Button reservationsButton;
    @FXML private Button usersButton;
    @FXML private Button reportsButton;
    @FXML private Button logoutButton;

    private final SessionManager sessionManager = SessionManager.getInstance();

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
        navigate(AppConfig.VIEW_MATERIELS, materialsButton);
    }

    @FXML
    private void navigateToReservations() {
        navigate(AppConfig.VIEW_RESERVATIONS, reservationsButton);
    }

    @FXML
    private void navigateToUsers() {
        navigate(AppConfig.VIEW_USERS, usersButton);
    }

    @FXML
    private void navigateToReports() {
        navigate(AppConfig.VIEW_REPORTS, reportsButton);
    }

    @FXML
    private void handleLogout() {
        if (DialogUtil.showConfirmation("Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?")) {
            sessionManager.clearSession();
            navigateToLogin();
        }
    }

    private void navigate(String fxmlPath, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page");
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
