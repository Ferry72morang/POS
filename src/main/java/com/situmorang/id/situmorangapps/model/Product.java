package com.situmorang.id.situmorangapps.model;

public class Product {
        private int id;
        private String name;
        private double price;
        private double priceIn;
        private String sourceStoreName;
        private int stock;
        private String unit;
        private String barcode;

        public Product(int id, String name, double price,double priceIn,String sourceStoreName, int stock, String unit,String barcode) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.priceIn = priceIn;
            this.sourceStoreName = sourceStoreName;
            this.stock = stock;
            this.unit = unit;
            this.barcode = barcode;
        }

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public double getPriceIn() { return priceIn; }
        public void setPriceIn(double priceIn) { this.priceIn = priceIn; }

        public String getSourceStoreName() { return sourceStoreName; }
        public void setSourceStoreName(String sourceStoreName) { this.sourceStoreName = sourceStoreName; }

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }


        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }

        @Override
        public String toString() {
            return name; // atau return getName(); tergantung strukturmu
        }
}
