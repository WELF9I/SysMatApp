package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.model.Materiel;
import com.institut.sysmat.desktop.service.MaterielService;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MaterielController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    
    @FXML private TableView<Materiel> materialsTable;
    @FXML private TableColumn<Materiel, Long> colId;
    @FXML private TableColumn<Materiel, String> colNom;
    @FXML private TableColumn<Materiel, String> colType;
    @FXML private TableColumn<Materiel, Integer> colQuantiteTotale;
    @FXML private TableColumn<Materiel, Integer> colQuantiteDisponible;
    @FXML private TableColumn<Materiel, String> colEtat;
    @FXML private TableColumn<Materiel, String> colLocalisation;
    @FXML private TableColumn<Materiel, String> colActions;
    
    @FXML private Label totalLabel;
    @FXML private Label availableLabel;
    @FXML private Label lowStockLabel;
    
    @FXML private Button addMaterialButton;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final MaterielService materielService = new MaterielService();
    
    private ObservableList<Materiel> allMaterials = FXCollections.observableArrayList();
    private ObservableList<Materiel> filteredMaterials = FXCollections.observableArrayList();
    
    private boolean isProfessorMode = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadMaterials();
        setupEventHandlers();
    }
    
    public void setProfessorMode(boolean professorMode) {
        this.isProfessorMode = professorMode;
        if (professorMode) {
            addMaterialButton.setVisible(false);
            addMaterialButton.setManaged(false);
        }
    }
    
    private void setupUI() {
        setupTableColumns();
        setupFilters();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTypeMateriel()));
        colQuantiteTotale.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantiteTotale()).asObject());
        colQuantiteDisponible.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantiteDisponible()).asObject());
        colEtat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtat()));
        colLocalisation.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocalisation()));
        
        // Color coding for availability
        colQuantiteDisponible.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(quantity.toString());
                    if (quantity == 0) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    } else if (quantity < 5) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Color coding for status
        colEtat.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "DISPONIBLE":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "EN_MAINTENANCE":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        case "HORS_SERVICE":
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
        
        // Actions column
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button viewButton = new Button("Voir");
            
            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");
                viewButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 12;");
                
                editButton.setOnAction(event -> {
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    showEditMaterialDialog(materiel);
                });
                
                deleteButton.setOnAction(event -> {
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    deleteMaterial(materiel);
                });
                
                viewButton.setOnAction(event -> {
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    showMaterialDetails(materiel);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    HBox buttons;
                    
                    if (isProfessorMode) {
                        buttons = new HBox(5, viewButton);
                    } else {
                        buttons = new HBox(5, editButton, deleteButton);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupFilters() {
        // Load filter options
        typeFilterCombo.getItems().addAll("Tous", "Connectique", "Audiovisuel", "Informatique", 
                                          "Réseau", "Pédagogique", "Électronique", "Électrique");
        typeFilterCombo.getSelectionModel().selectFirst();
        
        statusFilterCombo.getItems().addAll("Tous", "DISPONIBLE", "EN_MAINTENANCE", "HORS_SERVICE");
        statusFilterCombo.getSelectionModel().selectFirst();
    }
    
    private void loadMaterials() {
        showLoading(true);
        
        var service = materielService.getAllMateriels();
        service.setOnSucceeded(event -> {
            List<Materiel> materials = service.getValue();
            Platform.runLater(() -> {
                allMaterials.setAll(materials);
                filteredMaterials.setAll(materials);
                materialsTable.setItems(filteredMaterials);
                updateStats(materials);
                showLoading(false);
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les matériels");
            });
        });
        
        service.start();
    }
    
    private void updateStats(List<Materiel> materials) {
        int total = materials.size();
        int available = (int) materials.stream()
            .filter(m -> "DISPONIBLE".equals(m.getEtat()) && m.getQuantiteDisponible() > 0)
            .count();
        int lowStock = (int) materials.stream()
            .filter(m -> m.getQuantiteDisponible() < 5 && m.getQuantiteDisponible() > 0)
            .count();
        
        totalLabel.setText("Total: " + total + " matériels");
        availableLabel.setText("Disponibles: " + available);
        lowStockLabel.setText("Stock bas: " + lowStock);
    }
    
    @FXML
    private void filterMaterials() {
        String searchTerm = searchField.getText().toLowerCase();
        String selectedType = typeFilterCombo.getValue();
        String selectedStatus = statusFilterCombo.getValue();
        
        List<Materiel> filtered = allMaterials.stream()
            .filter(m -> searchTerm.isEmpty() || 
                        m.getNom().toLowerCase().contains(searchTerm) ||
                        m.getTypeMateriel().toLowerCase().contains(searchTerm) ||
                        (m.getDescription() != null && m.getDescription().toLowerCase().contains(searchTerm)))
            .filter(m -> "Tous".equals(selectedType) || m.getTypeMateriel().equals(selectedType))
            .filter(m -> "Tous".equals(selectedStatus) || m.getEtat().equals(selectedStatus))
            .toList();
        
        filteredMaterials.setAll(filtered);
        updateStats(filtered);
    }
    
    @FXML
    private void resetFilters() {
        searchField.clear();
        typeFilterCombo.getSelectionModel().selectFirst();
        statusFilterCombo.getSelectionModel().selectFirst();
        filteredMaterials.setAll(allMaterials);
        updateStats(allMaterials);
    }
    
    @FXML
    private void showAddMaterialDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/material-dialog.fxml"));
            Parent root = loader.load();
            
            MaterialDialogController controller = loader.getController();
            controller.setMode(MaterialDialogController.Mode.ADD);
            controller.setOnSaveCallback(() -> {
                loadMaterials(); // Refresh table
            });
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Ajouter un matériel");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }
    
    private void showEditMaterialDialog(Materiel materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/material-dialog.fxml"));
            Parent root = loader.load();
            
            MaterialDialogController controller = loader.getController();
            controller.setMode(MaterialDialogController.Mode.EDIT);
            controller.setMaterial(materiel);
            controller.setOnSaveCallback(() -> {
                loadMaterials(); // Refresh table
            });
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Modifier le matériel");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }
    
    private void showMaterialDetails(Materiel materiel) {
        StringBuilder details = new StringBuilder();
        details.append("Détails du matériel\n");
        details.append("===================\n");
        details.append("Nom: ").append(materiel.getNom()).append("\n");
        details.append("Type: ").append(materiel.getTypeMateriel()).append("\n");
        details.append("Quantité totale: ").append(materiel.getQuantiteTotale()).append("\n");
        details.append("Disponible: ").append(materiel.getQuantiteDisponible()).append("\n");
        details.append("État: ").append(materiel.getEtat()).append("\n");
        details.append("Localisation: ").append(materiel.getLocalisation()).append("\n");
        
        if (materiel.getDescription() != null && !materiel.getDescription().isEmpty()) {
            details.append("\nDescription:\n").append(materiel.getDescription());
        }
        
        DialogUtil.showInfo("Détails matériel", details.toString());
    }
    
    private void deleteMaterial(Materiel materiel) {
        if (DialogUtil.showConfirmation("Supprimer le matériel", 
                "Êtes-vous sûr de vouloir supprimer le matériel '" + materiel.getNom() + "' ?")) {
            showLoading(true);
            
            var service = materielService.deleteMateriel(materiel.getId());
            service.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Matériel supprimé avec succès");
                    loadMaterials(); // Refresh table
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de supprimer le matériel");
                });
            });
            
            service.start();
        }
    }
    
    private void setupEventHandlers() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMaterials());
        typeFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterMaterials());
        statusFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterMaterials());
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        loadMaterials();
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader;
            if (sessionManager.isAdmin()) {
                loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_ADMIN_DASHBOARD));
            } else {
                loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_PROF_DASHBOARD));
            }
            
            Parent root = loader.load();
            Stage stage = (Stage) addMaterialButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de revenir au tableau de bord");
        }
    }
    
    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        loadingPane.setManaged(show);
        loadingIndicator.setVisible(show);
    }
}