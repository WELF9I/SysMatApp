package com.institut.sysmat.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MainApp extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/institut/sysmat/desktop/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Rendre la fenêtre draggable
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });
            
            // Créer la scène avec style moderne
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/com/institut/sysmat/desktop/css/styles.css")).toExternalForm());
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("SysMat - Gestion de Matériels Pédagogiques");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED); // Pour un look moderne
            primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/com/institut/sysmat/desktop/images/logo.png"))));
            
            // Centrer la fenêtre
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            logger.info("Application SysMat démarrée avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage de l'application", e);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}