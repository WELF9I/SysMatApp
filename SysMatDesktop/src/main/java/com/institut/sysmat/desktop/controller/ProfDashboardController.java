package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.model.Materiel;
import com.institut.sysmat.desktop.model.Reservation;
import com.institut.sysmat.desktop.service.MaterielService;
import com.institut.sysmat.desktop.service.ReservationService;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfDashboardController implements Initializable {
    
    @FXML private Label welcomeLabel;
    @FXML private Label departmentLabel;
    @FXML private Label activeReservationsLabel;
    @FXML private Label pendingReservationsLabel;
    @FXML private Label availableMaterialsLabel;
    
    @FXML private TableView<Reservation> upcomingReservationsTable;
    @FXML private TableColumn<Reservation, String> reservationMaterialColumn;
    @FXML private TableColumn<Reservation, String> reservationDateColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;
    @FXML private TableColumn<Reservation, String> reservationActionsColumn;
    
    @FXML private PieChart materialsPieChart;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    @FXML private Button materialsButton;
    @FXML private Button newReservationButton;
    @FXML private Button myReservationsButton;
    @FXML private Button logoutButton;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final MaterielService materielService = new MaterielService();
    private final ReservationService reservationService = new ReservationService();
    
    private ObservableList<Reservation> upcomingReservations = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadDashboardData();
        setupEventHandlers();
    }
    
    private void setupUI() {
        welcomeLabel.setText("Bienvenue, " + sessionManager.getCurrentUserName());
        departmentLabel.setText("Département: " + sessionManager.getCurrentUser().getDepartement());
        
        setupReservationsTable();
        setupChart();
    }
    
    private void setupReservationsTable() {
        reservationMaterialColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getMaterielNom() + " x" + data.getValue().getQuantite()));
        reservationDateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDateDebut() + " → " + data.getValue().getFormattedDateFin()));
        reservationStatusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatusText()));
        
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
        
        reservationActionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button cancelButton = new Button("Annuler");
            private final Button detailsButton = new Button("Détails");
            
            {
                cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");
                detailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12;");
                
                cancelButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    if (reservation.isPending()) {
                        cancelReservation(reservation);
                    }
                });
                
                detailsButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    showReservationDetails(reservation);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5, detailsButton);
                    if (reservation.isPending()) {
                        buttons.getChildren().add(0, cancelButton);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupChart() {
        materialsPieChart.setTitle("Répartition des matériels");
        materialsPieChart.setLegendVisible(true);
        materialsPieChart.setLabelsVisible(true);
    }
    
    private void loadDashboardData() {
        showLoading(true);
        
        // Load user reservations
        var userReservationsService = reservationService.getUserReservations();
        userReservationsService.setOnSucceeded(event -> {
            List<Reservation> allReservations = userReservationsService.getValue();
            Platform.runLater(() -> {
                // Filter upcoming reservations (confirmed and future)
                upcomingReservations.setAll(allReservations.stream()
                    .filter(r -> r.isConfirmed() && r.getDateDebut().isAfter(LocalDateTime.now()))
                    .toList());
                
                upcomingReservationsTable.setItems(upcomingReservations);
                
                // Calculate statistics
                long activeCount = allReservations.stream()
                    .filter(Reservation::isActive)
                    .count();
                long pendingCount = allReservations.stream()
                    .filter(Reservation::isPending)
                    .count();
                
                activeReservationsLabel.setText(String.valueOf(activeCount));
                pendingReservationsLabel.setText(String.valueOf(pendingCount));
            });
        });
        userReservationsService.start();
        
        // Load available materials
        var availableMaterielsService = materielService.getAvailableMateriels();
        availableMaterielsService.setOnSucceeded(event -> {
            List<Materiel> availableMaterials = availableMaterielsService.getValue();
            Platform.runLater(() -> {
                availableMaterialsLabel.setText(String.valueOf(availableMaterials.size()));
                updateMaterialsChart(availableMaterials);
                showLoading(false);
            });
        });
        
        availableMaterielsService.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les données");
            });
        });
        availableMaterielsService.start();
    }
    
    private void updateMaterialsChart(List<Materiel> materials) {
        materialsPieChart.getData().clear();
        
        // Group by type
        materials.stream()
            .collect(java.util.stream.Collectors.groupingBy(Materiel::getTypeMateriel, 
                     java.util.stream.Collectors.counting()))
            .forEach((type, count) -> {
                PieChart.Data slice = new PieChart.Data(type + " (" + count + ")", count);
                materialsPieChart.getData().add(slice);
            });
        
        // Add colors
        String[] colors = {"#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6", "#1abc9c"};
        for (int i = 0; i < materialsPieChart.getData().size(); i++) {
            materialsPieChart.getData().get(i).getNode()
                .setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
        }
    }
    
    private void cancelReservation(Reservation reservation) {
        if (DialogUtil.showConfirmation("Annuler réservation", 
                "Êtes-vous sûr de vouloir annuler cette réservation ?")) {
            showLoading(true);
            
            reservationService.updateReservationStatus(reservation.getId(), "ANNULEE").start();
            reservationService.updateReservationStatus(reservation.getId(), "ANNULEE").setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Réservation annulée avec succès");
                    loadDashboardData(); // Refresh data
                });
            });
            
            reservationService.updateReservationStatus(reservation.getId(), "ANNULEE").setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible d'annuler la réservation");
                });
            });
        }
    }
    
    private void showReservationDetails(Reservation reservation) {
        StringBuilder details = new StringBuilder();
        details.append("Détails de la réservation\n");
        details.append("=========================\n");
        details.append("Matériel: ").append(reservation.getMaterielNom()).append("\n");
        details.append("Quantité: ").append(reservation.getQuantite()).append("\n");
        details.append("Date début: ").append(reservation.getFormattedDateDebut()).append("\n");
        details.append("Date fin: ").append(reservation.getFormattedDateFin()).append("\n");
        details.append("Statut: ").append(reservation.getStatusText()).append("\n");
        details.append("Date réservation: ").append(reservation.getFormattedDateReservation()).append("\n");
        
        if (reservation.getMotifUtilisation() != null && !reservation.getMotifUtilisation().isEmpty()) {
            details.append("Motif: ").append(reservation.getMotifUtilisation()).append("\n");
        }
        
        DialogUtil.showInfo("Détails réservation", details.toString());
    }
    
    private void setupEventHandlers() {
        materialsButton.setOnAction(event -> navigateToMaterials());
        newReservationButton.setOnAction(event -> navigateToNewReservation());
        myReservationsButton.setOnAction(event -> navigateToMyReservations());
        logoutButton.setOnAction(event -> handleLogout());
    }
    
    @FXML
    private void navigateToMaterials() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_MATERIELS));
            Parent root = loader.load();
            
            // Set professor mode
            MaterielController controller = loader.getController();
            controller.setProfessorMode(true);
            
            Stage stage = (Stage) materialsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des matériels");
        }
    }
    
    @FXML
    private void navigateToNewReservation() {
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
    private void navigateToMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_RESERVATIONS));
            Parent root = loader.load();
            
            // Set professor mode
            ReservationController controller = loader.getController();
            controller.setProfessorMode(true);
            
            Stage stage = (Stage) myReservationsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible de charger la page des réservations");
        }
    }
    
    @FXML
    private void handleLogout() {
        if (DialogUtil.showConfirmation("Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?")) {
            sessionManager.clearSession();
            navigateToLogin();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboardData();
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
    
    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        loadingPane.setManaged(show);
        loadingIndicator.setVisible(show);
    }
}