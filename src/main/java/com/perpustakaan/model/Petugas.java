package com.perpustakaan.model;

public class Petugas {
    private int idPetugas;
    private String username;
    private String password;
    private String nama;
    
    public Petugas() {}
    
    public Petugas(int idPetugas, String username, String password, String nama) {
        this.idPetugas = idPetugas;
        this.username = username;
        this.password = password;
        this.nama = nama;
    }
    
    public int getIdPetugas() {
        return idPetugas;
    }
    
    public void setIdPetugas(int idPetugas) {
        this.idPetugas = idPetugas;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
} 