package com.perpustakaan.model;

import java.util.Date;

public class Pengembalian {
    private int idPengembalian;
    private int idPeminjaman;
    private int idPetugas;
    private Date tglKembali;
    private int denda;
    private Peminjaman peminjaman;
    private Petugas petugas;

    public Pengembalian() {
    }

    public Pengembalian(int idPengembalian, int idPeminjaman, int idPetugas, 
            Date tglKembali, int denda) {
        this.idPengembalian = idPengembalian;
        this.idPeminjaman = idPeminjaman;
        this.idPetugas = idPetugas;
        this.tglKembali = tglKembali;
        this.denda = denda;
    }

    // Getters and Setters
    public int getIdPengembalian() {
        return idPengembalian;
    }

    public void setIdPengembalian(int idPengembalian) {
        this.idPengembalian = idPengembalian;
    }

    public int getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(int idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public int getIdPetugas() {
        return idPetugas;
    }

    public void setIdPetugas(int idPetugas) {
        this.idPetugas = idPetugas;
    }

    public Date getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(Date tglKembali) {
        this.tglKembali = tglKembali;
    }

    public int getDenda() {
        return denda;
    }

    public void setDenda(int denda) {
        this.denda = denda;
    }

    public Peminjaman getPeminjaman() {
        return peminjaman;
    }

    public void setPeminjaman(Peminjaman peminjaman) {
        this.peminjaman = peminjaman;
    }

    public Petugas getPetugas() {
        return petugas;
    }

    public void setPetugas(Petugas petugas) {
        this.petugas = petugas;
    }
} 