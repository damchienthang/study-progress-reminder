package com.nhom14;

import java.sql.*;

public class DbCheck {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:study_reminder.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connection successful.");
            
            System.out.println("\n--- TABLES ---");
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
            
            System.out.println("\n--- USERS DATA ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs2 = st.executeQuery("SELECT * FROM users")) {
                ResultSetMetaData md = rs2.getMetaData();
                int cols = md.getColumnCount();
                for (int i = 1; i <= cols; i++) System.out.print(md.getColumnName(i) + " | ");
                System.out.println();
                while (rs2.next()) {
                    for (int i = 1; i <= cols; i++) System.out.print(rs2.getString(i) + " | ");
                    System.out.println();
                }
            }
            
            System.out.println("\n--- ROLES DATA ---");
            try (Statement st = conn.createStatement();
                 ResultSet rs3 = st.executeQuery("SELECT * FROM roles")) {
                while (rs3.next()) {
                    System.out.println(rs3.getInt("roleId") + " - " + rs3.getString("roleName"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
