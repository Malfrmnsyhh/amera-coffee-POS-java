package Model;

import Database.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Database.ProdukDAO;

/**
 * Class untuk Business Logic Detail Transaksi
 * Menangani:
 * - Auto-numbering No Transaksi
 * - Tambah item ke transaksi
 * - Hapus item dari transaksi
 * - Hitung total, diskon, kembalian
 * - Simpan transaksi ke database
 */
public class DetailTransaksi {

  private transaksi transaksiAktif;
  private List<DetailItem> itemList;
  private ProdukDAO produkDAO = new ProdukDAO();
  private String lastError = "";

  public DetailTransaksi() {
    this.transaksiAktif = new transaksi();
    this.itemList = new ArrayList<>();
  }

  /**
   * Generate No Transaksi otomatis
   * Format: TR0001, TR0002, dst
   */
  public String generateNoTransaksi() {
    String sql = "SELECT no_transaksi FROM transaksi ORDER BY no_transaksi DESC LIMIT 1";

    try (Connection c = Koneksi.getKoneksi();
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(sql)) {

      if (!r.next()) {
        return "TR0001";
      }

      String noTransaksi = r.getString("no_transaksi");
      if (noTransaksi == null || noTransaksi.length() < 3 || !noTransaksi.startsWith("TR")) {
        return "TR0001";
      }

      String numericPart = noTransaksi.substring(2);
      int nextNumber = Integer.parseInt(numericPart) + 1;
      return String.format("TR%04d", nextNumber);
    } catch (Exception e) {
      System.err.println("Error in generateNoTransaksi(): " + e);
      e.printStackTrace();
      return "TR0001";
    }
  }

  /**
   * Tambah item ke detail transaksi
   */
  public void tambahItem(String idMenu, String namaMenu, int hargaSatuan, int jumlah) {
    DetailItem item = new DetailItem();
    item.setIdMenu(idMenu);
    item.setNamaMenu(namaMenu);
    item.setHargaSatuan(hargaSatuan);
    item.setJumlah(jumlah);
    item.setSubtotal(hargaSatuan * jumlah);

    itemList.add(item);
  }

  /**
   * Hapus item dari detail transaksi
   */
  public void hapusItem(int indexItem) {
    if (indexItem >= 0 && indexItem < itemList.size()) {
      itemList.remove(indexItem);
    }
  }

  /**
   * Hitung total semua item
   */
  public int hitungTotal() {
    int total = 0;
    for (DetailItem item : itemList) {
      total += item.getSubtotal();
    }
    return total;
  }

  /**
   * Hitung kembalian
   */
  public int hitungKembalian(int totalBayar, int bayar) {
    return bayar - totalBayar;
  }

  /**
   * Simpan transaksi ke database
   */
  public String getLastError() {
    return lastError;
  }

