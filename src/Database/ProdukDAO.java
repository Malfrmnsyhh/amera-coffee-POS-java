package Database;

import Model.Produk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Akses data tabel produk.
 */
public class ProdukDAO {

  public Produk cariByKode(String kodeProduk) {
    if (kodeProduk == null || kodeProduk.trim().isEmpty()) {
      return null;
    }

    String sql = "SELECT id, kode_produk, nama_produk, harga, stok "
        + "FROM produk WHERE UPPER(kode_produk) = ?";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, kodeProduk.trim().toUpperCase());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Produk(
              rs.getInt("id"),
              rs.getString("kode_produk"),
              rs.getString("nama_produk"),
              rs.getInt("harga"),
              rs.getInt("stok"));
        }
      }
    } catch (Exception e) {
      System.err.println("ProdukDAO.cariByKode: " + e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  public Produk cariById(int idProduk) {
    String sql = "SELECT id, kode_produk, nama_produk, harga, stok FROM produk WHERE id = ?";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idProduk);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Produk(
              rs.getInt("id"),
              rs.getString("kode_produk"),
              rs.getString("nama_produk"),
              rs.getInt("harga"),
              rs.getInt("stok"));
        }
      }
    } catch (Exception e) {
      System.err.println("ProdukDAO.cariById: " + e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  public List<Produk> getAllProduk() {
    List<Produk> list = new ArrayList<>();
    String sql = "SELECT id, kode_produk, nama_produk, harga, stok FROM produk ORDER BY kode_produk ASC";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        list.add(new Produk(
            rs.getInt("id"),
            rs.getString("kode_produk"),
            rs.getString("nama_produk"),
            rs.getInt("harga"),
            rs.getInt("stok")));
      }
    } catch (Exception e) {
      System.err.println("ProdukDAO.getAllProduk: " + e.getMessage());
      e.printStackTrace();
    }

    return list;
  }
}
