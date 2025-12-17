package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.model.Materiel;
import com.institut.sysmat.desktop.service.MaterielService;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MaterialDialogController {

    public enum Mode { ADD, EDIT }

    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField quantiteField;
    @FXML private ComboBox<String> etatCombo;
    @FXML private TextField localisationField;
    @FXML private TextArea descriptionArea;

    private Mode mode;
    private Materiel material;
    private Runnable onSaveCallback;
    private final MaterielService materielService = new MaterielService();

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("Connectique", "Audiovisuel", "Informatique", "Réseau", "Pédagogique", "Électronique", "Électrique");
        etatCombo.getItems().addAll("DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE");
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setMaterial(Materiel material) {
        this.material = material;
        if (material != null) {
            nomField.setText(material.getNom());
            typeCombo.setValue(material.getTypeMateriel());
            quantiteField.setText(String.valueOf(material.getQuantiteTotale()));
            etatCombo.setValue(material.getEtat());
            localisationField.setText(material.getLocalisation());
            descriptionArea.setText(material.getDescription());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML private Button saveButton;

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        if (mode == Mode.ADD) {
            material = new Materiel();
        }

        material.setNom(nomField.getText());
        material.setTypeMateriel(typeCombo.getValue());
        try {
            material.setQuantiteTotale(Integer.parseInt(quantiteField.getText()));
        } catch (NumberFormatException e) {
            DialogUtil.showError("Erreur", "La quantité doit être un nombre entier valide");
            return;
        }
        material.setEtat(etatCombo.getValue());
        material.setLocalisation(localisationField.getText());
        material.setDescription(descriptionArea.getText());

        if (saveButton != null) saveButton.setDisable(true);
        
        Service<Materiel> service;
        if (mode == Mode.ADD) {
            service = materielService.createMateriel(material);
        } else {
            service = materielService.updateMateriel(material);
        }
        
        service.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                if (saveButton != null) saveButton.setDisable(false);
                DialogUtil.showSuccess("Succès", "Matériel enregistré avec succès");
                closeDialog();
                if (onSaveCallback != null) onSaveCallback.run();
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                if (saveButton != null) saveButton.setDisable(false);
                Throwable exception = event.getSource().getException();
                String msg = exception != null ? exception.getMessage() : "Erreur inconnue";
                DialogUtil.showError("Erreur", "Impossible d'enregistrer le matériel: " + msg);
            });
        });
        
        service.start();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (nomField.getText().isEmpty() || typeCombo.getValue() == null || quantiteField.getText().isEmpty()) {
            DialogUtil.showError("Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            DialogUtil.showError("Erreur", "La quantité doit être un nombre entier");
            return false;
        }
        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
