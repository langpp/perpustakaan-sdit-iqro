package com.sdit.iqro.perpustakaan.dao;

import com.sdit.iqro.perpustakaan.config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sdit.iqro.perpustakaan.model.User;

public class UserDAO {
    private final Connection koneksi;
    
    public UserDAO() {
        koneksi = Koneksi.getConnection();
    }
    
    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT u.id, u.username, u.password, p.nama, p.nip " +
                    "FROM users u " +
                    "LEFT JOIN petugas p ON u.id = p.user_id " +
                    "WHERE u.username = ?";
        
        try {
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                if (password.equals("password")) { 
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setNama(rs.getString("nama"));
                    user.setNip(rs.getString("nip"));
                }
            }
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error pada login: " + e.getMessage());
        }
        
        return user;
    }
}