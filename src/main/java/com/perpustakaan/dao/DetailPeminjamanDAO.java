package com.perpustakaan.dao;

import com.perpustakaan.model.DetailPeminjaman;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DetailPeminjamanDAO {
    private Connection connection;
    
    public DetailPeminjamanDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public void addDetailPeminjaman(List<DetailPeminjaman> detailList) {
        String sql = "INSERT INTO detail_peminjaman (id_peminjaman, id_buku) VALUES (?, ?)";
        String updateStokSql = "UPDATE buku SET stok = stok - 1 WHERE id_buku = ?";
        
        try {
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 PreparedStatement updateStokStmt = connection.prepareStatement(updateStokSql)) {
                
                for (DetailPeminjaman detail : detailList) {
                    // Insert detail peminjaman
                    stmt.setInt(1, detail.getIdPeminjaman());
                    stmt.setInt(2, detail.getIdBuku());
                    stmt.addBatch();
                    
                    // Update stok buku
                    updateStokStmt.setInt(1, detail.getIdBuku());
                    updateStokStmt.addBatch();
                }
                
                stmt.executeBatch();
                updateStokStmt.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Error adding detail peminjaman", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateStokBuku(int idBuku, int jumlah) {
        String sql = "UPDATE buku SET stok = stok + ? WHERE id_buku = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jumlah);
            stmt.setInt(2, idBuku);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 