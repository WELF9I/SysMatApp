package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.service.ReservationService;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> exportFormatCombo;
    
    // Overview Tab
    @FXML private Label totalReservationsLabel;
    @FXML private Label usedMaterialsLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label occupancyRateLabel;
    
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> materialUsageChart;
    @FXML private CategoryAxis materialXAxis;
    @FXML private NumberAxis materialYAxis;
    
    // Analysis Tab
    @FXML private LineChart<String, Number> reservationsTrendChart;
    @FXML private CategoryAxis trendXAxis;
    @FXML private NumberAxis trendYAxis;
    
    @FXML private TableView<?> departmentTable;
    
    @FXML private BarChart<String, Number> peakHoursChart;
    @FXML private CategoryAxis hoursXAxis;
    @FXML private NumberAxis hoursYAxis;
    
    // Export Tab
    @FXML private CheckBox exportMaterialsCheck;
    @FXML private CheckBox exportReservationsCheck;
    @FXML private CheckBox exportUsersCheck;
    @FXML private CheckBox exportStatisticsCheck;
    @FXML private CheckBox exportLogsCheck;
    @FXML private CheckBox includeChartsCheck;
    
    @FXML private RadioButton pdfRadio;
    @FXML private RadioButton excelRadio;
    @FXML private RadioButton csvRadio;
    private ToggleGroup formatGroup;
    
    @FXML private TableView<?> exportHistoryTable;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ReservationService reservationService = new ReservationService();
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadReports();
        setupEventHandlers();
    }
    
    private void setupUI() {
        // Set default date range (last 30 days)
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        
        // Setup export format
        exportFormatCombo.getItems().addAll("PDF", "Excel", "CSV");
        exportFormatCombo.getSelectionModel().selectFirst();
        
        // Setup toggle group for export format
        formatGroup = new ToggleGroup();
        pdfRadio.setToggleGroup(formatGroup);
        excelRadio.setToggleGroup(formatGroup);
        csvRadio.setToggleGroup(formatGroup);
        pdfRadio.setSelected(true);
        
        // Setup charts
        setupCharts();
    }
    
    private void setupCharts() {
        // Pie chart settings
        statusPieChart.setTitle("Répartition par statut");
        statusPieChart.setLegendVisible(true);
        statusPieChart.setLabelsVisible(true);
        
        // Material usage chart
        materialUsageChart.setTitle("Matériels les plus utilisés");
        materialXAxis.setLabel("Matériel");
        materialYAxis.setLabel("Nombre de réservations");
        
        // Trend chart
        reservationsTrendChart.setTitle("Évolution des réservations");
        trendXAxis.setLabel("Date");
        trendYAxis.setLabel("Nombre de réservations");
        
        // Peak hours chart
        peakHoursChart.setTitle("Heures de pointe");
        hoursXAxis.setLabel("Heure");
        hoursYAxis.setLabel("Nombre de réservations");
    }
    
    private void loadReports() {
        showLoading(true);
        
        // Load basic statistics
        var service = reservationService.getPendingReservationsCount();
        service.setOnSucceeded(event -> {
            // Load more data here...
            Platform.runLater(() -> {
                updateChartsWithSampleData(); // Replace with real data
                showLoading(false);
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les rapports");
            });
        });
        
        service.start();
    }
    
    private void updateChartsWithSampleData() {
        // Status Pie Chart
        statusPieChart.getData().clear();
        statusPieChart.getData().addAll(
            new PieChart.Data("Confirmées", 45),
            new PieChart.Data("En attente", 15),
            new PieChart.Data("Annulées", 8),
            new PieChart.Data("Refusées", 5),
            new PieChart.Data("Terminées", 27)
        );
        
        // Material Usage Bar Chart
        materialUsageChart.getData().clear();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Utilisation");
        series1.getData().add(new XYChart.Data<>("Double-fiches", 45));
        series1.getData().add(new XYChart.Data<>("Projecteurs", 23));
        series1.getData().add(new XYChart.Data<>("Laptops", 18));
        series1.getData().add(new XYChart.Data<>("Switchs", 12));
        series1.getData().add(new XYChart.Data<>("Tableaux", 8));
        materialUsageChart.getData().add(series1);
        
        // Trend Line Chart
        reservationsTrendChart.getData().clear();
        XYChart.Series<String, Number> trendSeries = new XYChart.Series<>();
        trendSeries.setName("Réservations");
        String[] dates = {"01/06", "08/06", "15/06", "22/06", "29/06"};
        int[] values = {12, 18, 15, 22, 20};
        for (int i = 0; i < dates.length; i++) {
            trendSeries.getData().add(new XYChart.Data<>(dates[i], values[i]));
        }
        reservationsTrendChart.getData().add(trendSeries);
        
        // Peak Hours Bar Chart
        peakHoursChart.getData().clear();
        XYChart.Series<String, Number> peakSeries = new XYChart.Series<>();
        peakSeries.setName("Réservations");
        String[] hours = {"08h", "10h", "12h", "14h", "16h", "18h"};
        int[] peakValues = {8, 15, 12, 18, 10, 5};
        for (int i = 0; i < hours.length; i++) {
            peakSeries.getData().add(new XYChart.Data<>(hours[i], peakValues[i]));
        }
        peakHoursChart.getData().add(peakSeries);
        
        // Update stats labels
        totalReservationsLabel.setText("100");
        usedMaterialsLabel.setText("15");
        activeUsersLabel.setText("8");
        occupancyRateLabel.setText("72%");
    }
    
    @FXML
    private void generateReport() {
        showLoading(true);
        
        // Simulate report generation
        Platform.runLater(() -> {
            try {
                Thread.sleep(1000); // Simulate processing time
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Rapport généré avec succès");
                    updateChartsWithSampleData(); // Refresh data
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Erreur lors de la génération du rapport");
                });
            }
        });
    }
    
    @FXML
    private void exportReport() {
        String format = exportFormatCombo.getValue();
        
        if (format == null) {
            DialogUtil.showError("Erreur", "Veuillez sélectionner un format d'export");
            return;
        }
        
        showLoading(true);
        
        // Simulate export process
        Platform.runLater(() -> {
            try {
                Thread.sleep(1500); // Simulate export time
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Export réussi", 
                        "Rapport exporté au format " + format + " avec succès");
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Erreur lors de l'export");
                });
            }
        });
    }
    
    @FXML
    private void generateExport() {
        // Validate selections
        if (!exportMaterialsCheck.isSelected() && 
            !exportReservationsCheck.isSelected() && 
            !exportUsersCheck.isSelected() && 
            !exportStatisticsCheck.isSelected()) {
            DialogUtil.showError("Erreur", "Veuillez sélectionner au moins un type de données à exporter");
            return;
        }
        
        String format = pdfRadio.isSelected() ? "PDF" : 
                       excelRadio.isSelected() ? "Excel" : "CSV";
        
        showLoading(true);
        
        // Simulate export generation
        Platform.runLater(() -> {
            try {
                Thread.sleep(2000); // Simulate processing time
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Export généré", 
                        "Votre export " + format + " a été généré avec succès.\n" +
                        "Le fichier sera téléchargé automatiquement.");
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Erreur lors de la génération de l'export");
                });
            }
        });
    }
    
    private void setupEventHandlers() {
        // Add any event handlers needed
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        loadReports();
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_ADMIN_DASHBOARD));
            Parent root = loader.load();
            
            Stage stage = (Stage) startDatePicker.getScene().getWindow();
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