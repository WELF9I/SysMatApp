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
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    
    @FXML private Label pageSubtitle;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> materialFilterCombo;
    @FXML private TextField userSearchField;
    
    @FXML private Label pendingCountLabel;
    @FXML private Label confirmedCountLabel;
    @FXML private Label cancelledCountLabel;
    
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Long> colId;
    @FXML private TableColumn<Reservation, String> colUser;
    @FXML private TableColumn<Reservation, String> colMaterial;
    @FXML private TableColumn<Reservation, Integer> colQuantity;
    @FXML private TableColumn<Reservation, String> colDateDebut;
    @FXML private TableColumn<Reservation, String> colDateFin;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, String> colDateReservation;
    @FXML private TableColumn<Reservation, String> colActions;
    
    @FXML private Button exportButton;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    // New Reservation Form Fields
    @FXML private ComboBox<Materiel> materialComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private Label durationLabel;
    @FXML private TextArea motifTextArea;
    
    @FXML private Label materialNameLabel;
    @FXML private Label availableQuantityLabel;
    @FXML private Label materialLocationLabel;
    
    @FXML private Button submitButton;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final MaterielService materielService = new MaterielService();
    private final ReservationService reservationService = new ReservationService();
    
    private ObservableList<Reservation> allReservations = FXCollections.observableArrayList();
    private ObservableList<Reservation> filteredReservations = FXCollections.observableArrayList();
    private ObservableList<Materiel> availableMaterials = FXCollections.observableArrayList();
    
    private boolean isProfessorMode = false;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Determine mode based on current user role
        if (sessionManager.getCurrentUser() != null) {
            isProfessorMode = AppConfig.ROLE_PROFESSEUR.equals(sessionManager.getCurrentUser().getRole());
        }
        
        // Determine which form we're initializing based on FXML elements
        if (reservationsTable != null) {
            setupReservationListUI();
            loadReservations();
        } else if (materialComboBox != null) {
            setupNewReservationUI();
            loadAvailableMaterials();
        }
        setupEventHandlers();
    }
    
    public void setProfessorMode(boolean professorMode) {
        this.isProfessorMode = professorMode;
        if (professorMode && pageSubtitle != null) {
            pageSubtitle.setText("Visualisez et gérez vos réservations");
        }
    }
    
    // ===== RESERVATION LIST METHODS =====
    private void setupReservationListUI() {
        setupTableColumns();
        setupFilters();
        
        if (isProfessorMode) {
            exportButton.setVisible(false);
            exportButton.setManaged(false);
        }
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colUser.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getUtilisateurFullName()));
        colMaterial.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getMaterielNom() + " x" + data.getValue().getQuantite()));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantite()).asObject());
        colDateDebut.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDateDebut()));
        colDateFin.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDateFin()));
        colStatus.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatusText()));
        colDateReservation.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDateReservation()));
        
        // Color coding for status
        colStatus.setCellFactory(column -> new TableCell<>() {
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
        
        // Actions column
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button validateButton = new Button("Valider");
            private final Button rejectButton = new Button("Refuser");
            private final Button cancelButton = new Button("Annuler");
            private final Button detailsButton = new Button("Détails");
            
            {
                validateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12;");
                rejectButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 12;");
                cancelButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 12;");
                detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12;");
                
                validateButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    updateReservationStatus(reservation, "CONFIRMEE");
                });
                
                rejectButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    updateReservationStatus(reservation, "REFUSEE");
                });
                
                cancelButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    updateReservationStatus(reservation, "ANNULEE");
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
                    
                    if (sessionManager.isAdmin()) {
                        if (reservation.isPending()) {
                            buttons.getChildren().add(0, validateButton);
                            buttons.getChildren().add(1, rejectButton);
                        }
                    } else if (sessionManager.isProfesseur()) {
                        if (reservation.isPending()) {
                            buttons.getChildren().add(0, cancelButton);
                        }
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupFilters() {
        // Set default dates (last 30 days)
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        
        // Status filter options
        statusFilterCombo.getItems().addAll("Tous", "EN_ATTENTE", "CONFIRMEE", "EN_COURS", 
                                           "TERMINEE", "ANNULEE", "REFUSEE");
        statusFilterCombo.getSelectionModel().selectFirst();
        
        // Material filter options (init with Tous)
        materialFilterCombo.getItems().add("Tous");
        materialFilterCombo.getSelectionModel().selectFirst();
    }
    
    private void loadReservations() {
        showLoading(true);
        
        if (isProfessorMode) {
            // Load user's reservations
            var service = reservationService.getUserReservations();
            service.setOnSucceeded(event -> {
                List<Reservation> reservations = service.getValue();
                Platform.runLater(() -> {
                    allReservations.setAll(reservations);
                    filteredReservations.setAll(reservations);
                    reservationsTable.setItems(filteredReservations);
                    updateStats(reservations);
                    loadMaterialFilterOptions(reservations);
                    showLoading(false);
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de charger les réservations");
                });
            });
            
            service.start();
        } else {
            // Load all reservations (admin)
            var service = reservationService.getAllReservations();
            service.setOnSucceeded(event -> {
                List<Reservation> reservations = service.getValue();
                Platform.runLater(() -> {
                    allReservations.setAll(reservations);
                    filteredReservations.setAll(reservations);
                    reservationsTable.setItems(filteredReservations);
                    updateStats(reservations);
                    loadMaterialFilterOptions(reservations);
                    showLoading(false);
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de charger les réservations");
                });
            });
            
            service.start();
        }
    }
    
    private void updateStats(List<Reservation> reservations) {
        long pending = reservations.stream().filter(Reservation::isPending).count();
        long confirmed = reservations.stream().filter(Reservation::isConfirmed).count();
        long cancelled = reservations.stream()
            .filter(r -> "ANNULEE".equals(r.getStatut()) || "REFUSEE".equals(r.getStatut()))
            .count();
        
        pendingCountLabel.setText(String.valueOf(pending));
        confirmedCountLabel.setText(String.valueOf(confirmed));
        cancelledCountLabel.setText(String.valueOf(cancelled));
    }
    
    private void loadMaterialFilterOptions(List<Reservation> reservations) {
        List<String> materialNames = reservations.stream()
            .map(Reservation::getMaterielNom)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .toList();
        
        materialFilterCombo.getItems().clear();
        materialFilterCombo.getItems().add("Tous");
        materialFilterCombo.getItems().addAll(materialNames);
        materialFilterCombo.getSelectionModel().selectFirst();
    }
    
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    @FXML
    private void applyFilters() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        
        String selectedStatus = statusFilterCombo.getValue();
        String selectedMaterial = materialFilterCombo.getValue();
        String searchTerm = userSearchField.getText().toLowerCase();
        
        LocalTime startTime = null;
        LocalTime endTime = null;
        
        try {
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                startTime = LocalTime.parse(startTimeStr, timeFormatter);
            }
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                endTime = LocalTime.parse(endTimeStr, timeFormatter);
            }
        } catch (Exception e) {
            // Ignore invalid time format for filtering
        }
        
        final LocalTime finalStartTime = startTime;
        final LocalTime finalEndTime = endTime;
        
        List<Reservation> filtered = allReservations.stream()
            .filter(r -> {
                if (startDate == null) return true;
                LocalDateTime startDateTime = r.getDateDebut();
                if (finalStartTime != null) {
                    return startDateTime.isAfter(LocalDateTime.of(startDate, finalStartTime).minusSeconds(1));
                }
                return startDateTime.toLocalDate().isAfter(startDate.minusDays(1));
            })
            .filter(r -> {
                if (endDate == null) return true;
                LocalDateTime endDateTime = r.getDateDebut(); // Filter based on start date usually
                if (finalEndTime != null) {
                    return endDateTime.isBefore(LocalDateTime.of(endDate, finalEndTime).plusSeconds(1));
                }
                return endDateTime.toLocalDate().isBefore(endDate.plusDays(1));
            })
            .filter(r -> "Tous".equals(selectedStatus) || r.getStatut().equals(selectedStatus))
            .filter(r -> "Tous".equals(selectedMaterial) || r.getMaterielNom().equals(selectedMaterial))
            .filter(r -> searchTerm.isEmpty() || 
                        r.getUtilisateurFullName().toLowerCase().contains(searchTerm) ||
                        r.getMotifUtilisation().toLowerCase().contains(searchTerm))
            .toList();
        
        filteredReservations.setAll(filtered);
        updateStats(filtered);
    }
    
    @FXML
    private void resetFilters() {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        if (startTimeField != null) startTimeField.clear();
        if (endTimeField != null) endTimeField.clear();
        statusFilterCombo.getSelectionModel().selectFirst();
        materialFilterCombo.getSelectionModel().selectFirst();
        userSearchField.clear();
        filteredReservations.setAll(allReservations);
        updateStats(allReservations);
    }
    
    private void updateReservationStatus(Reservation reservation, String newStatus) {
        String action = newStatus.equals("CONFIRMEE") ? "valider" : 
                       newStatus.equals("REFUSEE") ? "refuser" : "annuler";
        
        if (DialogUtil.showConfirmation("Confirmation", 
                "Êtes-vous sûr de vouloir " + action + " cette réservation ?")) {
            showLoading(true);
            
            var service = reservationService.updateReservationStatus(reservation.getId(), newStatus);
            service.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Réservation " + action + "ée avec succès");
                    loadReservations(); // Refresh table
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de mettre à jour la réservation");
                });
            });
            
            service.start();
        }
    }
    
    private void showReservationDetails(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/reservation-details-dialog.fxml"));
            Parent root = loader.load();
            
            ReservationDetailsController controller = loader.getController();
            controller.setReservation(reservation);
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Détails de la réservation");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to simple dialog
            StringBuilder details = new StringBuilder();
            details.append("Détails de la réservation #").append(reservation.getId()).append("\n");
            details.append("===============================\n\n");
            details.append("Utilisateur: ").append(reservation.getUtilisateurFullName()).append("\n");
            details.append("Matériel: ").append(reservation.getMaterielNom()).append("\n");
            details.append("Quantité: ").append(reservation.getQuantite()).append("\n");
            details.append("Date début: ").append(reservation.getFormattedDateDebut()).append("\n");
            details.append("Date fin: ").append(reservation.getFormattedDateFin()).append("\n");
            details.append("Statut: ").append(reservation.getStatusText()).append("\n");
            details.append("Date réservation: ").append(reservation.getFormattedDateReservation()).append("\n\n");
            
            if (reservation.getMotifUtilisation() != null && !reservation.getMotifUtilisation().isEmpty()) {
                details.append("Motif d'utilisation:\n").append(reservation.getMotifUtilisation());
            }
            
            DialogUtil.showInfo("Détails réservation", details.toString());
        }
    }
    
    // ===== NEW RESERVATION METHODS =====
    private void setupNewReservationUI() {
        // Setup time options
        for (int hour = 8; hour <= 20; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
        
        // Set default times
        startTimeCombo.getSelectionModel().select("08:30");
        endTimeCombo.getSelectionModel().select("10:30");
        updateDuration();
        
        // Set default date
        datePicker.setValue(LocalDate.now().plusDays(1));
        
        // Setup material combo box
        materialComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Materiel materiel, boolean empty) {
                super.updateItem(materiel, empty);
                if (empty || materiel == null) {
                    setText(null);
                } else {
                    setText(materiel.getNom() + " (" + materiel.getTypeMateriel() + 
                           ") - Disponible: " + materiel.getQuantiteDisponible());
                }
            }
        });
        
        materialComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Materiel materiel, boolean empty) {
                super.updateItem(materiel, empty);
                if (empty || materiel == null) {
                    setText(null);
                } else {
                    setText(materiel.getNom());
                }
            }
        });
        
        // Setup quantity spinner
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }
    
    private void loadAvailableMaterials() {
        showLoading(true);
        
        var service = materielService.getAvailableMateriels();
        service.setOnSucceeded(event -> {
            List<Materiel> materials = service.getValue();
            Platform.runLater(() -> {
                availableMaterials.setAll(materials);
                materialComboBox.setItems(availableMaterials);
                showLoading(false);
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les matériels disponibles");
            });
        });
        
        service.start();
    }
    
    @FXML
    private void onMaterialSelected() {
        Materiel selected = materialComboBox.getValue();
        if (selected != null) {
            materialNameLabel.setText(selected.getNom());
            availableQuantityLabel.setText(String.valueOf(selected.getQuantiteDisponible()));
            materialLocationLabel.setText(selected.getLocalisation());
            
            // Update quantity spinner max value
            ((SpinnerValueFactory.IntegerSpinnerValueFactory) quantitySpinner.getValueFactory()).setMax(selected.getQuantiteDisponible());
            
            // Show material info
            materialNameLabel.getParent().setVisible(true);
        }
    }
    
    @FXML
    private void updateDuration() {
        String start = startTimeCombo.getValue();
        String end = endTimeCombo.getValue();
        
        if (start != null && end != null) {
            try {
                LocalTime startTime = LocalTime.parse(start, timeFormatter);
                LocalTime endTime = LocalTime.parse(end, timeFormatter);
                
                if (endTime.isAfter(startTime)) {
                    long hours = java.time.Duration.between(startTime, endTime).toHours();
                    long minutes = java.time.Duration.between(startTime, endTime).toMinutes() % 60;
                    
                    if (hours > 4) {
                        durationLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                        durationLabel.setText("Durée: " + hours + "h" + (minutes > 0 ? minutes : "") + " (MAX 4h)");
                        submitButton.setDisable(true);
                    } else {
                        durationLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        durationLabel.setText("Durée: " + hours + "h" + (minutes > 0 ? minutes : ""));
                        submitButton.setDisable(false);
                    }
                } else {
                    durationLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    durationLabel.setText("L'heure de fin doit être après l'heure de début");
                    submitButton.setDisable(true);
                }
            } catch (Exception e) {
                durationLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                durationLabel.setText("Format d'heure invalide");
                submitButton.setDisable(true);
            }
        }
    }
    
    @FXML
    private void checkAvailability() {
        // TODO: Implement availability check with backend
        Materiel selected = materialComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String start = startTimeCombo.getValue();
        String end = endTimeCombo.getValue();
        
        if (selected != null && date != null && start != null && end != null) {
            // Simulate availability check
            boolean available = true; // Replace with actual API call
            
            if (available) {
                // Show availability message
            } else {
                // Show conflict message
            }
        }
    }
    
    @FXML
    private void submitReservation() {
        // Validate form
        if (materialComboBox.getValue() == null) {
            DialogUtil.showError("Erreur", "Veuillez sélectionner un matériel");
            return;
        }
        
        if (datePicker.getValue() == null) {
            DialogUtil.showError("Erreur", "Veuillez sélectionner une date");
            return;
        }
        
        if (startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            DialogUtil.showError("Erreur", "Veuillez sélectionner les heures de début et fin");
            return;
        }
        
        if (motifTextArea.getText() == null || motifTextArea.getText().trim().isEmpty()) {
            DialogUtil.showError("Erreur", "Veuillez saisir un motif d'utilisation");
            return;
        }
        
        try {
            // Prepare reservation request
            ReservationService.ReservationRequest request = new ReservationService.ReservationRequest();
            request.setMaterielId(materialComboBox.getValue().getId());
            request.setQuantite(quantitySpinner.getValue());
            
            // Combine date and time
            LocalDate date = datePicker.getValue();
            LocalTime startTime = LocalTime.parse(startTimeCombo.getValue(), timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeCombo.getValue(), timeFormatter);
            
            request.setDateDebut(LocalDateTime.of(date, startTime));
            request.setDateFin(LocalDateTime.of(date, endTime));
            request.setMotifUtilisation(motifTextArea.getText().trim());
            
            // Submit reservation
            showLoading(true);
            var service = reservationService.createReservation(request);
            service.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Réservation créée avec succès");
                    
                    // Navigate back to dashboard
                    navigateToDashboard();
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de créer la réservation");
                });
            });
            
            service.start();
            
        } catch (Exception e) {
            DialogUtil.showError("Erreur", "Format de date/heure invalide");
        }
    }
    
    @FXML
    private void cancelForm() {
        navigateToDashboard();
    }
    
    // ===== COMMON METHODS =====
    private void setupEventHandlers() {
        // Time combo boxes listeners
        if (startTimeCombo != null) {
            startTimeCombo.valueProperty().addListener((observable, oldValue, newValue) -> updateDuration());
        }
        if (endTimeCombo != null) {
            endTimeCombo.valueProperty().addListener((observable, oldValue, newValue) -> updateDuration());
        }
        
        // Material combo box listener
        if (materialComboBox != null) {
            materialComboBox.valueProperty().addListener((observable, oldValue, newValue) -> onMaterialSelected());
        }
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        if (reservationsTable != null) {
            loadReservations();
        } else if (materialComboBox != null) {
            loadAvailableMaterials();
        }
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
            Stage stage;
            
            if (submitButton != null) {
                stage = (Stage) submitButton.getScene().getWindow();
            } else if (exportButton != null) {
                stage = (Stage) exportButton.getScene().getWindow();
            } else {
                return;
            }
            
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