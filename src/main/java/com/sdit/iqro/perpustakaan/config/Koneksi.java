package com.sdit.iqro.perpustakaan.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection koneksi;
    
    public static Connection getConnection() {
        if (koneksi == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                String url = "jdbc:mysql://localhost:3306/perpustakaan?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                String user = "root";
                String password = "";
                
                koneksi = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi berhasil");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan:\n" + e.getMessage());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error koneksi database:\n" + e.getMessage());
            }
        }
        return koneksi;
    }
    
    public static void closeConnection() {
        if (koneksi != null) {
            try {
                koneksi.close();
                koneksi = null;
                System.out.println("Koneksi ditutup");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error menutup koneksi:\n" + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Koneksi ke database perpustakaan berhasil!");
            closeConnection();
        } else {
            System.out.println("Koneksi ke database perpustakaan gagal!");
        }
    }
}