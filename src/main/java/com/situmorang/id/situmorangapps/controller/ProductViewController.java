package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Product;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class ProductViewController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Number> colNumber; // tambahkan ini
    @FXML private TextField searchField;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Double> colPriceIn;
    @FXML private TableColumn<Product, String> colSourceStoreName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colUnit;
    @FXML private TableColumn<Product, String> colBarcode;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField priceInField;
    @FXML private TextField sourceStoreNameField;
    @FXML private TextField stockField;
    @FXML private TextField unitField;
    @FXML private TextField barcodeField;

    private Product selectedProduct = null;

    @FXML
    public void initialize() {
        ProductController.initializeDatabase();

        colNumber.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(productTable.getItems().indexOf(cellData.getValue()) + 1));
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colPriceIn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPriceIn()).asObject());
        colSourceStoreName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSourceStoreName()));
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colUnit.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUnit()));
        colBarcode.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBarcode()));

        productTable.setItems(ProductController.getAllProducts());

        productTable.setOnMouseClicked((MouseEvent event) -> {
            Product clickedProduct = productTable.getSelectionModel().getSelectedItem();

            // Jika klik ulang pada produk yang sudah terseleksi → batalkan seleksi
            if (clickedProduct != null && clickedProduct.equals(selectedProduct)) {
                productTable.getSelectionModel().clearSelection(); // hilangkan seleksi
                clearFields(); // kosongkan form
                selectedProduct = null;
            }
            // Jika pilih produk berbeda → tampilkan ke form
            else if (clickedProduct != null) {
                selectedProduct = clickedProduct;
                nameField.setText(selectedProduct.getName());
                priceField.setText(String.valueOf(selectedProduct.getPrice()));
                priceInField.setText(String.valueOf(selectedProduct.getPriceIn()));
                sourceStoreNameField.setText(selectedProduct.getSourceStoreName());
                stockField.setText(String.valueOf(selectedProduct.getStock()));
                unitField.setText(selectedProduct.getUnit());
                barcodeField.setText(selectedProduct.getBarcode());
            }
        });
    }

    @FXML
    public void onAddProduct() {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            double priceIn = Double.parseDouble(priceInField.getText());
            String sourceStoreName = sourceStoreNameField.getText();
            int stock = Integer.parseInt(stockField.getText());
            String unit = unitField.getText();
            String barcode = barcodeField.getText();

            String barcodeCheck = barcodeField.getText().trim();
            if (ProductController.isBarcodeExists(barcodeCheck)) {
                showAlert("Perhatian!","Barcode sudah digunakan produk lain!");
                return;
            }

            ProductController.addProduct(name, price,priceIn,sourceStoreName, stock, unit, barcode);
            refreshTable();
            clearFields();
        } catch (Exception e) {
            showAlert("Input Error", "Pastikan semua data terisi dengan benar.");
        }
    }

    @FXML
    public void onUpdateProduct() {
        if (selectedProduct != null) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                double priceIn = Double.parseDouble(priceInField.getText());
                String sourceStoreName = sourceStoreNameField.getText();
                int stock = Integer.parseInt(stockField.getText());
                String unit = unitField.getText();
                String barcode = barcodeField.getText();

                ProductController.updateProduct(selectedProduct.getId(), name, price,priceIn,sourceStoreName, stock,unit,barcode);
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Input Error", "Pastikan semua data terisi dengan benar.");
            }
        }
    }

    @FXML
    public void onDeleteProduct() {
        if (selectedProduct != null) {
            ProductController.deleteProduct(selectedProduct.getId());
            refreshTable();
            clearFields();
        }
    }

    private void refreshTable() {
        ObservableList<Product> products = ProductController.getAllProducts();
        productTable.setItems(products);
        colNumber.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(productTable.getItems().indexOf(cellData.getValue()) + 1));
    }


    private void clearFields() {
        nameField.clear();
        priceField.clear();
        priceInField.clear();
        sourceStoreNameField.clear();
        stockField.clear();
        unitField.clear();
        barcodeField.clear();
        selectedProduct = null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onBackToMain() {
        try {
            com.situmorang.id.situmorangapps.app.Main.setRoot("/view/main_view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSearchProduct() {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<Product> filtered = ProductController.getAllProducts().filtered(p ->
                (p.getName() != null && p.getName().toLowerCase().contains(keyword)) ||
                        (p.getSourceStoreName() != null && p.getSourceStoreName().toLowerCase().contains(keyword)) ||
                        (p.getBarcode() != null && p.getBarcode().toLowerCase().contains(keyword))
        );
        productTable.setItems(filtered);
    }

    @FXML
    public void onResetSearch() {
        searchField.clear();
        refreshTable();
    }

}
