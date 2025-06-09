package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Product;
import com.situmorang.id.situmorangapps.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class ProductController {

    // Method untuk membuat tabel di database PostgreSQL
    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                price NUMERIC(10, 2) NOT NULL,
                price_in NUMERIC(10, 2) NOT NULL,
                source_store_name VARCHAR(255) NOT NULL,
                stock INTEGER NOT NULL,
                unit VARCHAR(255) NOT NULL,
                barcode VARCHAR(50)
            )
        """;

        try (Connection conn = DBUtil.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mengambil semua produk dari database
    public static ObservableList<Product> getAllProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products order by name asc";

        try (Connection conn = DBUtil.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("price_in"),
                        rs.getString("source_store_name"),
                        rs.getInt("stock"),
                        rs.getString("unit"),
                        rs.getString("barcode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Product findByName(String name) {
        String sql = "SELECT * FROM products WHERE LOWER(name) = LOWER(?) LIMIT 1";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("price_in"),
                        rs.getString("source_store_name"),
                        rs.getInt("stock"),
                        rs.getString("unit"),
                        rs.getString("barcode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Product findByBarcode(String barcode) {
        String sql = "SELECT * FROM products WHERE barcode = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("price_in"),
                        rs.getString("source_store_name"),
                        rs.getInt("stock"),
                        rs.getString("unit"),
                        rs.getString("barcode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method untuk menambahkan produk baru ke database
    public static void addProduct(String name, double price, double priceIn, String sourceStoreName, int stock,String unit,String barcode) {
        String sql = "INSERT INTO products (name, price, price_in, source_store_name, stock, unit, barcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setDouble(3, priceIn);
            pstmt.setString(4, sourceStoreName);
            pstmt.setInt(5, stock);
            pstmt.setString(6, unit);
            pstmt.setString(7, barcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mengupdate produk yang sudah ada di database
    public static void updateProduct(int id, String name, double price,double priceIn, String sourceStoreName, int stock, String unit, String barcode) {
        String sql = "UPDATE products SET name = ?, price = ?,price_in = ?,source_store_name = ?, stock = ?, unit = ?, barcode = ? WHERE id = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setDouble(3, priceIn);
            pstmt.setString(4, sourceStoreName);
            pstmt.setInt(5, stock);
            pstmt.setString(6, unit);
            pstmt.setString(7, barcode);
            pstmt.setInt(8, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isProductUsed(int id) {
        String sql = "SELECT 1 FROM sales_items WHERE product_id = ? LIMIT 1";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // true = masih digunakan
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // anggap dipakai kalau error
        }
    }

    // Method untuk menghapus produk dari database berdasarkan ID
    public static void deleteProduct(int id) {
        if (isProductUsed(id)) {
            System.out.println("Produk Sudah Terdaftar dalam History Penjualan. Tidak bisa dihapus.");
            showAlert("Produk Sudah Terdaftar dalam History Penjualan. Tidak bisa dihapus.");
            return;
        }

        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void reduceStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }


    public static boolean isBarcodeExists(String barcode) {
        String sql = "SELECT COUNT(*) FROM products WHERE barcode = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
