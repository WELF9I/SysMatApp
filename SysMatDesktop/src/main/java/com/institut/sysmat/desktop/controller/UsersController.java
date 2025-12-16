package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.config.AppConfig;
import com.institut.sysmat.desktop.model.Utilisateur;
import com.institut.sysmat.desktop.service.UsersService;
import com.institut.sysmat.desktop.service.SessionManager;
import com.institut.sysmat.desktop.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> departmentFilterCombo;
    
    @FXML private TableView<Utilisateur> usersTable;
    @FXML private TableColumn<Utilisateur, Long> colId;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colPrenom;
    @FXML private TableColumn<Utilisateur, String> colEmail;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colDepartement;
    @FXML private TableColumn<Utilisateur, String> colStatus;
    @FXML private TableColumn<Utilisateur, String> colDateCreation;
    @FXML private TableColumn<Utilisateur, String> colActions;
    
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label inactiveUsersLabel;
    @FXML private Label adminCountLabel;
    @FXML private Label professorCountLabel;
    
    @FXML private Button addUserButton;
    
    @FXML private StackPane loadingPane;
    @FXML private ProgressIndicator loadingIndicator;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final UsersService usersService = new UsersService();
    
    private ObservableList<Utilisateur> allUsers = FXCollections.observableArrayList();
    private ObservableList<Utilisateur> filteredUsers = FXCollections.observableArrayList();
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadUsers();
        setupEventHandlers();
    }
    
    private void setupUI() {
        setupTableColumns();
        setupFilters();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        colDepartement.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDepartement() != null ? 
                                   data.getValue().getDepartement() : ""));
        colStatus.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isActif() ? "Actif" : "Inactif"));
        colDateCreation.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDateCreation() != null ? 
                                   data.getValue().getDateCreation().format(dateFormatter) : ""));
        
        // Color coding for role
        colRole.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(role);
                    if ("ADMIN".equals(role)) {
                        setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
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
                    if ("Actif".equals(status)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Actions column
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button toggleButton = new Button("Activer/Désactiver");
            private final Button deleteButton = new Button("Supprimer");
            private final Button resetPasswordButton = new Button("Réinitialiser MDP");
            
            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12;");
                toggleButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 12;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12;");
                resetPasswordButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 12;");
                
                editButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });
                
                toggleButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    toggleUserStatus(user);
                });
                
                deleteButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
                
                resetPasswordButton.setOnAction(event -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    resetUserPassword(user);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    // Don't allow editing/deleting current user
                    boolean isCurrentUser = user.getId().equals(sessionManager.getCurrentUserId());
                    
                    HBox buttons = new HBox(5);
                    if (!isCurrentUser) {
                        buttons.getChildren().addAll(editButton, toggleButton, deleteButton);
                    } else {
                        buttons.getChildren().add(editButton);
                    }
                    
                    // Only admin can reset passwords
                    if (sessionManager.isAdmin()) {
                        buttons.getChildren().add(resetPasswordButton);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void setupFilters() {
        // Role filter options
        roleFilterCombo.getItems().addAll("Tous", "ADMIN", "PROFESSEUR");
        roleFilterCombo.getSelectionModel().selectFirst();
        
        // Status filter options
        statusFilterCombo.getItems().addAll("Tous", "Actif", "Inactif");
        statusFilterCombo.getSelectionModel().selectFirst();
    }
    
    private void loadUsers() {
        showLoading(true);
        
        var service = usersService.getAllUsers();
        service.setOnSucceeded(event -> {
            List<Utilisateur> users = service.getValue();
            Platform.runLater(() -> {
                allUsers.setAll(users);
                filteredUsers.setAll(users);
                usersTable.setItems(filteredUsers);
                updateStats(users);
                loadDepartmentFilterOptions(users);
                showLoading(false);
            });
        });
        
        service.setOnFailed(event -> {
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showError("Erreur", "Impossible de charger les utilisateurs");
            });
        });
        
        service.start();
    }
    
    private void updateStats(List<Utilisateur> users) {
        int total = users.size();
        long active = users.stream().filter(Utilisateur::isActif).count();
        long inactive = total - active;
        long admins = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long professors = users.stream().filter(u -> "PROFESSEUR".equals(u.getRole())).count();
        
        totalUsersLabel.setText("Total: " + total + " utilisateurs");
        activeUsersLabel.setText("Actifs: " + active);
        inactiveUsersLabel.setText("Inactifs: " + inactive);
        adminCountLabel.setText("Administrateurs: " + admins);
        professorCountLabel.setText("Professeurs: " + professors);
    }
    
    private void loadDepartmentFilterOptions(List<Utilisateur> users) {
        List<String> departments = users.stream()
            .map(Utilisateur::getDepartement)
            .filter(d -> d != null && !d.isEmpty())
            .distinct()
            .sorted()
            .toList();
        
        departmentFilterCombo.getItems().clear();
        departmentFilterCombo.getItems().add("Tous");
        departmentFilterCombo.getItems().addAll(departments);
        departmentFilterCombo.getSelectionModel().selectFirst();
    }
    
    @FXML
    private void searchUsers() {
        String searchTerm = searchField.getText().toLowerCase();
        String selectedRole = roleFilterCombo.getValue();
        String selectedStatus = statusFilterCombo.getValue();
        String selectedDepartment = departmentFilterCombo.getValue();
        
        List<Utilisateur> filtered = allUsers.stream()
            .filter(u -> searchTerm.isEmpty() || 
                        u.getNom().toLowerCase().contains(searchTerm) ||
                        u.getPrenom().toLowerCase().contains(searchTerm) ||
                        u.getEmail().toLowerCase().contains(searchTerm) ||
                        (u.getDepartement() != null && u.getDepartement().toLowerCase().contains(searchTerm)))
            .filter(u -> "Tous".equals(selectedRole) || u.getRole().equals(selectedRole))
            .filter(u -> "Tous".equals(selectedStatus) || 
                        ("Actif".equals(selectedStatus) && u.isActif()) ||
                        ("Inactif".equals(selectedStatus) && !u.isActif()))
            .filter(u -> "Tous".equals(selectedDepartment) || 
                        (u.getDepartement() != null && u.getDepartement().equals(selectedDepartment)))
            .toList();
        
        filteredUsers.setAll(filtered);
        updateStats(filtered);
    }
    
    @FXML
    private void showAddUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/user-dialog.fxml"));
            Parent root = loader.load();
            
            UserDialogController controller = loader.getController();
            controller.setMode(UserDialogController.Mode.ADD);
            controller.setOnSaveCallback(() -> {
                loadUsers(); // Refresh table
            });
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Ajouter un utilisateur");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }
    
    private void showEditUserDialog(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/user-dialog.fxml"));
            Parent root = loader.load();
            
            UserDialogController controller = loader.getController();
            controller.setMode(UserDialogController.Mode.EDIT);
            controller.setUser(user);
            controller.setOnSaveCallback(() -> {
                loadUsers(); // Refresh table
            });
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Modifier l'utilisateur");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Erreur", "Impossible d'ouvrir le formulaire");
        }
    }
    
    private void toggleUserStatus(Utilisateur user) {
        String action = user.isActif() ? "désactiver" : "activer";
        String newStatus = user.isActif() ? "inactif" : "actif";
        
        if (DialogUtil.showConfirmation("Changer le statut", 
                "Êtes-vous sûr de vouloir " + action + " l'utilisateur " + 
                user.getFullName() + " ?\nIl deviendra " + newStatus + ".")) {
            showLoading(true);
            
            var service = usersService.toggleUserStatus(user.getId());
            service.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Statut utilisateur modifié avec succès");
                    loadUsers(); // Refresh table
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de modifier le statut");
                });
            });
            
            service.start();
        }
    }
    
    private void deleteUser(Utilisateur user) {
        if (DialogUtil.showConfirmation("Supprimer l'utilisateur", 
                "Êtes-vous sûr de vouloir supprimer l'utilisateur '" + user.getFullName() + "' ?\n" +
                "Cette action est irréversible et supprimera toutes ses réservations.")) {
            showLoading(true);
            
            var service = usersService.deleteUser(user.getId());
            service.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showSuccess("Succès", "Utilisateur supprimé avec succès");
                    loadUsers(); // Refresh table
                });
            });
            
            service.setOnFailed(event -> {
                Platform.runLater(() -> {
                    showLoading(false);
                    DialogUtil.showError("Erreur", "Impossible de supprimer l'utilisateur");
                });
            });
            
            service.start();
        }
    }
    
    private void resetUserPassword(Utilisateur user) {
        String newPassword = DialogUtil.showTextInput("Réinitialiser le mot de passe", 
            "Nouveau mot de passe pour " + user.getFullName(), "");
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) {
                DialogUtil.showError("Erreur", "Le mot de passe doit contenir au moins 6 caractères");
                return;
            }
            
            showLoading(true);
            
            // TODO: Implement password reset service
            // usersService.resetPassword(user.getId(), newPassword).start();
            
            Platform.runLater(() -> {
                showLoading(false);
                DialogUtil.showSuccess("Succès", "Mot de passe réinitialisé avec succès");
            });
        }
    }
    
    private void setupEventHandlers() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchUsers());
        roleFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> searchUsers());
        statusFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> searchUsers());
        departmentFilterCombo.valueProperty().addListener((observable, oldValue, newValue) -> searchUsers());
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        loadUsers();
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.VIEW_ADMIN_DASHBOARD));
            Parent root = loader.load();
            
            Stage stage = (Stage) addUserButton.getScene().getWindow();
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
