package com.perpustakaan.dao;

import com.perpustakaan.db.DatabaseConnection;
import com.perpustakaan.model.Anggota;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnggotaDAO {
    private Connection connection;
    
    public AnggotaDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public List<Anggota> getAllAnggota() {
        List<Anggota> anggotaList = new ArrayList<>();
        String sql = "SELECT * FROM anggota";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Anggota anggota = new Anggota();
                anggota.setIdAnggota(rs.getInt("id_anggota"));
                anggota.setNis(rs.getString("nis"));
                anggota.setNama(rs.getString("nama"));
                anggota.setKelas(rs.getString("kelas"));
                anggota.setTelepon(rs.getString("telepon"));
                anggota.setTanggalDaftar(rs.getDate("tanggal_daftar"));
                anggotaList.add(anggota);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anggotaList;
    }
    
    public Anggota getAnggotaById(int idAnggota) {
        String sql = "SELECT * FROM anggota WHERE id_anggota = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idAnggota);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Anggota anggota = new Anggota();
                    anggota.setIdAnggota(rs.getInt("id_anggota"));
                    anggota.setNis(rs.getString("nis"));
                    anggota.setNama(rs.getString("nama"));
                    anggota.setKelas(rs.getString("kelas"));
                    anggota.setTelepon(rs.getString("telepon"));
                    anggota.setTanggalDaftar(rs.getDate("tanggal_daftar"));
                    return anggota;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addAnggota(Anggota anggota) {
        String sql = "INSERT INTO anggota (nis, nama, kelas, telepon, tanggal_daftar) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, anggota.getNis());
            stmt.setString(2, anggota.getNama());
            stmt.setString(3, anggota.getKelas());
            stmt.setString(4, anggota.getTelepon());
            stmt.setDate(5, new java.sql.Date(anggota.getTanggalDaftar().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateAnggota(Anggota anggota) {
        String sql = "UPDATE anggota SET nis = ?, nama = ?, kelas = ?, telepon = ?, tanggal_daftar = ? WHERE id_anggota = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, anggota.getNis());
            stmt.setString(2, anggota.getNama());
            stmt.setString(3, anggota.getKelas());
            stmt.setString(4, anggota.getTelepon());
            stmt.setDate(5, new java.sql.Date(anggota.getTanggalDaftar().getTime()));
            stmt.setInt(6, anggota.getIdAnggota());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAnggota(int idAnggota) {
        String sql = "DELETE FROM anggota WHERE id_anggota = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idAnggota);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Anggota getAnggotaByNis(String nis) {
        Anggota anggota = null;
        try {
            String sql = "SELECT * FROM anggota WHERE nis=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nis);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                anggota = new Anggota();
                anggota.setIdAnggota(resultSet.getInt("id_anggota"));
                anggota.setNis(resultSet.getString("nis"));
                anggota.setNama(resultSet.getString("nama"));
                anggota.setKelas(resultSet.getString("kelas"));
                anggota.setTelepon(resultSet.getString("telepon"));
                anggota.setTanggalDaftar(resultSet.getDate("tanggal_daftar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return anggota;
    }
} 