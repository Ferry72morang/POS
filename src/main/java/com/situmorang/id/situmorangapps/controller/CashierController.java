package com.situmorang.id.situmorangapps.controller;

import com.situmorang.id.situmorangapps.model.Product;
import com.situmorang.id.situmorangapps.model.Sales;
import com.situmorang.id.situmorangapps.model.SalesItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Optional;

public class CashierController {

    @FXML private TextField productNameField;
    @FXML private TextField quantityField;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private Label totalLabel;
    @FXML private Label stockLabel;
    @FXML private TextField barcodeField;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    private Product selectedProduct;

    @FXML
    public void initialize() {
        // Aksi saat enter di field nama produk
        productNameField.setOnAction(event -> {
            String name = productNameField.getText().trim();
            if (!name.isEmpty()) {
                Product found = ProductController.findByName(name);
                if (found != null) {
                    selectedProduct = found;
                    stockLabel.setText("Stok: " + found.getStock());
                    quantityField.requestFocus();
                } else {
                    selectedProduct = null;
                    stockLabel.setText("Stok: -");
                    showAlert("Produk Tidak Ditemukan", "Nama produk tidak ditemukan.");
                }
            }
        });

        // Aksi saat enter di field barcode
        barcodeField.setOnAction(event -> {
            String barcode = barcodeField.getText().trim();
            if (!barcode.isEmpty()) {
                Product found = ProductController.findByBarcode(barcode);
                if (found != null) {
                    selectedProduct = found;
                    productNameField.setText(found.getName());
                    stockLabel.setText("Stok: " + found.getStock());
                    quantityField.requestFocus();
                } else {
                    selectedProduct = null;
                    productNameField.clear();
                    stockLabel.setText("Stok: -");
                    showAlert("Produk Tidak Ditemukan", "Tidak ada produk dengan barcode tersebut.");
                }
            }
        });

        // Inisialisasi kolom-kolom tabel keranjang
        cartTable.setItems(cartItems);

        colProductName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getProduct().getName()));

        colQuantity.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());

        colTotal.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotal()).asObject());
    }




    @FXML
    public void onAddToCart() {
        Product selected = selectedProduct;
        String qtyText = quantityField.getText();

        if (selected == null) {
            showAlert("Peringatan", "Silakan pilih produk terlebih dahulu.");
            return;
        }

        if (qtyText == null || qtyText.trim().isEmpty()) {
            showAlert("Peringatan", "Silakan masukkan jumlah.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                showAlert("Peringatan", "Jumlah harus lebih dari 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Peringatan", "Jumlah harus berupa angka.");
            return;
        }

        // Hitung total qty produk ini di keranjang
        int totalQtyInCart = 0;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == selected.getId()) {
                totalQtyInCart += item.getQuantity();
            }
        }

        if (totalQtyInCart + qty > selected.getStock()) {
            showAlert("Peringatan", "Stok tidak mencukupi. Sisa stok: " + (selected.getStock() - totalQtyInCart));
            return;
        }

        // Tambahkan qty ke item yang sudah ada di cart, atau buat baru
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == selected.getId()) {
                item.setQuantity(item.getQuantity() + qty);
                cartTable.refresh(); // <---- Tambahkan ini agar TableView diperbarui
                found = true;
                break;
            }
        }

        if (!found) {
            cartItems.add(new CartItem(selected, qty));
        }

        updateTotal();
        quantityField.clear();
    }




    public void onCheckout() {
        if (cartItems.isEmpty()) {
            showAlert("Peringatan", "Keranjang masih kosong. Tambahkan produk terlebih dahulu.");
            return;
        }

        // Validasi stok
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            if (product.getStock() < quantity) {
                showAlert("Stok Tidak Cukup", "Stok untuk " + product.getName() + " tidak mencukupi.");
                return;
            }
        }

        // Simpan transaksi ke tabel sales
        int saleId = SalesController.insertSale();
        if (saleId == -1) {
            showAlert("Gagal", "Gagal menyimpan transaksi.");
            return;
        }

        // Simpan item penjualan & update stok
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            // Simpan ke sales_items
            SalesItem saleItem = new SalesItem(0, saleId, product, quantity, product.getPrice());
            SalesController.insertSaleItem(saleId, saleItem);

            // Kurangi stok
            ProductController.reduceStock(product.getId(), quantity);
        }

        // Tampilkan sukses
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaksi Berhasil");
        alert.setHeaderText("Pembayaran Berhasil");
        alert.setContentText(
                "Transaksi berhasil disimpan!\n\n" +
                        "ID Transaksi : " + saleId + "\n" +
                        "Jumlah Item  : " + cartItems.size() + "\n" +
                        "Total        : Rp " + String.format("%,.0f", cartItems.stream().mapToDouble(CartItem::getTotal).sum()) + "\n\n" +
                        "Silakan cetak struk jika diperlukan."
        );
        alert.showAndWait();

        // Bersihkan keranjang & reset UI input
        cartItems.clear();
        updateTotal();
        productNameField.clear();
        barcodeField.clear();
        quantityField.clear();
        stockLabel.setText("Stok: -");
        selectedProduct = null;

        printReceipt(saleId);
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        totalLabel.setText("Total: Rp " + total);
    }

    public static class CartItem {
        private final Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return product.getPrice() * quantity;
        }
    }


    private void printReceipt(int saleId) {
        Sales sale = SalesController.getSaleById(saleId);
        if (sale == null) {
            showAlert("Error", "Data transaksi tidak ditemukan.");
            return;
        }

        List<SalesItem> items = SalesController.getItemsBySaleId(saleId);
        sale.setItems(items);

        StringBuilder receipt = new StringBuilder();
        receipt.append("=========== SITUMORANG MART ===========\n");
        receipt.append("ID Transaksi : ").append(sale.getId()).append("\n");
        receipt.append("Tanggal      : ").append(sale.getDate()).append("\n");
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-15s %5s %10s %12s\n", "Produk", "Qty", "Harga", "Subtotal"));
        receipt.append("----------------------------------------\n");

        double total = 0.0;
        for (SalesItem item : items) {
            String name = item.getProduct().getName();
            int qty = item.getQuantity();
            double price = item.getPrice();
            double subtotal = qty * price;
            total += subtotal;

            receipt.append(String.format("%-15s %5d %10s %12s\n",
                    name.length() > 15 ? name.substring(0, 15) : name,
                    qty,
                    String.format("%,.0f", price),
                    String.format("%,.0f", subtotal)));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-32s %12s\n", "TOTAL:", "Rp " + String.format("%,.0f", total)));
        receipt.append("========================================\n");
        receipt.append("Terima kasih telah berbelanja!\n");

        // Konfirmasi kepada user apakah ingin mencetak struk
        Alert receiptDialog = new Alert(Alert.AlertType.CONFIRMATION);
        receiptDialog.setTitle("Cetak Struk");
        receiptDialog.setHeaderText("Apakah Anda ingin mencetak struk ini?");
        receiptDialog.setContentText("Pilih YES untuk mencetak langsung ke printer.");

        ButtonType yesButton = new ButtonType("YES", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("NO", ButtonBar.ButtonData.NO);
        receiptDialog.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = receiptDialog.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Cetak langsung ke printer default
            TextFlow printContent = new TextFlow(new Text(receipt.toString()));
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean success = job.printPage(printContent);
                if (success) {
                    job.endJob();
                } else {
                    showAlert("Print Gagal", "Struk gagal dicetak.");
                }
            } else {
                showAlert("Printer Tidak Tersedia", "Tidak dapat membuat job printer.");
            }
        }
    }




    @FXML
    public void onRemoveItem() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Peringatan", "Pilih item yang ingin dihapus dari keranjang.");
            return;
        }

        cartItems.remove(selectedItem);
        updateTotal();
    }

    @FXML
    public void onSearchProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Cari Produk");

        Label searchLabel = new Label("Nama / Barcode:");
        TextField searchField = new TextField();

        TableView<Product> table = new TableView<>();
        TableColumn<Product, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Product, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBarcode()));
        barcodeCol.setPrefWidth(150);

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()).asObject());
        stockCol.setPrefWidth(80);

        table.getColumns().addAll(nameCol, barcodeCol, stockCol);
        ObservableList<Product> allProducts = ProductController.getAllProducts();
        table.setItems(allProducts);

        searchField.textProperty().addListener((obs, old, val) -> {
            String keyword = val.toLowerCase();
            table.setItems(allProducts.filtered(p -> {
                String name = p.getName();
                String barcode = p.getBarcode();
                return (name != null && name.toLowerCase().contains(keyword)) ||
                        (barcode != null && barcode.toLowerCase().contains(keyword));
            }));
        });



        VBox content = new VBox(10, new HBox(10, searchLabel, searchField), table);
        content.setPrefSize(500, 400);
        dialog.getDialogPane().setContent(content);

        ButtonType pilihBtn = new ButtonType("Pilih", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(pilihBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == pilihBtn) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selected -> {
            selectedProduct = selected;
            productNameField.setText(selected.getName());
            stockLabel.setText("Stok: " + selected.getStock());
            quantityField.requestFocus();
        });
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
