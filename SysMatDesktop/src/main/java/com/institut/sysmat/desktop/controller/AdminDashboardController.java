package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.model.Materiel;
import com.institut.sysmat.desktop.model.Reservation;
import com.institut.sysmat.desktop.service.MaterielService;
import com.institut.sysmat.desktop.service.ReservationService;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    
    @FXML private Label welcomeLabel;
    @FXML private Label pendingReservationsLabel;
    @FXML private Label totalMaterialsLabel;
    @FXML private Label availableMaterialsLabel;
    @FXML private Label lowStockMaterialsLabel;
    
    @FXML private TableView<Reservation> pendingReservationsTable;
    @FXML private TableColumn<Reservation, String> reservationUserColumn;
    @FXML private TableColumn<Reservation, String> reservationMaterialColumn;
    @FXML private TableColumn<Reservation, String> reservationDateColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;
    
    @FXML private TableView<Materiel> lowStockTable;
    @FXML private TableColumn<Materiel, String> materialNameColumn;
    @FXML private TableColumn<Materiel, Integer> materialStockColumn;
    @FXML private TableColumn<Materiel, String> materialLocationColumn;
    
    @FXML private BarChart<String, Number> usageChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final MaterielService materielService = new MaterielService();
    private final ReservationService reservationService = new ReservationService();
    
    private ObservableList<Reservation> pendingReservations = FXCollections.observableArrayList();
    private ObservableList<Materiel> lowStockMaterials = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadDashboardData();
        setupEventHandlers();
    }
    
    private void setupUI() {
        // Personnaliser le message de bienvenue
        welcomeLabel.setText("Bienvenue, " + sessionManager.getCurrentUserName());
        
        // Configurer les tables
        setupReservationsTable();
        setupMaterialsTable();
        setupChart();
    }
    
    private void setupReservationsTable() {
        reservationUserColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getUtilisateurFullName()));
        reservationMaterialColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getMaterielNom() + " x" + data.getValue().getQuantite()));
        reservationDateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDateDebut() + " → " + data.getValue().getFormattedDateFin()));
        reservationStatusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatusText()));
        
        // Personnaliser la couleur du statut
        reservationStatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    setStyle("-fx-text-fill: " + reservation.getStatusColor() + "; -fx-font-weight: bold;");
                }
            }
        });
        
        // Double-click pour valider/refuser
        pendingReservationsTable.setRowFactory(tv -> {
            TableRow<Reservation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Reservation reservation = row.getItem();
                    showReservationActionDialog(reservation);
                }
            });
            return row;
        });
    }
    
    private void setupMaterialsTable() {
        materialNameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNom()));
        materialStockColumn.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantiteDisponible()).asObject());
        materialLocationColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getLocalisation()));
        
        // Personnaliser la colonne stock avec couleur
        materialStockColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(stock.toString());
                    Materiel materiel = getTableView().getItems().get(getIndex());
                    if (stock == 0) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    } else if (stock < 5) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
    private void setupChart() {
        usageChart.setTitle("Utilisation des matériels (7 derniers jours)");
        usageChart.setLegendVisible(false);
        usageChart.setAnimated(false);
    }
    
    private void loadDashboardData() {
        showLoading(true);
        
        // Charger les statistiques
        var pendingCountService = reservationService.getPendingReservationsCount();
        pendingCountService.setOnSucceeded(event -> {
            Long pendingCount = pendingCountService.getValue();
            Platform.runLater(() -> pendingReservationsLabel.setText(pendingCount.toString()));
        });
        pendingCountService.start();
        
        // Charger les réservations en attente
        var pendingReservationsService = reservationService.getPendingReservations();
        pendingReservationsService.setOnSucceeded(event -> {
            pendingReservations = pendingReservationsService.getValue();
            Platform.runLater(() -> {
                pendingReservationsTable.setItems(pendingReservations);
                pendingReservationsLabel.setText(String.valueOf(pendingReservations.size()));
            });
        });
        pendingReservationsService.start();
        
        // Charger tous les matériels pour calculer les statistiques
        var allMaterielsService = materielService.getAllMateriels();
        allMaterielsService.setOnSucceeded(event -> {
            ObservableList<Materiel> allMaterials = allMaterielsService.getValue();
            Platform.runLater(() -> {
                int total = allMaterials.size();
                int available = (int) allMaterials.stream().filter(Materiel::isAvailable).count();
                int lowStock = (int) allMaterials.stream()
                    .filter(m -> m.isAvailable() && m.getQuantiteDisponible() < 5)
                    .count();
                
                totalMaterialsLabel.setText(String.valueOf(total));
                availableMaterialsLabel.setText(String.valueOf(available));
                lowStockMaterialsLabel.setText(String.valueOf(lowStock));
                
                // Remplir la table des stocks bas
                lowStockMaterials.setAll(allMaterials.stream()
                    .filter(m -> m.getQuantiteDisponible() < 5)
                    .toList());
                lowStockTable.setItems(lowStockMaterials);
                
                // Mettre à jour le graphique
                updateChart(allMaterials);
                
                showLoading(false);
            });
        });
        
        allMaterielsService.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les données");
            });
        });
        allMaterielsService.start();
    }
    
    private void updateChart(ObservableList<Materiel> materials) {
        usageChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Simuler des données d'utilisation (dans un vrai projet, récupérer depuis l'API)
        String[] categories = {"Double-fiches", "Projecteurs", "Laptops", "Switchs", "Tableaux"};
        int[] usage = {45, 23, 18, 12, 8};
        
        for (int i = 0; i < categories.length; i++) {
            series.getData().add(new XYChart.Data<>(categories[i], usage[i]));
        }
        
        usageChart.getData().add(series);
    }
    
    private void setupEventHandlers() {
        // Handlers for dashboard items if any
    }

    @FXML
    private void navigateToMaterials() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_MATERIELS));
            Parent root = loader.load();
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
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
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des réservations");
        }
    }
    
    private void showReservationActionDialog(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/reservation-action-dialog.fxml"));
            Parent root = loader.load();
            
            ReservationActionDialogController controller = loader.getController();
            controller.setReservation(reservation);
            controller.setOnActionCallback(() -> {
                // Recharger les données après action
                loadDashboardData();
            });
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Action sur réservation");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible d'ouvrir la boîte de dialogue");
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboardData();
    }
    
    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        loadingPane.setManaged(show);
        loadingIndicator.setVisible(show);
    }
}