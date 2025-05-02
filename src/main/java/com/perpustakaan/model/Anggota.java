package com.perpustakaan.model;

import java.util.Date;

public class Anggota {
    private int idAnggota;
    private String nis;
    private String nama;
    private String kelas;
    private String telepon;
    private Date tanggalDaftar;

    public Anggota() {
    }

    public Anggota(int idAnggota, String nis, String nama, String kelas, String telepon, Date tanggalDaftar) {
        this.idAnggota = idAnggota;
        this.nis = nis;
        this.nama = nama;
        this.kelas = kelas;
        this.telepon = telepon;
        this.tanggalDaftar = tanggalDaftar;
    }

    // Getters and Setters
    public int getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(int idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public Date getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void setTanggalDaftar(Date tanggalDaftar) {
        this.tanggalDaftar = tanggalDaftar;
    }

    @Override
    public String toString() {
        return nama;
    }
} 