package com.situmorang.id.situmorangapps.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://127.0.0.1:5432/postgres?currentSchema=test";
    private static final String USER = "postgres";
    private static final String PASSWORD = "P@ssw0rd";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
