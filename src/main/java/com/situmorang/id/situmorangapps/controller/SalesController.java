package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Product;
import com.situmorang.id.situmorangapps.model.Sales;
import com.situmorang.id.situmorangapps.model.SalesItem;
import com.situmorang.id.situmorangapps.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SalesController {
    public static int insertSale() {
        String sql = "INSERT INTO sales DEFAULT VALUES RETURNING id";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public static void insertSaleItem(int saleId, SalesItem item) {
        String sql = "INSERT INTO sales_items (sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            pstmt.setInt(2, item.getProduct().getId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPrice());
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static ObservableList<Sales> getAllSales() {
        ObservableList<Sales> salesList = FXCollections.observableArrayList();
        String sql = "SELECT id, date FROM sales ORDER BY id DESC";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDateTime createdAt = rs.getTimestamp("date").toLocalDateTime();
                salesList.add(new Sales(id, createdAt));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return salesList;
    }

    public static List<Sales> getAllSalesWithItems() {
        List<Sales> salesList = getAllSales();
        for (Sales sale : salesList) {
            sale.setItems(getItemsBySaleId(sale.getId()));
        }
        return salesList;
    }

    public static ObservableList<SalesItem> getItemsBySaleId(int saleId) {
        ObservableList<SalesItem> list = FXCollections.observableArrayList();
        String sql = "SELECT si.quantity, si.price,p.price_in, p.id, p.name FROM sales_items si JOIN products p ON si.product_id = p.id WHERE si.sale_id = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"),rs.getDouble("price_in"),"", 0, "","");
                SalesItem item = new SalesItem(saleId, saleId, p, rs.getInt("quantity"), rs.getDouble("price"));
                list.add(item);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static Sales getSaleById(int saleId) {
        String sql = "SELECT id, date FROM sales WHERE id = ?";
        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                LocalDateTime createdAt = rs.getTimestamp("date").toLocalDateTime();
                return new Sales(id, createdAt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Sales> getSalesByFilter(LocalDate from, LocalDate to) {
        ObservableList<Sales> filtered = FXCollections.observableArrayList();
        String sql = "SELECT id, date FROM sales WHERE date::date BETWEEN ? AND ? ORDER BY id DESC";

        try (Connection conn = DBUtil.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.sql.Date.valueOf(from));
            pstmt.setObject(2, java.sql.Date.valueOf(to));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDateTime createdAt = rs.getTimestamp("date").toLocalDateTime();
                Sales sale = new Sales(id, createdAt);
                sale.setItems(getItemsBySaleId(id));
                filtered.add(sale);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filtered;
    }


}
