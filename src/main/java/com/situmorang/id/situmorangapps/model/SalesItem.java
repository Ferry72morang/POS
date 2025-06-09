package com.situmorang.id.situmorangapps.model;

public class SalesItem {
    private int id;
    private int saleId;
    private Product product;
    private int quantity;
    private double price;

    public SalesItem() {}

    public SalesItem(int id, int saleId, Product product, int quantity, double price) {
        this.id = id;
        this.saleId = saleId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
