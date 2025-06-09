package com.situmorang.id.situmorangapps.model;

import java.time.LocalDateTime;
import java.util.List;

public class Sales {
    private int id;
    private LocalDateTime date;
    private List<SalesItem> items;

    public Sales(int id, LocalDateTime date) {
        this.id = id;
        this.date = date;
    }

    public int getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public List<SalesItem> getItems() { return items; }
    public void setItems(List<SalesItem> items) { this.items = items; }
}
