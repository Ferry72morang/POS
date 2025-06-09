package com.situmorang.id.situmorangapps.model;

public class Pengguna {
    private int id;
    private String username;
    private String password;
    private String role;

    // Getter dan Setter untuk ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter dan Setter untuk Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter dan Setter untuk Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter dan Setter untuk Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
