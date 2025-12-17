package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.model.Utilisateur;
import com.institut.sysmat.desktop.service.UsersService;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserDialogController {

    public enum Mode { ADD, EDIT }

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField departementField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;

    private Mode mode;
    private Utilisateur user;
    private Runnable onSaveCallback;
    private final UsersService usersService = new UsersService();

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("ADMIN", "PROFESSEUR");
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.EDIT) {
            passwordField.setPromptText("Laisser vide pour ne pas changer");
        }
    }

    public void setUser(Utilisateur user) {
        this.user = user;
        if (user != null) {
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            roleCombo.setValue(user.getRole());
            departementField.setText(user.getDepartement());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        if (mode == Mode.ADD) {
            user = new Utilisateur();
        }

        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleCombo.getValue());
        user.setDepartement(departementField.getText());

        String password = passwordField.getText();
        
        if (saveButton != null) saveButton.setDisable(true);
        
        Service<Utilisateur> service;
        if (mode == Mode.ADD) {
            service = usersService.createUser(user, password);
        } else {
            service = usersService.updateUser(user, password);
        }
        
        service.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                if (saveButton != null) saveButton.setDisable(false);
                DialogUtil.showSuccess("Succès", "Utilisateur enregistré avec succès");
                closeDialog();
                if (onSaveCallback != null) onSaveCallback.run();
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                if (saveButton != null) saveButton.setDisable(false);
                Throwable exception = event.getSource().getException();
                String msg = exception != null ? exception.getMessage() : "Erreur inconnue";
                DialogUtil.showError("Erreur", "Impossible d'enregistrer l'utilisateur: " + msg);
            });
        });
        
        service.start();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || 
            emailField.getText().isEmpty() || roleCombo.getValue() == null) {
            DialogUtil.showError("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        if (mode == Mode.ADD && passwordField.getText().isEmpty()) {
             DialogUtil.showError("Erreur", "Le mot de passe est obligatoire pour un nouvel utilisateur");
             return false;
        }
        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
