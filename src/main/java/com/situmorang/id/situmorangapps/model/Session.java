package com.situmorang.id.situmorangapps.model;

public class Session {
    private static Pengguna loggedUser;

    public static void setLoggedUser(Pengguna user) {
        loggedUser = user;
    }

    public static Pengguna getLoggedUser() {
        return loggedUser;
    }

    public static void clear() {
        loggedUser = null;
    }
}
