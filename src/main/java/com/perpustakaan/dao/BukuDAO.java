package com.perpustakaan.dao;

import com.perpustakaan.db.DatabaseConnection;
import com.perpustakaan.model.Buku;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BukuDAO {
    private Connection connection;
    
    public BukuDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public List<Buku> getAllBuku() {
        List<Buku> bukuList = new ArrayList<>();
        String sql = "SELECT * FROM buku";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Buku buku = new Buku();
                buku.setIdBuku(rs.getInt("id_buku"));
                buku.setJudulBuku(rs.getString("judul_buku"));
                buku.setPengarang(rs.getString("pengarang"));
                buku.setPenerbit(rs.getString("penerbit"));
                buku.setTahunTerbit(rs.getInt("tahun_terbit"));
                buku.setIdKategori(rs.getInt("id_kategori"));
                buku.setStok(rs.getInt("stok"));
                bukuList.add(buku);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bukuList;
    }
    
    public Buku getBukuById(int idBuku) {
        String sql = "SELECT * FROM buku WHERE id_buku = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idBuku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Buku buku = new Buku();
                    buku.setIdBuku(rs.getInt("id_buku"));
                    buku.setJudulBuku(rs.getString("judul_buku"));
                    buku.setPengarang(rs.getString("pengarang"));
                    buku.setPenerbit(rs.getString("penerbit"));
                    buku.setTahunTerbit(rs.getInt("tahun_terbit"));
                    buku.setIdKategori(rs.getInt("id_kategori"));
                    buku.setStok(rs.getInt("stok"));
                    return buku;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addBuku(Buku buku) {
        String sql = "INSERT INTO buku (judul_buku, pengarang, penerbit, tahun_terbit, id_kategori, stok) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, buku.getJudulBuku());
            stmt.setString(2, buku.getPengarang());
            stmt.setString(3, buku.getPenerbit());
            stmt.setInt(4, buku.getTahunTerbit());
            stmt.setInt(5, buku.getIdKategori());
            stmt.setInt(6, buku.getStok());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateBuku(Buku buku) {
        String sql = "UPDATE buku SET judul_buku = ?, pengarang = ?, penerbit = ?, tahun_terbit = ?, id_kategori = ?, stok = ? WHERE id_buku = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, buku.getJudulBuku());
            stmt.setString(2, buku.getPengarang());
            stmt.setString(3, buku.getPenerbit());
            stmt.setInt(4, buku.getTahunTerbit());
            stmt.setInt(5, buku.getIdKategori());
            stmt.setInt(6, buku.getStok());
            stmt.setInt(7, buku.getIdBuku());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteBuku(int idBuku) {
        String sql = "DELETE FROM buku WHERE id_buku = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idBuku);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateStok(int idBuku, int jumlah) {
        try {
            String sql = "UPDATE buku SET stok = stok + ? WHERE id_buku = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, jumlah);
            statement.setInt(2, idBuku);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 