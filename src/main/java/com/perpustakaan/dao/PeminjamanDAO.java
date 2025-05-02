package com.perpustakaan.dao;

import com.perpustakaan.db.DatabaseConnection;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.model.DetailPeminjaman;
import com.perpustakaan.model.Anggota;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class PeminjamanDAO {
    private Connection connection;
    
    public PeminjamanDAO() {
        connection = DatabaseConnection.getConnection();
    }
    
    public int addPeminjaman(Peminjaman peminjaman) {
        String sql = "INSERT INTO peminjaman (id_anggota, id_petugas, tanggal_pinjam, tanggal_kembali, status) VALUES (?, ?, ?, ?, ?)";
        try {
            System.out.println("=== Starting addPeminjaman ===");
            System.out.println("Parameters:");
            System.out.println("- id_anggota: " + peminjaman.getIdAnggota());
            System.out.println("- id_petugas: " + peminjaman.getIdPetugas());
            System.out.println("- tanggal_pinjam: " + peminjaman.getTanggalPinjam());
            System.out.println("- tanggal_kembali: " + peminjaman.getTanggalKembali());
            System.out.println("- status: " + peminjaman.getStatus());
            
            // Validasi parameter
            if (peminjaman.getIdAnggota() <= 0) {
                System.out.println("Error: ID Anggota tidak valid");
                throw new RuntimeException("ID Anggota tidak valid");
            }
            if (peminjaman.getIdPetugas() <= 0) {
                System.out.println("Error: ID Petugas tidak valid");
                throw new RuntimeException("ID Petugas tidak valid");
            }
            if (peminjaman.getTanggalPinjam() == null) {
                System.out.println("Error: Tanggal pinjam tidak valid");
                throw new RuntimeException("Tanggal pinjam tidak valid");
            }
            if (peminjaman.getTanggalKembali() == null) {
                System.out.println("Error: Tanggal kembali tidak valid");
                throw new RuntimeException("Tanggal kembali tidak valid");
            }
            if (peminjaman.getStatus() == null || peminjaman.getStatus().isEmpty()) {
                System.out.println("Error: Status tidak valid");
                throw new RuntimeException("Status tidak valid");
            }
            
            System.out.println("Creating prepared statement...");
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            System.out.println("Setting parameters...");
            stmt.setInt(1, peminjaman.getIdAnggota());
            stmt.setInt(2, peminjaman.getIdPetugas());
            stmt.setDate(3, new java.sql.Date(peminjaman.getTanggalPinjam().getTime()));
            stmt.setDate(4, new java.sql.Date(peminjaman.getTanggalKembali().getTime()));
            stmt.setString(5, peminjaman.getStatus());
            
            System.out.println("Executing SQL: " + sql);
            int affectedRows = stmt.executeUpdate();
            System.out.println("ExecuteUpdate completed, affected rows: " + affectedRows);
            
            if (affectedRows == 0) {
                System.out.println("Error: No rows affected in peminjaman insert");
                return -1;
            }

            System.out.println("Getting generated keys...");
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Success: Generated ID: " + id);
                    return id;
                } else {
                    System.out.println("Error: No generated keys found");
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.out.println("=== SQLException in addPeminjaman ===");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Stack Trace:");
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            System.out.println("=== Exception in addPeminjaman ===");
            System.out.println("Message: " + e.getMessage());
            System.out.println("Stack Trace:");
            e.printStackTrace();
            return -1;
        } finally {
            System.out.println("=== End of addPeminjaman ===");
        }
    }
    
    public void updatePeminjaman(Peminjaman peminjaman) {
        try {
            String sql = "UPDATE peminjaman SET id_anggota=?, id_petugas=?, tanggal_pinjam=?, tanggal_kembali=?, status=? WHERE id_peminjaman=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, peminjaman.getIdAnggota());
            statement.setInt(2, peminjaman.getIdPetugas());
            statement.setDate(3, new java.sql.Date(peminjaman.getTanggalPinjam().getTime()));
            statement.setDate(4, new java.sql.Date(peminjaman.getTanggalKembali().getTime()));
            statement.setString(5, peminjaman.getStatus());
            statement.setInt(6, peminjaman.getIdPeminjaman());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletePeminjaman(int idPeminjaman) {
        try {
            connection.setAutoCommit(false);
            
            // Get detail peminjaman
            String sql = "SELECT id_buku FROM detail_peminjaman WHERE id_peminjaman=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
            ResultSet rs = statement.executeQuery();
            
            // Update stok buku
            while (rs.next()) {
                updateStokBuku(rs.getInt("id_buku"), 1);
            }
            
            // Delete detail peminjaman
            sql = "DELETE FROM detail_peminjaman WHERE id_peminjaman=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
            statement.executeUpdate();
            
            // Delete peminjaman
            sql = "DELETE FROM peminjaman WHERE id_peminjaman=?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
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
    
    public List<Peminjaman> getAllPeminjaman() {
        List<Peminjaman> peminjamanList = new ArrayList<>();
        String sql = "SELECT * FROM peminjaman ORDER BY id_pinjam DESC";
        
        try {
            System.out.println("=== Getting all peminjaman ===");
            System.out.println("Executing SQL: " + sql);
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Peminjaman peminjaman = new Peminjaman();
                    peminjaman.setIdPeminjaman(rs.getInt("id_pinjam"));
                    peminjaman.setIdAnggota(rs.getInt("id_anggota"));
                    peminjaman.setIdPetugas(rs.getInt("id_petugas"));
                    peminjaman.setTanggalPinjam(rs.getDate("tanggal_pinjam"));
                    peminjaman.setTanggalKembali(rs.getDate("tanggal_kembali"));
                    peminjaman.setStatus(rs.getString("status"));
                    
                    System.out.println("Found peminjaman: " + peminjaman.getIdPeminjaman());
                    peminjamanList.add(peminjaman);
                }
            }
            
            System.out.println("Total peminjaman found: " + peminjamanList.size());
            return peminjamanList;
        } catch (SQLException e) {
            System.out.println("Error in getAllPeminjaman:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return peminjamanList;
        }
    }
    
    public Peminjaman getPeminjamanById(int idPeminjaman) {
        String sql = "SELECT p.*, a.nama as nama_anggota " +
                    "FROM peminjaman p " +
                    "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                    "WHERE p.id_pinjam = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPeminjaman);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Peminjaman peminjaman = new Peminjaman();
                    peminjaman.setIdPeminjaman(rs.getInt("id_pinjam"));
                    peminjaman.setIdAnggota(rs.getInt("id_anggota"));
                    peminjaman.setIdPetugas(rs.getInt("id_petugas"));
                    peminjaman.setTanggalPinjam(rs.getDate("tanggal_pinjam"));
                    peminjaman.setTanggalKembali(rs.getDate("tanggal_kembali"));
                    peminjaman.setStatus(rs.getString("status"));
                    
                    // Set nama anggota
                    Anggota anggota = new Anggota();
                    anggota.setNama(rs.getString("nama_anggota"));
                    peminjaman.setAnggota(anggota);
                    
                    return peminjaman;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getPeminjamanById:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<DetailPeminjaman> getDetailPeminjaman(int idPeminjaman) {
        List<DetailPeminjaman> detailList = new ArrayList<>();
        try {
            System.out.println("Getting detail peminjaman for id: " + idPeminjaman);
            
            String sql = "SELECT d.*, b.judul_buku, b.pengarang, b.penerbit, b.tahun_terbit " +
                    "FROM detail_peminjaman d " +
                    "JOIN buku b ON d.id_buku = b.id_buku " +
                    "WHERE d.id_pinjam = ?";
            
            System.out.println("Executing SQL: " + sql);
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idPeminjaman);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                DetailPeminjaman detail = new DetailPeminjaman();
                detail.setIdDetail(resultSet.getInt("id_detail"));
                detail.setIdPinjam(resultSet.getInt("id_pinjam"));
                detail.setIdBuku(resultSet.getInt("id_buku"));
                detail.setJudulBuku(resultSet.getString("judul_buku"));
                detail.setPengarang(resultSet.getString("pengarang"));
                detail.setPenerbit(resultSet.getString("penerbit"));
                detail.setTahunTerbit(resultSet.getInt("tahun_terbit"));
                
                System.out.println("Found book: " + detail.getJudulBuku());
                detailList.add(detail);
            }
            
            System.out.println("Total books found: " + detailList.size());
            
        } catch (SQLException e) {
            System.out.println("Error in getDetailPeminjaman:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return detailList;
    }
    
    public void updateStatusPeminjaman(int idPeminjaman, String status) {
        String sql = "UPDATE peminjaman SET status = ? WHERE id_peminjaman = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, idPeminjaman);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateStokBuku(int idBuku, int jumlah) {
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

    public void addDetailPeminjaman(List<DetailPeminjaman> detailList) {
        String sql = "INSERT INTO detail_peminjaman (id_pinjam, id_buku) VALUES (?, ?)";
        String updateStokSql = "UPDATE buku SET stok = stok - 1 WHERE id_buku = ?";
        
        try {
            System.out.println("=== Starting addDetailPeminjaman ===");
            System.out.println("Number of details to add: " + detailList.size());
            
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 PreparedStatement updateStokStmt = connection.prepareStatement(updateStokSql)) {
                
                for (DetailPeminjaman detail : detailList) {
                    System.out.println("Processing detail: id_pinjam=" + detail.getIdPinjam() + 
                                     ", id_buku=" + detail.getIdBuku());
                    
                    // Cek stok buku terlebih dahulu
                    String checkStokSql = "SELECT stok FROM buku WHERE id_buku = ?";
                    try (PreparedStatement checkStokStmt = connection.prepareStatement(checkStokSql)) {
                        checkStokStmt.setInt(1, detail.getIdBuku());
                        ResultSet rs = checkStokStmt.executeQuery();
                        if (rs.next()) {
                            int stok = rs.getInt("stok");
                            System.out.println("Current stok for buku " + detail.getIdBuku() + ": " + stok);
                            if (stok <= 0) {
                                throw new RuntimeException("Stok buku habis untuk buku ID: " + detail.getIdBuku());
                            }
                        } else {
                            throw new RuntimeException("Buku tidak ditemukan dengan ID: " + detail.getIdBuku());
                        }
                    }
                    
                    // Insert detail peminjaman
                    stmt.setInt(1, detail.getIdPinjam());
                    stmt.setInt(2, detail.getIdBuku());
                    int result = stmt.executeUpdate();
                    System.out.println("Insert detail result: " + result);
                    
                    // Update stok buku
                    updateStokStmt.setInt(1, detail.getIdBuku());
                    int updateResult = updateStokStmt.executeUpdate();
                    System.out.println("Update stok result: " + updateResult);
                    
                    // Clear parameters for next iteration
                    stmt.clearParameters();
                    updateStokStmt.clearParameters();
                }
                
                connection.commit();
                System.out.println("Transaction committed successfully");
            } catch (SQLException e) {
                System.out.println("SQLException in batch execution: " + e.getMessage());
                System.out.println("SQL State: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("SQLException in addDetailPeminjaman: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Error adding detail peminjaman: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteDetailPeminjaman(int idPinjam, int idBuku) {
        String sql = "DELETE FROM detail_peminjaman WHERE id_pinjam = ? AND id_buku = ?";
        String updateStokSql = "UPDATE buku SET stok = stok + 1 WHERE id_buku = ?";
        
        try {
            System.out.println("Starting deleteDetailPeminjaman...");
            System.out.println("Deleting detail for id_pinjam=" + idPinjam + ", id_buku=" + idBuku);
            
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 PreparedStatement updateStokStmt = connection.prepareStatement(updateStokSql)) {
                
                stmt.setInt(1, idPinjam);
                stmt.setInt(2, idBuku);
                updateStokStmt.setInt(1, idBuku);
                
                int deletedRows = stmt.executeUpdate();
                System.out.println("Deleted " + deletedRows + " rows from detail_peminjaman");
                
                updateStokStmt.executeUpdate();
                System.out.println("Updated buku stok");
                
                connection.commit();
                System.out.println("Transaction committed successfully");
            } catch (SQLException e) {
                System.out.println("SQLException in deleteDetailPeminjaman: " + e.getMessage());
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("SQLException in deleteDetailPeminjaman: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Error deleting detail peminjaman", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Object[]> getTop5BorrowedBooks() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
} 