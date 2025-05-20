package com.expense.database;

import java.sql.*;

public class DBUtil {
    private static Connection con;

    public static Connection getConnection() {
        try {
            String db_userId = "root";           
            String db_userPass = "admin";          
            String db_URL = "jdbc:mysql://localhost:3306/expense_tracker";

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(db_URL, db_userId, db_userPass);

        } catch (ClassNotFoundException | SQLException cse) {
            cse.printStackTrace();
        }
        return con;
    }

    public static void closeConnection() {
        try {
            if (con != null)
                con.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
