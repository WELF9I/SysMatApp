package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.service.AuthService;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AuthController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label loadingLabel;
    
    private final AuthService authService = new AuthService();
    
    @FXML
    public void initialize() {
        // Raccourci clavier : Enter pour se connecter
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
        
        // Données de test pour faciliter le développement
        emailField.setText("admin@institut.edu");
        passwordField.setText("admin123");
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validation des champs
        if (email.isEmpty() || password.isEmpty()) {
            DialogUtil.showError("Erreur de validation", "Veuillez remplir tous les champs");
            return;
        }
        
        if (!email.contains("@")) {
            DialogUtil.showError("Erreur de validation", "Format d'email invalide");
            return;
        }
        
        // Afficher l'indicateur de chargement
        showLoading(true);
        loginButton.setDisable(true);
        
        // Appeler le service d'authentification
        var loginService = authService.login(email, password);
        
        loginService.setOnSucceeded(event -> {
            AuthService.AuthResult result = loginService.getValue();
            
            if (result.isSuccess()) {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Connexion réussie", "Bienvenue " + result.getUser().getFullName());
                    navigateToDashboard(result.getUser().getRole());
                });
            } else {
                Platform.runLater(() -> {
                    showLoading(false);
                    loginButton.setDisable(false);
                    DialogUtil.showError("Échec de connexion", result.getMessage());
                });
            }
        });
        
        loginService.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                loginButton.setDisable(false);
                DialogUtil.showError("Erreur", "Erreur lors de la connexion au serveur");
            });
        });
        
        loginService.start();
    }
    
    @FXML
    private void handleForgotPassword() {
        DialogUtil.showInfo("Mot de passe oublié", 
            "Veuillez contacter l'administrateur pour réinitialiser votre mot de passe.");
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setIconified(true);
    }
    
    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        loadingPane.setManaged(show);
        loadingIndicator.setVisible(show);
        loadingLabel.setVisible(show);
    }
    
    private void navigateToDashboard(String role) {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            String fxmlPath;
            
            if (AppConfig.ROLE_ADMIN.equals(role)) {
                fxmlPath = AppConfig.VIEW_ADMIN_DASHBOARD;
            } else if (AppConfig.ROLE_PROFESSEUR.equals(role)) {
                fxmlPath = AppConfig.VIEW_PROF_DASHBOARD;
            } else {
                throw new IllegalArgumentException("Rôle inconnu: " + role);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource(AppConfig.CSS_STYLES)).toExternalForm());
            
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
            currentStage.setMaximized(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger le tableau de bord");
        }
    }
}