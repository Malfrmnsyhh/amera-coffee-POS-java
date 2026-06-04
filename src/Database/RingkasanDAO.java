package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RingkasanDAO {

    public int getTotalPendapatanHariIni() {
        String sql = "SELECT SUM(total) as total_pendapatan FROM transaksi WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_pendapatan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalTransaksiHariIni() {
        String sql = "SELECT COUNT(*) as total_transaksi FROM transaksi WHERE DATE(tanggal) = CURDATE()";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_transaksi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalPegawai() {
        String sql = "SELECT COUNT(*) as total_pegawai FROM users";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_pegawai");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalProduk() {
        String sql = "SELECT COUNT(*) as total_produk FROM produk";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_produk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
