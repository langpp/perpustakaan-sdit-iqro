package com.sdit.iqro.perpustakaan.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String nama;
    private String nip;
    
    public User() {
    }
    
    public User(int id, String username, String password, String nama, String nip) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.nip = nip;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getNip() {
        return nip;
    }
    
    public void setNip(String nip) {
        this.nip = nip;
    }
}