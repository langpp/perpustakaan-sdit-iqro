-- Create database
CREATE DATABASE IF NOT EXISTS perpustakaan;
USE perpustakaan;

-- Create tables
CREATE TABLE IF NOT EXISTS kategori (
    id_kategori INT PRIMARY KEY AUTO_INCREMENT,
    nama_kategori VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS buku (
    id_buku INT PRIMARY KEY AUTO_INCREMENT,
    judul_buku VARCHAR(100) NOT NULL,
    pengarang VARCHAR(50) NOT NULL,
    penerbit VARCHAR(50) NOT NULL,
    tahun_terbit INT NOT NULL,
    id_kategori INT NOT NULL,
    stok INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori)
);

CREATE TABLE IF NOT EXISTS anggota (
    id_anggota INT PRIMARY KEY AUTO_INCREMENT,
    nis VARCHAR(20) NOT NULL UNIQUE,
    nama VARCHAR(50) NOT NULL,
    kelas VARCHAR(20) NOT NULL,
    telepon VARCHAR(15) NOT NULL,
    tanggal_daftar DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS petugas (
    id_petugas INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'petugas') NOT NULL
);

CREATE TABLE IF NOT EXISTS peminjaman (
    id_pinjam INT PRIMARY KEY AUTO_INCREMENT,
    id_anggota INT NOT NULL,
    id_petugas INT NOT NULL,
    tanggal_pinjam DATE NOT NULL,
    tanggal_kembali DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota),
    FOREIGN KEY (id_petugas) REFERENCES petugas(id_petugas)
);

CREATE TABLE IF NOT EXISTS detail_peminjaman (
    id_detail INT PRIMARY KEY AUTO_INCREMENT,
    id_pinjam INT NOT NULL,
    id_buku INT NOT NULL,
    FOREIGN KEY (id_pinjam) REFERENCES peminjaman(id_pinjam),
    FOREIGN KEY (id_buku) REFERENCES buku(id_buku)
);

CREATE TABLE IF NOT EXISTS pengembalian (
    id_kembali INT PRIMARY KEY AUTO_INCREMENT,
    id_pinjam INT NOT NULL,
    tanggal_dikembalikan DATE NOT NULL,
    denda DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (id_pinjam) REFERENCES peminjaman(id_pinjam)
);

-- Insert initial data
INSERT INTO petugas (nama, username, password, role) VALUES 
('Administrator', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin');

INSERT INTO kategori (nama_kategori) VALUES 
('Novel'),
('Pelajaran'),
('Referensi'),
('Kamus'),
('Majalah');

-- Insert sample books
INSERT INTO buku (judul_buku, pengarang, penerbit, tahun_terbit, id_kategori, stok) VALUES
('Laskar Pelangi', 'Andrea Hirata', 'Bentang Pustaka', 2005, 1, 10),
('Bumi Manusia', 'Pramoedya Ananta Toer', 'Hasta Mitra', 1980, 1, 8),
('Matematika Dasar', 'Budi Raharjo', 'Erlangga', 2020, 2, 15),
('Kamus Inggris-Indonesia', 'John Echols', 'Gramedia', 2019, 4, 5),
('National Geographic', 'National Geographic', 'National Geographic', 2023, 5, 20); 