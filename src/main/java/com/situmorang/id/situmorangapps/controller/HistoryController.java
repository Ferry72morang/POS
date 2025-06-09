package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Sales;
import com.situmorang.id.situmorangapps.model.SalesItem;
import com.situmorang.id.situmorangapps.util.PdfReportUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;

public class HistoryController {
    @FXML private TableView<Sales> salesTable;
    @FXML private TableColumn<Sales, Number> colId;
    @FXML private TableColumn<Sales, String> colDate;

    @FXML private TableView<SalesItem> itemsTable;
    @FXML private TableColumn<SalesItem, String> colProduct;
    @FXML private TableColumn<SalesItem, Integer> colQty;
    @FXML private TableColumn<SalesItem, Double> colPrice;
    @FXML private Label totalLabel;

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML
    public void initialize() {
        ObservableList<Sales> salesList = SalesController.getAllSales();
        salesTable.setItems(salesList);

        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate().toString()));

        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        colQty.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        // Format kolom harga menjadi format Rupiah
        NumberFormat rupiahFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        colPrice.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SalesItem, Double> call(TableColumn<SalesItem, Double> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty || price == null) {
                            setText(null);
                        } else {
                            setText("Rp " + rupiahFormat.format(price));
                        }
                    }
                };
            }
        });

        // Saat klik baris transaksi, tampilkan detailnya
        salesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                ObservableList<SalesItem> items = SalesController.getItemsBySaleId(newSel.getId());
                itemsTable.setItems(items);

                double total = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
                totalLabel.setText("Rp " + rupiahFormat.format(total));
            } else {
                totalLabel.setText("");
            }
        });
    }

    @FXML
    private void onFilterClicked() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        if (from != null && to != null && !to.isBefore(from)) {
            ObservableList<Sales> filteredSales = SalesController.getSalesByFilter(from, to);
            salesTable.setItems(filteredSales);
            itemsTable.getItems().clear();
            totalLabel.setText("");
        } else {
            showAlert("Tanggal Tidak Valid", "Pastikan tanggal awal dan akhir dipilih dengan benar.");
        }
    }

    @FXML
    private void onAllDataClicked() {
        salesTable.setItems(SalesController.getAllSales());
        itemsTable.getItems().clear();
        totalLabel.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onPrintReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            List<Sales> salesList = salesTable.getItems(); // <== gunakan data yang ditampilkan di tabel (sudah terfilter)

            // Pastikan setiap Sales memiliki items (jika belum dimuat sebelumnya)
            for (Sales sale : salesList) {
                sale.setItems(SalesController.getItemsBySaleId(sale.getId())); // isi items
            }

            PdfReportUtil.generateSalesReport(salesList, file.getAbsolutePath());
            showAlert("Berhasil", "Laporan berhasil disimpan: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void onBackToMain() {
        try {
            com.situmorang.id.situmorangapps.app.Main.setRoot("/view/main_view.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