  public boolean simpanTransaksi(String noTransaksi, int kasirId, String namaKasir,
      int memberId, String namaCustomer, int nomorMeja,
      LocalDate tanggal, int total, int bayar) {
    lastError = "";
    Connection c = null;
    try {
      if (!Koneksi.isConnected()) {
        lastError = "Database tidak terhubung. Cek MySQL (port 3308), database amera_coffee, dan file Koneksi.java.";
        return false;
      }

      if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
        lastError = "No transaksi kosong.";
        return false;
      }
      if (itemList == null || itemList.isEmpty()) {
        lastError = "Item transaksi kosong.";
        return false;
      }
      if (tanggal == null) {
        lastError = "Tanggal transaksi kosong.";
        return false;
      }
      if (bayar < total) {
        lastError = "Uang bayar kurang dari total.";
        return false;
      }

      c = Koneksi.getKoneksi();
      c.setAutoCommit(false);
      int kembalian = bayar - total;

      String sqlHeader = "INSERT INTO transaksi (no_transaksi, kasir_id, nama_kasir, " +
          "member_id, nama_customer, nomor_meja, tanggal, total, bayar, kembalian) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

      int transaksiId;
      try (PreparedStatement ps = c.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, noTransaksi);
        if (kasirId > 0) {
          ps.setInt(2, kasirId);
        } else {
          ps.setNull(2, java.sql.Types.INTEGER);
        }
        ps.setString(3, namaKasir);
        if (memberId > 0) {
          ps.setInt(4, memberId);
        } else {
          ps.setNull(4, java.sql.Types.INTEGER);
        }
        ps.setString(5, namaCustomer);
        ps.setInt(6, nomorMeja);
        ps.setDate(7, java.sql.Date.valueOf(tanggal));
        ps.setInt(8, total);
        ps.setInt(9, bayar);
        ps.setInt(10, kembalian);
        ps.executeUpdate();

        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (!keys.next()) {
            throw new SQLException("Gagal mendapatkan ID transaksi baru.");
          }
          transaksiId = keys.getInt(1);
        }
      }

      String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, produk_id, nama_produk, "
          + "harga_satuan, jumlah, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

      try (PreparedStatement psDetail = c.prepareStatement(sqlDetail)) {
        for (DetailItem item : itemList) {
          int produkId = cariProdukId(c, item.getIdMenu());
          psDetail.setInt(1, transaksiId);
          if (produkId > 0) {
            psDetail.setInt(2, produkId);
          } else {
            psDetail.setNull(2, java.sql.Types.INTEGER);
          }
          psDetail.setString(3, item.getNamaMenu());
          psDetail.setInt(4, item.getHargaSatuan());
          psDetail.setInt(5, item.getJumlah());
          psDetail.setInt(6, item.getSubtotal());
          psDetail.executeUpdate();
        }
      }

      for (DetailItem item : itemList) {
        int produkId = cariProdukId(c, item.getIdMenu());
        if (produkId > 0) {
          boolean stokBerhasil = produkDAO.kurangiStok(c, produkId, item.getJumlah());
          if (!stokBerhasil) {
            lastError = "Gagal mengurangi Stok untuk " + item.getNamaMenu();
            throw new SQLException(lastError);
          }
        }
      }

      c.commit();
      return true;
    } catch (Exception e) {
      lastError = buildErrorMessage(e);
      System.err.println("Error in simpanTransaksi(): " + lastError);
      e.printStackTrace();
      if (c != null) {
        try {
          c.rollback();
        } catch (Exception ignored) {
        }
      }
      return false;
    } finally {
      if (c != null) {
        try {
          c.setAutoCommit(true);
        } catch (Exception ignored) {
        }
      }
    }
  }

  private static String buildErrorMessage(Throwable t) {
    if (t == null) {
      return "Terjadi error (exception null).";
    }

    if (t instanceof SQLException) {
      SQLException se = (SQLException) t;
      String msg = se.getMessage();
      if (msg == null || msg.isEmpty()) {
        msg = se.toString();
      }
      return msg + " (SQLState=" + se.getSQLState() + ", ErrorCode=" + se.getErrorCode() + ")";
    }

    String msg = t.getMessage();
    if (msg == null || msg.isEmpty()) {
      msg = t.toString();
    }
    return msg;
  }

  private int cariProdukId(Connection c, String kodeProduk) {
    if (kodeProduk == null || kodeProduk.trim().isEmpty()) {
      return 0;
    }
    String sql = "SELECT id FROM produk WHERE UPPER(kode_produk) = ?";
    try (PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, kodeProduk.trim().toUpperCase());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("id");
        }
      }
    } catch (Exception e) {
      System.err.println("cariProdukId: " + e.getMessage());
    }
    return 0;
  }

  // Getters & Setters
  public transaksi getTransaksiAktif() {
    return transaksiAktif;
  }

  public void setTransaksiAktif(transaksi transaksiAktif) {
    this.transaksiAktif = transaksiAktif;
  }

  public List<DetailItem> getItemList() {
    return itemList;
  }

  public void setItemList(List<DetailItem> itemList) {
    this.itemList = itemList;
  }

  /**
   * Inner class untuk Detail Item
   */
  public static class DetailItem {
    private String idMenu;
    private String namaMenu;
    private int hargaSatuan;
    private int jumlah;
    private int subtotal;

    // Getters & Setters
    public String getIdMenu() {
      return idMenu;
    }

    public void setIdMenu(String idMenu) {
      this.idMenu = idMenu;
    }

    public String getNamaMenu() {
      return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
      this.namaMenu = namaMenu;
    }

    public int getHargaSatuan() {
      return hargaSatuan;
    }

    public void setHargaSatuan(int hargaSatuan) {
      this.hargaSatuan = hargaSatuan;
    }

    public int getJumlah() {
      return jumlah;
    }

    public void setJumlah(int jumlah) {
      this.jumlah = jumlah;
    }

    public int getSubtotal() {
      return subtotal;
    }

    public void setSubtotal(int subtotal) {
      this.subtotal = subtotal;
    }

    @Override
    public String toString() {
      return "DetailItem{" +
          "idMenu='" + idMenu + '\'' +
          ", namaMenu='" + namaMenu + '\'' +
          ", hargaSatuan=" + hargaSatuan +
          ", jumlah=" + jumlah +
          ", subtotal=" + subtotal +
          '}';
    }
  }
}
