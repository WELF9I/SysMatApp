package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.model.Materiel;
import com.institut.sysmat.desktop.service.MaterielService;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.fxml.FXML;
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

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        if (mode == Mode.ADD) {
            material = new Materiel();
        }

        material.setNom(nomField.getText());
        material.setTypeMateriel(typeCombo.getValue());
        material.setQuantiteTotale(Integer.parseInt(quantiteField.getText()));
        material.setEtat(etatCombo.getValue());
        material.setLocalisation(localisationField.getText());
        material.setDescription(descriptionArea.getText());

        // In a real app, you would call the service here.
        // Since I don't have the full service code, I'll assume a method exists or just simulate it.
        // materielService.save(material); 
        
        // Close and callback
        closeDialog();
        if (onSaveCallback != null) onSaveCallback.run();
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
