package com.institut.sysmat.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HeaderController {

    @FXML private Button backButton;
    @FXML private Button refreshButton;

    @FXML
    private void handleBack() {
        // Navigate back - this would need to be implemented based on navigation history
        System.out.println("Back button clicked");
    }

    @FXML
    private void handleRefresh() {
        // Refresh current page - this would need to trigger reload of current view
        System.out.println("Refresh button clicked");
    }
}
