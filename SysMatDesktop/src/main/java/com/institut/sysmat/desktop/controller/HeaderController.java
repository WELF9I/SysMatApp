package com.institut.sysmat.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HeaderController {

    @FXML private Button backButton;

    @FXML
    private void handleBack() {
        // Navigate back - this would need to be implemented based on navigation history
        System.out.println("Back button clicked");
    }
}
