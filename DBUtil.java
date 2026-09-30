package com.hall;
import java.sql.*;

public class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/hallbooking";
    static final String USER = "root";
    static final String PASS = "your_password";   // change this

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
