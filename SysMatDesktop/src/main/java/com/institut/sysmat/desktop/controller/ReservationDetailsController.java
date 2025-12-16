package com.institut.sysmat.desktop.controller;

import com.institut.sysmat.desktop.model.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReservationDetailsController {

    @FXML private Label idLabel;
    @FXML private Label userLabel;
    @FXML private Label materialLabel;
    @FXML private Label quantityLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label statusLabel;
    @FXML private Label reservationDateLabel;
    @FXML private TextArea motifArea;

    public void setReservation(Reservation reservation) {
        if (reservation != null) {
            idLabel.setText(String.valueOf(reservation.getId()));
            userLabel.setText(reservation.getUtilisateurFullName());
            materialLabel.setText(reservation.getMaterielNom());
            quantityLabel.setText(String.valueOf(reservation.getQuantite()));
            startDateLabel.setText(reservation.getFormattedDateDebut());
            endDateLabel.setText(reservation.getFormattedDateFin());
            statusLabel.setText(reservation.getStatusText());
            statusLabel.setStyle("-fx-text-fill: " + reservation.getStatusColor() + "; -fx-font-weight: bold;");
            reservationDateLabel.setText(reservation.getFormattedDateReservation());
            motifArea.setText(reservation.getMotifUtilisation());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }
}
