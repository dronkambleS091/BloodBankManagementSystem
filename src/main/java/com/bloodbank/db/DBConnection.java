
package com.bloodbank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/blood_bank_db";
    private static final String USER = "root";

    // Replace this with your actual MySQL root password
    private static final String PASSWORD = "Drkamble23@";

   
public static Connection getConnection() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("MySQL JDBC driver was not found.", e);
    }

    return DriverManager.getConnection(URL, USER, PASSWORD);
}
}

