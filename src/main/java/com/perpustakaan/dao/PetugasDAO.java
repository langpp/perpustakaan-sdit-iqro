package com.perpustakaan.dao;

import com.perpustakaan.model.Petugas;
import com.perpustakaan.util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetugasDAO {
    private Connection connection;
    
    public PetugasDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public boolean authenticate(String username, String password) {
        try {
            // Get user by username
            Petugas petugas = getPetugasByUsername(username);
            if (petugas == null) {
                System.out.println("User not found: " + username);
                return false;
            }
            
            // Hash the provided password
            String hashedPassword = PasswordHasher.hashPassword(password);
            System.out.println("Provided password hash: " + hashedPassword);
            System.out.println("Stored password hash: " + petugas.getPassword());
            
            // Compare hashed passwords
            boolean isAuthenticated = hashedPassword.equals(petugas.getPassword());
            System.out.println("Authentication result: " + isAuthenticated);
            
            return isAuthenticated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addPetugas(Petugas petugas) {
        String sql = "INSERT INTO petugas (username, password, nama, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, petugas.getUsername());
            stmt.setString(2, petugas.getPassword());
            stmt.setString(3, petugas.getNama());
            stmt.setString(4, "petugas"); // Default role
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePetugas(Petugas petugas) {
        String sql = "UPDATE petugas SET username = ?, password = ?, nama = ? WHERE id_petugas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, petugas.getUsername());
            stmt.setString(2, petugas.getPassword());
            stmt.setString(3, petugas.getNama());
            stmt.setInt(4, petugas.getIdPetugas());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePetugas(int idPetugas) {
        String sql = "DELETE FROM petugas WHERE id_petugas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPetugas);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Petugas> getAllPetugas() {
        List<Petugas> petugasList = new ArrayList<>();
        String sql = "SELECT * FROM petugas";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Petugas petugas = new Petugas();
                petugas.setIdPetugas(rs.getInt("id_petugas"));
                petugas.setUsername(rs.getString("username"));
                petugas.setPassword(rs.getString("password"));
                petugas.setNama(rs.getString("nama"));
                petugasList.add(petugas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return petugasList;
    }
    
    public Petugas getPetugasById(int idPetugas) {
        String sql = "SELECT * FROM petugas WHERE id_petugas = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPetugas);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Petugas petugas = new Petugas();
                    petugas.setIdPetugas(rs.getInt("id_petugas"));
                    petugas.setUsername(rs.getString("username"));
                    petugas.setPassword(rs.getString("password"));
                    petugas.setNama(rs.getString("nama"));
                    return petugas;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Petugas getPetugasByUsername(String username) {
        String sql = "SELECT * FROM petugas WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Petugas petugas = new Petugas();
                    petugas.setIdPetugas(rs.getInt("id_petugas"));
                    petugas.setUsername(rs.getString("username"));
                    petugas.setPassword(rs.getString("password"));
                    petugas.setNama(rs.getString("nama"));
                    return petugas;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 