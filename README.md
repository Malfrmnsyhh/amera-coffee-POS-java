# Amera Coffee - Point of Sale (POS) System ☕

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans&logoColor=white)

Amera Coffee POS adalah aplikasi kasir berbasis Desktop (GUI) yang dikembangkan menggunakan **Java Swing** dan **MySQL**. Aplikasi ini dibangun sebagai bentuk pemenuhan Tugas Akhir Mata Kuliah **Pemrograman Berorientasi Objek (PBO / OOP)** pada Semester 4.

Proyek ini dirancang dengan mengedepankan prinsip-prinsip OOP seperti *Encapsulation*, *Inheritance*, dan *Polymorphism*, serta mengadaptasi pola arsitektur **Data Access Object (DAO)** dan pemisahan logika Model-View-Controller (MVC) yang bersih.

## 🌟 Fitur Utama

Aplikasi ini memiliki sistem *Role-Based Access Control (RBAC)* yang membedakan hak akses antara **Admin** dan **Karyawan (Kasir)**.

### 👨‍💼 Dashboard Admin (Manajemen & Analisis)
- **Ringkasan Bisnis (Dashboard):** Menampilkan metrik utama secara *real-time* seperti Total Pendapatan Hari Ini, Jumlah Transaksi, Total Pegawai, dan Total Varian Produk.
- **Kelola Pegawai (CRUD):** Manajemen akun akses sistem. Admin dapat menambah, mengedit, atau menghapus akun karyawan/kasir.
- **Manajemen Produk (Inventaris):** CRUD data produk (Kopi, Snack, dll) termasuk pembaruan harga dan stok barang.
- **Laporan Penjualan (Riwayat):** Melihat histori transaksi yang telah terjadi secara rinci.

### 🧑‍🍳 Dashboard Karyawan (Operasional)
- **Sistem Kasir (Point of Sale):**
  - Kalkulasi total belanja, diskon, dan kembalian secara otomatis.
  - Pengurangan stok barang secara otomatis setiap transaksi berhasil *(Triggered by Code / ACID Compliant Transaction)*.
  - Auto-generate Nomor Transaksi (Contoh: TR0001, TR0002).
- **Manajemen Member (Pelanggan):** Pendaftaran member baru untuk keperluan loyalitas/diskon.
- **Pencarian Produk Cepat:** Mencari harga dan ketersediaan stok produk untuk melayani pelanggan.
- **Riwayat Transaksi & Cetak Struk:** Memungkinkan karyawan untuk melihat transaksi hari ini dan melakukan **Cetak Ulang Struk** jika pelanggan memintanya.

## 🏗️ Struktur Arsitektur (OOP & DAO Pattern)

Kode sumber (`src/`) dipisahkan dengan sangat rapi berdasarkan fungsinya:

```text
src/
├── Asset/       # Menyimpan gambar, ikon, dan logo UI (logo.png)
├── Database/    # Layer Data Access Object (Koneksi.java, ProdukDAO.java, UserDAO.java)
├── Model/       # Layer Entitas Data / POJO (Produk.java, Member.java, DetailTransaksi.java)
├── UI/          # Layer Presentasi / View (AdminDashboard, Kasir, Formlogin, dll)
└── Utils/       # Layer Bantuan & Utilitas (Session.java untuk login statis, IconHelper.java)
```

**Kelebihan Arsitektur Ini:**
- **Modularitas Tinggi:** Komponen UI (`JFrame`) disematkan (embedded) ke dalam Dashboard utama sebagai panel, sehingga memori lebih hemat *(Single-Window Feel)*.
- **Keamanan Transaksi SQL:** Penyimpanan transaksi menggunakan `Connection.setAutoCommit(false)` dan `rollback()` untuk memastikan tabel `transaksi` dan `detail_transaksi` tersimpan secara atomik (ACID).
- **Keamanan Session:** Penyimpanan data pengguna yang login ditangani menggunakan kelas *Static* pada `Utils.Session`.

## 🛠️ Prasyarat & Instalasi

1. **Java Development Kit (JDK):** Versi 8 atau lebih baru.
2. **Database:** MySQL Server (XAMPP / MAMP / Standalone).
3. **IDE:** Disarankan menggunakan Apache NetBeans (karena file `.form` GUI builder).
4. **Driver JDBC:** Tambahkan `mysql-connector-j` ke dalam *Libraries* project.

**Cara Menjalankan:**
1. Buat database baru di MySQL dengan nama `amera_coffee`.
2. Import file struktur tabel (bisa dilihat di `schema.sql` atau *dump* dari phpMyAdmin).
3. Sesuaikan *port*, *username*, dan *password* database kamu pada file `src/Database/Koneksi.java`.
4. *Build and Run* project melalui NetBeans.

## 📜 Lisensi

Proyek ini dibuat untuk tujuan edukasi dan portofolio akademik.Bebas untuk dipelajari, dimodifikasi dan menggunakan kode ini sebagai referensi pembelajaran Pemrograman Berorientasi Objek dengan Java Swing.
