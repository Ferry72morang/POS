package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.app.Main;
import com.situmorang.id.situmorangapps.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    @FXML
    private Button productButton;

    @FXML
    private Button historyButton;

    @FXML
    public void initialize() {
        if (!"ADMIN".equalsIgnoreCase(Session.getLoggedUser().getRole())) {
            // Sembunyikan tombol untuk non-admin
            productButton.setVisible(false);
            historyButton.setVisible(false);
        }
    }

    @FXML
    private void openProductView() {
        try {
            Main.setRoot("/view/product_view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCashierView() {
        try {
            Main.setRoot("/view/cashier_view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openHistoryView() {
        try {
            Main.setRoot("/view/history_view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
