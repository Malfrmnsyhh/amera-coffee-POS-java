package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public class RiwayatDAO {

  // Mengambil daftar transaksi (Tabel Utama)
  public DefaultTableModel getDaftarTransaksi(String keyword, String filterKategori) {
    DefaultTableModel model = new DefaultTableModel(
        new String[] { "ID Transaksi", "No Transaksi", "Kasir", "Customer", "Tanggal", "Total" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    String sql = "SELECT id, no_transaksi, nama_kasir, nama_customer, tanggal, total FROM transaksi ";

    // Logika Filter & Search
    if (keyword != null && !keyword.trim().isEmpty()) {
      if (filterKategori.equals("Tanggal")) {
        sql += "WHERE tanggal LIKE ? ";
      } else if (filterKategori.equals("Total")) {
        sql += "WHERE total LIKE ? ";
      } else {
        sql += "WHERE no_transaksi LIKE ? OR nama_customer LIKE ? ";
      }
    }
    sql += "ORDER BY id DESC";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      if (keyword != null && !keyword.trim().isEmpty()) {
        ps.setString(1, "%" + keyword + "%");
        if (!filterKategori.equals("Tanggal") && !filterKategori.equals("Total")) {
          ps.setString(2, "%" + keyword + "%");
        }
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          model.addRow(new Object[] {
              rs.getInt("id"),
              rs.getString("no_transaksi"),
              rs.getString("nama_kasir"),
              rs.getString("nama_customer"),
              rs.getDate("tanggal"),
              rs.getInt("total")
          });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return model;
  }

  // Mengambil detail produk dari transaksi yang diklik
  public DefaultTableModel getDetailTransaksi(int transaksiId, String keyword, String filterKategori) {
    DefaultTableModel model = new DefaultTableModel(
        new String[] { "Nama Produk", "Harga", "Jumlah", "Subtotal" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    String sql = "SELECT nama_produk, harga_satuan, jumlah, subtotal FROM detail_transaksi WHERE transaksi_id = ? ";

    if (keyword != null && !keyword.trim().isEmpty()) {
      if (filterKategori.equals("Qty")) {
        sql += "AND jumlah LIKE ? ";
      } else if (filterKategori.equals("Subtotal")) {
        sql += "AND subtotal LIKE ? ";
      } else {
        sql += "AND nama_produk LIKE ? ";
      }
    }

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, transaksiId);
      if (keyword != null && !keyword.trim().isEmpty()) {
        ps.setString(2, "%" + keyword + "%");
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          model.addRow(new Object[] {
              rs.getString("nama_produk"),
              rs.getInt("harga_satuan"),
              rs.getInt("jumlah"),
              rs.getInt("subtotal")
          });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return model;
  }
}