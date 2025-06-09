package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Pengguna;
import com.situmorang.id.situmorangapps.model.Session;
import com.situmorang.id.situmorangapps.util.DBUtil;
import com.situmorang.id.situmorangapps.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Pengguna user = this.findUserByUsername(username); // implementasikan ini

        String hashedInput = PasswordUtil.hashPassword(password);

        if (user != null && user.getPassword().equals(hashedInput)) {
            Session.setLoggedUser(user); // simpan user
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Halaman Utama");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Username atau password salah.");
            alert.showAndWait();
        }
    }

    public static Pengguna findUserByUsername(String username) {
        String sql = "SELECT * FROM pengguna WHERE username = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Pengguna user = new Pengguna();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
