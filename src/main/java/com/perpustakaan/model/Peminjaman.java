package com.perpustakaan.model;

import java.util.Date;
import java.util.List;

public class Peminjaman {
    private int idPeminjaman;
    private int idAnggota;
    private int idPetugas;
    private Date tanggalPinjam;
    private Date tanggalKembali;
    private String status;
    private Anggota anggota;
    private Petugas petugas;
    private List<DetailPeminjaman> detailPeminjaman;

    public Peminjaman() {
    }

    public Peminjaman(int idPeminjaman, int idAnggota, int idPetugas, Date tanggalPinjam, 
            Date tanggalKembali, String status) {
        this.idPeminjaman = idPeminjaman;
        this.idAnggota = idAnggota;
        this.idPetugas = idPetugas;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
    }

    // Getters and Setters
    public int getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(int idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public int getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(int idAnggota) {
        this.idAnggota = idAnggota;
    }

    public int getIdPetugas() {
        return idPetugas;
    }

    public void setIdPetugas(int idPetugas) {
        this.idPetugas = idPetugas;
    }

    public Date getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(Date tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(Date tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public void setAnggota(Anggota anggota) {
        this.anggota = anggota;
    }

    public Petugas getPetugas() {
        return petugas;
    }

    public void setPetugas(Petugas petugas) {
        this.petugas = petugas;
    }

    public List<DetailPeminjaman> getDetailPeminjaman() {
        return detailPeminjaman;
    }

    public void setDetailPeminjaman(List<DetailPeminjaman> detailPeminjaman) {
        this.detailPeminjaman = detailPeminjaman;
    }
} 