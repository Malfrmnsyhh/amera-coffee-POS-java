CREATE DATABASE IF NOT EXISTS amera_coffee;
USE amera_coffee;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nama_lengkap VARCHAR(100) NOT NULL,
  role ENUM('admin', 'karyawan') NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, nama_lengkap, role) VALUES
("admin", "12345", "Muhammad Akmal", "admin"),
("kasir1", "12345", "Dimas Dzaky", "karyawan"),
("kasir2", "12345", "Eka Ahmad", "karyawan");

CREATE TABLE member (
  id INT PRIMARY KEY AUTO_INCREMENT,
  kode_member VARCHAR(100) NOT NULL UNIQUE,
  nama VARCHAR(100) NOT NULL,
  no_hp VARCHAR(100)
);

CREATE TABLE produk (
  id INT PRIMARY KEY AUTO_INCREMENT,
  kode_produk VARCHAR(100) NOT NULL UNIQUE,
  nama_produk VARCHAR(100) NOT NULL,
  harga INT NOT NULL,
  stok INT DEFAULT 0
);

CREATE TABLE transaksi (
  id INT PRIMARY KEY AUTO_INCREMENT,
  no_transaksi VARCHAR(100) NOT NULL UNIQUE,
  kasir_id INT,
  nama_kasir VARCHAR(100),
  member_id INT DEFAULT NULL,
  nama_customer VARCHAR(100),
  nomor_meja INT,
  tanggal DATE,
  total INT,
  bayar INT,
  kembalian INT,
  FOREIGN KEY (kasir_id) REFERENCES users(id),
  FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE detail_transaksi (
  id INT PRIMARY KEY AUTO_INCREMENT,
  transaksi_id INT,
  produk_id INT,
  nama_produk VARCHAR(100),
  harga_satuan INT,
  jumlah INT,
  subtotal INT,
  Foreign Key (transaksi_id) REFERENCES transaksi(id),
  Foreign Key (produk_id) REFERENCES produk(id)
);

-- Data contoh untuk testing kasir
INSERT INTO produk (kode_produk, nama_produk, harga, stok) VALUES
('PR001', 'Americano', 25000, 50),
('PR002', 'Cappuccino', 28000, 40),
('PR003', 'Latte', 30000, 35),
('PR004', 'Espresso', 20000, 60);

INSERT INTO member (kode_member, nama, no_hp) VALUES
('MB001', 'Nuril Abel', '081234567890'),
('MB002', 'Nuril Fatoni', '081298765432');
