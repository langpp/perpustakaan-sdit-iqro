package com.perpustakaan.dao;

import com.perpustakaan.db.DatabaseConnection;
import com.perpustakaan.model.Anggota;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.model.Pengembalian;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PengembalianDAO {
    private Connection connection;
    
    public PengembalianDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public void addPengembalian(Pengembalian pengembalian) {
        try {
            System.out.println("Starting addPengembalian process...");
            connection.setAutoCommit(false);
            
            // Insert pengembalian
            String sql = "INSERT INTO pengembalian (id_pinjam, tanggal_dikembalikan, denda) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, pengembalian.getIdPeminjaman());
            statement.setDate(2, new java.sql.Date(pengembalian.getTglKembali().getTime()));
            statement.setInt(3, pengembalian.getDenda());
            
            System.out.println("Executing insert pengembalian...");
            int result = statement.executeUpdate();
            System.out.println("Insert result: " + result);
            
            // Update stok buku
            sql = "SELECT id_buku FROM detail_peminjaman WHERE id_pinjam = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, pengembalian.getIdPeminjaman());
            ResultSet rs = statement.executeQuery();
            
            System.out.println("Updating book stock...");
            while (rs.next()) {
                int idBuku = rs.getInt("id_buku");
                System.out.println("Updating stock for book ID: " + idBuku);
                updateStokBuku(idBuku, 1);
            }
            
            // Update status peminjaman
            sql = "UPDATE peminjaman SET status = 'Dikembalikan' WHERE id_pinjam = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, pengembalian.getIdPeminjaman());
            
            System.out.println("Updating peminjaman status...");
            result = statement.executeUpdate();
            System.out.println("Update result: " + result);
            
            connection.commit();
            System.out.println("Transaction committed successfully");
        } catch (SQLException e) {
            System.out.println("Error in addPengembalian:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            try {
                System.out.println("Rolling back transaction...");
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back:");
                ex.printStackTrace();
            }
            throw new RuntimeException("Gagal menyimpan pengembalian: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void updatePengembalian(Pengembalian pengembalian) {
        try {
            String sql = "UPDATE pengembalian SET id_peminjaman=?, id_petugas=?, tgl_kembali=?, denda=? WHERE id_pengembalian=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, pengembalian.getIdPeminjaman());
            statement.setInt(2, pengembalian.getIdPetugas());
            statement.setDate(3, new java.sql.Date(pengembalian.getTglKembali().getTime()));
            statement.setInt(4, pengembalian.getDenda());
            statement.setInt(5, pengembalian.getIdPengembalian());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletePengembalian(int idPengembalian) {
        try {
            connection.setAutoCommit(false);
            
            // Get peminjaman id
            String sql = "SELECT id_peminjaman FROM pengembalian WHERE id_pengembalian=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPengembalian);
            ResultSet rs = statement.executeQuery();
            
            int idPeminjaman = 0;
            if (rs.next()) {
                idPeminjaman = rs.getInt("id_peminjaman");
            }
            
            // Update status peminjaman
            sql = "UPDATE peminjaman SET status = 'Dipinjam' WHERE id_peminjaman = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
            statement.executeUpdate();
            
            // Update stok buku
            sql = "SELECT id_buku FROM detail_peminjaman WHERE id_peminjaman = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
            rs = statement.executeQuery();
            
            while (rs.next()) {
                updateStokBuku(rs.getInt("id_buku"), -1);
            }
            
            // Delete pengembalian
            sql = "DELETE FROM pengembalian WHERE id_pengembalian=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPengembalian);
            statement.executeUpdate();
            
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Pengembalian> getAllPengembalian() {
        List<Pengembalian> pengembalianList = new ArrayList<>();
        try {
            System.out.println("Getting all pengembalian records...");
            
            String sql = "SELECT p.id_kembali, p.id_pinjam, p.tanggal_dikembalikan, p.denda, " +
                    "pm.tanggal_pinjam, a.nama as nama_anggota " +
                    "FROM pengembalian p " +
                    "JOIN peminjaman pm ON p.id_pinjam = pm.id_pinjam " +
                    "JOIN anggota a ON pm.id_anggota = a.id_anggota " +
                    "ORDER BY p.id_kembali DESC";
            
            System.out.println("Executing SQL: " + sql);
            
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Pengembalian pengembalian = new Pengembalian();
                pengembalian.setIdPengembalian(resultSet.getInt("id_kembali"));
                pengembalian.setIdPeminjaman(resultSet.getInt("id_pinjam"));
                pengembalian.setTglKembali(resultSet.getDate("tanggal_dikembalikan"));
                pengembalian.setDenda(resultSet.getInt("denda"));
                
                // Set peminjaman data
                Peminjaman peminjaman = new Peminjaman();
                peminjaman.setIdPeminjaman(resultSet.getInt("id_pinjam"));
                peminjaman.setTanggalPinjam(resultSet.getDate("tanggal_pinjam"));
                
                // Set anggota data
                Anggota anggota = new Anggota();
                anggota.setNama(resultSet.getString("nama_anggota"));
                peminjaman.setAnggota(anggota);
                
                pengembalian.setPeminjaman(peminjaman);
                pengembalianList.add(pengembalian);
                
                System.out.println("Found pengembalian: " + pengembalian.getIdPengembalian());
            }
            
            System.out.println("Total pengembalian found: " + pengembalianList.size());
        } catch (SQLException e) {
            System.out.println("Error in getAllPengembalian:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return pengembalianList;
    }
    
    public Pengembalian getPengembalianById(int idPengembalian) {
        Pengembalian pengembalian = null;
        try {
            String sql = "SELECT p.id_kembali, p.id_pinjam, p.tanggal_dikembalikan, p.denda, " +
                    "pm.tanggal_pinjam, a.nama as nama_anggota " +
                    "FROM pengembalian p " +
                    "JOIN peminjaman pm ON p.id_pinjam = pm.id_pinjam " +
                    "JOIN anggota a ON pm.id_anggota = a.id_anggota " +
                    "WHERE p.id_kembali = ?";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPengembalian);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                pengembalian = new Pengembalian();
                pengembalian.setIdPengembalian(resultSet.getInt("id_kembali"));
                pengembalian.setIdPeminjaman(resultSet.getInt("id_pinjam"));
                pengembalian.setTglKembali(resultSet.getDate("tanggal_dikembalikan"));
                pengembalian.setDenda(resultSet.getInt("denda"));
                
                // Set peminjaman data
                Peminjaman peminjaman = new Peminjaman();
                peminjaman.setIdPeminjaman(resultSet.getInt("id_pinjam"));
                peminjaman.setTanggalPinjam(resultSet.getDate("tanggal_pinjam"));
                
                // Set anggota data
                Anggota anggota = new Anggota();
                anggota.setNama(resultSet.getString("nama_anggota"));
                peminjaman.setAnggota(anggota);
                
                pengembalian.setPeminjaman(peminjaman);
            }
        } catch (SQLException e) {
            System.out.println("Error in getPengembalianById:");
            e.printStackTrace();
        }
        return pengembalian;
    }
    
    public List<Peminjaman> getPeminjamanAktif() {
        List<Peminjaman> peminjamanList = new ArrayList<>();
        try {
            String sql = "SELECT p.id_pinjam, p.tanggal_kembali, a.nama as nama_anggota " +
                    "FROM peminjaman p " +
                    "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                    "WHERE p.status = 'Dipinjam' " +
                    "ORDER BY p.id_pinjam DESC";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Peminjaman peminjaman = new Peminjaman();
                peminjaman.setIdPeminjaman(resultSet.getInt("id_pinjam"));
                peminjaman.setTanggalKembali(resultSet.getDate("tanggal_kembali"));
                
                Anggota anggota = new Anggota();
                anggota.setNama(resultSet.getString("nama_anggota"));
                peminjaman.setAnggota(anggota);
                
                peminjamanList.add(peminjaman);
            }
        } catch (SQLException e) {
            System.out.println("Error in getPeminjamanAktif:");
            e.printStackTrace();
        }
        return peminjamanList;
    }
    
    private void updateStokBuku(int idBuku, int jumlah) {
        try {
            System.out.println("Updating stock for book ID: " + idBuku + " by " + jumlah);
            
            // Get current stock first
            String sql = "SELECT stok FROM buku WHERE id_buku = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idBuku);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                int currentStok = rs.getInt("stok");
                System.out.println("Current stock: " + currentStok);
                
                // Update stock
                sql = "UPDATE buku SET stok = ? WHERE id_buku = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, currentStok + jumlah);
                statement.setInt(2, idBuku);
                
                int result = statement.executeUpdate();
                System.out.println("Stock update result: " + result);
            } else {
                System.out.println("Book not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book stock:");
            e.printStackTrace();
            throw new RuntimeException("Gagal mengupdate stok buku: " + e.getMessage());
        }
    }
} 