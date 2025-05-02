package com.perpustakaan.dao;

import com.perpustakaan.model.Kategori;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {
    private Connection connection;
    
    public KategoriDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public boolean addKategori(Kategori kategori) {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kategori.getNamaKategori());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateKategori(Kategori kategori) {
        String sql = "UPDATE kategori SET nama_kategori = ? WHERE id_kategori = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kategori.getNamaKategori());
            stmt.setInt(2, kategori.getIdKategori());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteKategori(int idKategori) {
        String sql = "DELETE FROM kategori WHERE id_kategori = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idKategori);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Kategori> getAllKategori() {
        List<Kategori> kategoriList = new ArrayList<>();
        String sql = "SELECT * FROM kategori";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Kategori kategori = new Kategori();
                kategori.setIdKategori(rs.getInt("id_kategori"));
                kategori.setNamaKategori(rs.getString("nama_kategori"));
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriList;
    }
    
    public Kategori getKategoriById(int idKategori) {
        String sql = "SELECT * FROM kategori WHERE id_kategori = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idKategori);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Kategori kategori = new Kategori();
                    kategori.setIdKategori(rs.getInt("id_kategori"));
                    kategori.setNamaKategori(rs.getString("nama_kategori"));
                    return kategori;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 